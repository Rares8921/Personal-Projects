using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class LikeController : Controller
    {
        public readonly AppContext appContext;

        public LikeController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        public IActionResult Index(string contentType, int contentId, int postId)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            try
            {
                string currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                if (string.IsNullOrEmpty(currentUserId))
                {
                    TempData["Error"] = "User is not authenticated.";
                    return Unauthorized();
                }

                // Sa existe continutul
                if (!ContentExists(contentType, contentId))
                {
                    TempData["Error"] = "Content not found.";
                    return RedirectToAction("Detail", "Post", new { id = postId });
                }

                Like? existingLike = null;

                if (contentType == "post")
                {
                    existingLike = appContext.Likes.FirstOrDefault(l =>
                        l.ContentId == contentId && l.CommentId == null && l.AnswerId == null && l.UserId == currentUserId);
                }
                else if (contentType == "comment")
                {
                    existingLike = appContext.Likes.FirstOrDefault(l =>
                        l.CommentId == contentId && l.ContentId == null && l.AnswerId == null && l.UserId == currentUserId);
                }
                else if (contentType == "answer")
                {
                    existingLike = appContext.Likes.FirstOrDefault(l =>
                        l.AnswerId == contentId && l.ContentId == null && l.CommentId == null && l.UserId == currentUserId);
                }

                // Daca deja exista like-ul, il scot din baza de date
                if (existingLike != null)
                {
                    appContext.Likes.Remove(existingLike);

                    UpdateLikeCounter(contentType, contentId, -1);

                    appContext.SaveChanges();
                    TempData["Message"] = "Like removed successfully!";
                    return RedirectToAction("Detail", "Post", new { id = postId });
                }

                // Daca nu este deja, il adaug in baza de date
                Like newLike = new Like
                {
                    UserId = currentUserId,
                    timeStamp = DateTime.Now
                };

                // Adaugare like pentru postari, comentarii si raspunsuri
                if (contentType == "post")
                {
                    newLike.ContentId = contentId;
                }
                else if (contentType == "comment")
                {
                    newLike.CommentId = contentId;
                }
                else if (contentType == "answer")
                {
                    newLike.AnswerId = contentId;
                }
                else
                {
                    TempData["Error"] = "Unsupported content type.";
                    return RedirectToAction("Detail", "Post", new { id = postId });
                }

                appContext.Likes.Add(newLike);

                UpdateLikeCounter(contentType, contentId, 1);

                // Notific utilizatorul daca are contentType == "post" si LikesPost al utilizatorului este true 

                var post = appContext.Posts.FirstOrDefault(f => f.ContentId == postId);
                if (post != null)
                {
                    var current_User_notificationOp = appContext.NotificationOptions.FirstOrDefault(f => f.UserId == post.UserId);
                    int[] validNumbers = { 1, 10, 50, 100, 500, 1000, 5000, 10000 };
                    if (current_User_notificationOp != null && current_User_notificationOp.LikesPost && validNumbers.Contains(GetPostCounter(postId)))
                    {
                        var NewNotification = new Notification
                        {
                            Type = "Like",
                            SenderId = currentUserId,
                            RecipientId = post.UserId,
                            Context = $"The content: {post.TextContent} got a like!",
                            Timestamp = DateTime.Now
                        };
                        appContext.Notifications.Add(NewNotification);
                    }
                }

                appContext.SaveChanges();

                TempData["Message"] = "Like added successfully!";
                return RedirectToAction("Detail", "Post", new { id = postId });
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                TempData["Error"] = "An unexpected error occurred.";
                return RedirectToAction("Detail", "Post", new { id = postId });
            }
        }

        // Verificare in plus
        private bool ContentExists(string contentType, int contentId)
        {
            switch (contentType)
            {
                case "post":
                    return appContext.Posts.Any(p => p.ContentId == contentId);
                case "comment":
                    return appContext.Comments.Any(c => c.CommentId == contentId);
                case "answer":
                    return appContext.Answers.Any(a => a.AnswerId == contentId);
                default:
                    return false;
            }
        }

        private void UpdateLikeCounter(string contentType, int contentId, int increment)
        {
            switch (contentType)
            {
                case "post":
                    Post? post = appContext.Posts.FirstOrDefault(p => p.ContentId == contentId);
                    if (post != null)
                    {
                        post.LikeCounter += increment;
                    }
                    break;

                case "comment":
                    Comment? comment = appContext.Comments.FirstOrDefault(c => c.CommentId == contentId);
                    if (comment != null)
                    {
                        comment.likeCounter += increment;
                    }
                    break;

                case "answer":
                    Answer? answer = appContext.Answers.FirstOrDefault(a => a.AnswerId == contentId);
                    if (answer != null)
                    {
                        answer.LikeCounter += increment;
                    }
                    break;

                default:
                    throw new ArgumentException("Invalid content type.");
            }
        }

        private int GetPostCounter(int contentId)
        {
            Post? post = appContext.Posts.FirstOrDefault(p => p.ContentId == contentId);
            return post.LikeCounter;
        }
    }
}
