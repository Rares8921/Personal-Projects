using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform.Controllers
{
    public class UsersController : Controller
    {

        private readonly AppContext database;
        public UsersController(AppContext _database)
        {
            database = _database;
        }
        public IActionResult Index()
        {
            return View();
        }

        public string searchForUser(string userName)
        {
            var result = from Users in database.Users select new {UserName = userName};
            return "Merge";
        }
    }
}
