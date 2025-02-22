using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class InboxController : Controller
    {
        private readonly AppContext appContext;
        public InboxController(AppContext _appContext)
        {
            appContext = _appContext;
        }
        public async Task<IActionResult> Index()
        {
            if (User.FindFirstValue(ClaimTypes.NameIdentifier) == null)
            {
                return Unauthorized();
            }

            string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);

            var notifications = await appContext.Notifications.Where(f => f.RecipientId == currentUserId)
                                .OrderByDescending(f => f.Timestamp)
                                .ToListAsync();
            foreach (var notification in notifications)
            {
                notification.IsRead = true; 
            }

            appContext.SaveChanges();

            ViewBag.Notifications = notifications;
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;

            return View();
        }


        //
        [HttpPost]
        public IActionResult AcceptFollowThenDelete(string? notificationId)
        {
            return RedirectToAction();
        }
        //
        [HttpPost]
        public IActionResult AcceptInviteThenDelete(string? notificationId)
        {
            return RedirectToAction();
        }

        [HttpPost]
        public IActionResult DeleteNotification(string notificationId)
        {
            var notification = appContext.Notifications.Find(notificationId);
            if (notification != null)
            {
                appContext.Notifications.Remove(notification);
                appContext.SaveChanges();
            }
            return RedirectToAction("Index", "Inbox");
        }
    }
}
