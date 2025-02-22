using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using NuGet.Versioning;
using System.Security.Claims;
using System.Text;

namespace MicroSocial_Platform
{
    public class StoryController : Controller
    {
        private readonly AppContext appContext;
        public StoryController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        public IActionResult Create()
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View("~/Views/Profile/Story_Create.cshtml");
        }

        [HttpPost]
        public IActionResult Create(string Title, IFormFile MediaContent, string mimeType)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            using (var memoryStream = new MemoryStream())
            {
                MediaContent.CopyTo(memoryStream);
                byte[] mediaBytes = memoryStream.ToArray();

                var new_story = new Story
                {
                    UserId = sessionUserId,
                    TimeStamp = DateTime.Now,
                    User = appContext.Users.Find(sessionUserId),
                    Name = Title,
                    MediaLinks = mediaBytes,
                    MimeType = mimeType
                };

                appContext.Stories.Add(new_story);
                appContext.SaveChanges();
            }

            return RedirectToAction("Index", "Profile");
        }

        public IActionResult Index(int id)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            var story = appContext.Stories.FirstOrDefault(s => s.ContentId == id);
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (story == null || userId == null)
            {
                return NotFound();
            }
            var user = appContext.Users.Find(story.UserId);
            if (user == null)
            {
                return NotFound();
            }

            // Deserializarea imaginii
            string imageUrl = Convert.ToBase64String(user.ProfilePicture);
            ViewBag.ProfilePicture = $"data:image/png;base64,{imageUrl}";
            ViewBag.Username = user.UserName;
            ViewBag.Story_title = story.Name;
            ViewBag.ContentId = story.ContentId;

            string mediaUrl = Convert.ToBase64String(story.MediaLinks);
            ViewBag.Story_media = $"data:image/png;base64,{mediaUrl}";
            ViewBag.Story_mimeType = story.MimeType;

            ViewBag.IsSameUser = story.UserId == userId;

            return View("~/Views/Profile/Story.cshtml", ViewBag);
        }

        public IActionResult Delete(int id)
        {
            var story = appContext.Stories.FirstOrDefault(s => s.ContentId == id);

            if (story != null)
            {
                appContext.Stories.Remove(story);
                appContext.SaveChanges();
            }
            return RedirectToAction("Index", "Profile");
        }
    }
}