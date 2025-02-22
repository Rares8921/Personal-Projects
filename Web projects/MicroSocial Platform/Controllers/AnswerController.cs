using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Hosting;
using Microsoft.ML;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class AnswerController : Controller
    {
        private readonly AppContext appContext;
        public AnswerController(AppContext _appContext) { 
            appContext = _appContext;
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Add(int commentId, string content, int contentId, int? parentAnswerId)
        {
            if (string.IsNullOrWhiteSpace(content))
            {
                ModelState.AddModelError("content", "Reply content cannot be empty.");
                return RedirectToAction("Detail", "Post", new { id = commentId });
            }

            string? currentUserId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if(currentUserId == null)
            {
                return Unauthorized();
            }

            var answer = new Answer
            {
                Content = content,
                CommentId = commentId,
                UserId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value,
                User = appContext.Users.FirstOrDefault(u => u.Id == currentUserId),
                Comment = appContext.Comments.FirstOrDefault(c => c.CommentId == commentId),
                TimeStamp = DateTime.Now,
                ParentAnswerId = parentAnswerId
            };

            appContext.Answers.Add(answer);
            await appContext.SaveChangesAsync();

            return RedirectToAction("Detail", "Post", new { id = contentId });
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Edit(int answerId, string content)
        {
            var answer = appContext.Answers.FirstOrDefault(a => a.AnswerId == answerId);
            if (answer == null)
            {
                return NotFound();
            }

            answer.Content = content;
            appContext.SaveChanges();

            var post = appContext.Posts.Include(p => p.PostComments)
                              .ThenInclude(c => c.Replies)
                              .FirstOrDefault(p => p.PostComments.Any(c => c.Replies.Contains(answer)));

            if (post == null)
            {
                return NotFound();
            }

            return RedirectToAction("Detail", "Post", new { id = post.ContentId });

        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Delete(int answerId, string typeOfAnswer)
        {
            var answer = appContext.Answers.Include(a => a.Replies).FirstOrDefault(a => a.AnswerId == answerId);

            if (answer == null)
            {
                return NotFound();
            }

            var comment = appContext.Comments.Include(c => c.Replies).FirstOrDefault(c => c.Replies.Contains(answer));
            var parentAnswer = appContext.Answers.Include(a => a.Replies).FirstOrDefault(a => a.Replies.Contains(answer));

            Post? post = null;
            // Daca e raspuns la comentariu
            if (typeOfAnswer == "commentAnswer" && comment != null)
            {
                comment.Replies.Remove(answer);

                post = appContext.Posts.Include(p => p.PostComments).ThenInclude(c => c.Replies)
                                          .FirstOrDefault(p => p.PostComments.Contains(comment));
            }
            // Daca e raspuns la raspuns
            else if (parentAnswer != null)
            {
                parentAnswer.Replies.Remove(answer);

                post = appContext.Posts.Include(p => p.PostComments).ThenInclude(c => c.Replies)
                                          .FirstOrDefault(p => p.PostComments.Any(c => c.Replies.Contains(parentAnswer)));
            }

            if (post != null)
            {

                DeleteAnswerRecursively(answer);

                appContext.SaveChanges();
                return RedirectToAction("Detail", "Post", new { id = post.ContentId });
            }

            return NotFound();
        }

        public void DeleteAnswerRecursively(Answer answer)
        {
            var likesToDelete = appContext.Likes.Where(l => l.AnswerId == answer.AnswerId).ToList();
            appContext.Likes.RemoveRange(likesToDelete);

            var postLikesToDelete = appContext.Likes.Where(l => l.CommentId == answer.Comment.CommentId).ToList();
            appContext.Likes.RemoveRange(postLikesToDelete);

            if (answer.ParentAnswerId.HasValue)
            {
                var replyLikesToDelete = appContext.Likes.Where(l => l.AnswerId == answer.ParentAnswerId).ToList();
                appContext.Likes.RemoveRange(replyLikesToDelete);
            }

            // Sterg pentru copii si dupa sterg parintele
            foreach (var reply in answer.Replies.ToList())
            {
                DeleteAnswerRecursively(reply); 
            }

            appContext.Answers.Remove(answer);
        }
    }
}
