using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class ConferenceController : Controller
    {
        private readonly AppContext appContext;

        public ConferenceController(AppContext _appContext) {
            appContext = _appContext;
        }

        public IActionResult Index()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if(userId == null)
            {
                return Unauthorized();
            }
            ViewBag.UserId = userId;
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View("~/Views/Conference/Conference.cshtml");
        }

        public IActionResult VideoCall(string userId, string? roomId)
        {
            var user = appContext.Users.FirstOrDefault(u => u.Id == userId);
            if (user == null)
            {
                return Unauthorized();
            }
            ViewBag.UserId = userId;
            ViewBag.UserName = user.UserName;
            ViewBag.RoomId = roomId ?? "invalid";
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated; 
            return View("~/Views/Conference/VideoCall.cshtml");
        }
    }
} 