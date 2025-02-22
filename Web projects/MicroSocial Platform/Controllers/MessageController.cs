using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class MessageController : Controller
    {

        private readonly AppContext appContext;

        public MessageController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        public IActionResult Index()
        {
            // Sa nu pot accesa pagina daca nu sunt logat
            if (User.FindFirstValue(ClaimTypes.NameIdentifier) == null)
            {
                return Unauthorized();
            }
            //ViewBag.ShowRelatives = true;
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            ViewBag.UserId = userId;
            ViewBag.UserName = (appContext.Users.Find(userId)).UserName;
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View("~/Views/Message/ChatMessages.cshtml");
        }

    }
}