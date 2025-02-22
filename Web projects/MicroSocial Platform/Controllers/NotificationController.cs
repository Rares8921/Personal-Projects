using Microsoft.AspNetCore.Mvc;

namespace SocialPlatform.Controllers
{
    public class NotificationController : Controller
    {
        public IActionResult Index()
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View();
        }

        [HttpPost]
        public IActionResult Delete(string id)
        {
            return RedirectToAction("Index", "Inbox");
        }

    }
}