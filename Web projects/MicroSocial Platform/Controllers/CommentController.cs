using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class CommentController : Controller
    {
        public readonly AppContext appContext;
        public CommentController(AppContext _appContext) { 
            appContext = _appContext;
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Add(int postId, string content)
        {
            var post = await appContext.Posts.Include(p => p.PostComments).FirstOrDefaultAsync(p => p.ContentId == postId);
            if (post == null)
            {
                return NotFound();
            }
            var userOptions = await appContext.NotificationOptions.FirstOrDefaultAsync(o => o.UserId == post.UserId);
            if (userOptions != null && !userOptions.CommentsPost)
            {
                ModelState.AddModelError("CommentsPost", "Comments are disabled for this user's posts.");
                return RedirectToAction("Detail", "Post", new { id = postId });
            }
            if (string.IsNullOrEmpty(content))
            {
                ModelState.AddModelError("content", "Comment content cannot be empty.");
                return RedirectToAction("Detail", "Post", new { id = postId });
            }
            var newComment = new Comment
            {
                TimeStamp = DateTime.Now,
                UserId = User.FindFirst(ClaimTypes.NameIdentifier).Value,
                User = await appContext.Users.FindAsync(User.FindFirst(ClaimTypes.NameIdentifier).Value),
                likeCounter = 0,
                StringContent = content
            };

            // Notific utilizatorul daca are CommentPost al utilizatorului este true

            var That_Content = appContext.Posts.FirstOrDefault(f => f.ContentId == postId);
            var current_User_notificationOp = appContext.NotificationOptions.FirstOrDefault(f => f.UserId == That_Content.User.Id);
            if (current_User_notificationOp != null && current_User_notificationOp.CommentsPost)
            {
                var NewNotification = new Notification
                {
                    Type = "Comment",
                    SenderId = User.FindFirst(ClaimTypes.NameIdentifier).Value,
                    RecipientId = That_Content.User.Id,
                    Context = $"Your post received a comment.",
                    Timestamp = DateTime.Now
                };
                appContext.Notifications.Add(NewNotification);
            }

            Console.WriteLine($"New comment string content: {newComment.StringContent}");

            post.PostComments.Add(newComment);
            appContext.Comments.Add(newComment);
            await appContext.SaveChangesAsync();

            return RedirectToAction("Detail", "Post", new { id = post.ContentId });
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, int commentId, string content)
        {
            if (string.IsNullOrWhiteSpace(content))
            {
                ModelState.AddModelError("content", "Comment content cannot be empty.");
                return RedirectToAction("Detail", "Post", new { id });
            }

            var comment = await appContext.Comments.Include(c => c.User).FirstOrDefaultAsync(c => c.CommentId == commentId);

            if (comment == null)
            {
                return NotFound();
            }

            // daca user-ul curent nu este cel care a postat comentariul
            var currentUserId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var isAdmin = User.IsInRole("Admin");

            if (comment.UserId != currentUserId && !isAdmin)
            {
                return Unauthorized();
            }

            comment.StringContent = content;
            appContext.Comments.Update(comment);
            await appContext.SaveChangesAsync();

            return RedirectToAction("Detail", "Post", new { id });
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Delete(int id, int postId)
        {
            var comment = appContext.Comments.FirstOrDefault(c => c.CommentId == id);
            if(comment == null)
            {
                return NotFound();
            }

            // daca user-ul curent nu este cel care a postat comentariul
            var currentUserId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var isAdmin = User.IsInRole("Admin");
            if (comment.UserId != currentUserId && !isAdmin)
            {
                return Unauthorized();
            }

            var likes = appContext.Likes.Where(l => l.CommentId == id);
            appContext.Likes.RemoveRange(likes);

            var post = await appContext.Posts.FirstOrDefaultAsync(p => p.ContentId == postId);
            if (post != null)
            {
                post.PostComments.Remove(comment);
            } else
            {
                return NotFound();
            }

            appContext.Comments.Remove(comment);
            await appContext.SaveChangesAsync();

            return RedirectToAction("Detail", "Post", new { id = post.ContentId });
        }

    }
}