using Microsoft.AspNetCore.Mvc;

namespace SocialPlatform.Controllers
{
    public class SavedPostController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Save(int id)
        {
            return View();
        }

        public IActionResult Unsave(int id)
        {
            return View();
        }

    }
}