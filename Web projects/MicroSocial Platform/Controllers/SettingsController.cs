using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class SettingsController : Controller
    {
        private readonly AppContext appContext;
        public SettingsController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        public IActionResult Edit()
        {
            if (User.FindFirstValue(ClaimTypes.NameIdentifier) == null)
            {
                return Unauthorized();
            }

            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            ViewBag.UserId = currentUserId;

            NotificationOpt? userOptions = appContext.NotificationOptions.FirstOrDefault(f => f.UserId == currentUserId);

            if (userOptions == null)
            {
                NotificationOpt newUserOptions = new NotificationOpt();
                newUserOptions.UserId = currentUserId;
                newUserOptions.LD_Mode = true;
                newUserOptions.ChatMessages = true;
                newUserOptions.LikesPost = true;
                newUserOptions.CommentsPost = true;
                newUserOptions.NewFollowers = true;

                appContext.NotificationOptions.Add(newUserOptions);
                appContext.SaveChanges();

                ViewBag.NotificationOptions = newUserOptions;
            }
            else
            {
                ViewBag.NotificationOptions = userOptions;
            }
            return View();
        }

        [HttpPost]
        public IActionResult Edit(string LD_Mode, string ChatMessages, string LikesPost, string CommentsPost, string NewFollowers)
        {
            if (User.FindFirstValue(ClaimTypes.NameIdentifier) == null)
            {
                return Unauthorized();
            }

            string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            NotificationOpt? user = appContext.NotificationOptions.FirstOrDefault(u => u.UserId == currentUserId);

            if (user == null)
            {
                return BadRequest("Notification settings for the current user could not be found.");
            }

            try
            {
                user.LD_Mode = LD_Mode == "on";
                user.ChatMessages = ChatMessages == "on";
                user.LikesPost = LikesPost == "on";
                user.CommentsPost = CommentsPost == "on";
                user.NewFollowers = NewFollowers == "on";
                appContext.SaveChanges();

                return RedirectToAction("Edit");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error updating notification settings: {ex.Message}");
                return RedirectToAction("Edit");
            }
        }
    }
}
