using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Diagnostics;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly AppContext appContext;

        public HomeController(ILogger<HomeController> logger, AppContext _appContext)
        {
            _logger = logger;
            appContext = _appContext;
        }

        public IActionResult Index()
        {
            bool isValid = false;
            ViewBag.ShowRelatives = true;
            try
            {
                isValid = User.Identity.IsAuthenticated;
            } catch (Exception e)
            {
                //pass
            }
            ViewBag.IsLoggedIn = isValid;
            // Daca user-ul este logat, extrag username-ul
            if(isValid)
            {
                string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                var user = appContext.Users.FirstOrDefault(u => u.Id == currentUserId);
                string imageUrl = Convert.ToBase64String(user.ProfilePicture);
                ViewBag.UserName = user.UserName;
                // Aici este metoda standard de transformare a byte[] uri in imagine
                // Exemplu: https://stackoverflow.com/questions/31822820/convert-image-data-uri-to-image-php
                ViewBag.ProfilePicture = $"data:image/png;base64,{imageUrl}";

                // Selectez notificarile noi
                var newNotificationsCount = appContext.Notifications.Count(n => n.RecipientId == currentUserId && !n.IsRead);
                ViewBag.NewNotificationsCount = newNotificationsCount;

                // Selectez story-urile aferente
                var followedUsers = appContext.Followers.Where(f => f.FollowerUserId == currentUserId).Select(f => f.FollowedUserId).ToList();

                var stories = appContext.Stories.Where(s => followedUsers.Contains(s.UserId)).OrderByDescending(s => s.TimeStamp).ToList();

                ViewBag.Stories = stories;

                var peopleIFollow = appContext.Followers
                                        .Where(f => f.FollowerUserId == currentUserId)
                                        .Select(f => f.FollowedUserId)
                                        .ToList();

                var recommendedUserIds = appContext.Followers
                                            .Where(f => peopleIFollow.Contains(f.FollowerUserId) &&
                                                        f.FollowedUserId != currentUserId &&
                                                        !peopleIFollow.Contains(f.FollowedUserId))
                                            .Select(f => f.FollowedUserId)
                                            .Take(5)
                                            .ToList();

                var recommendedUsers = appContext.Users
                                            .Where(u => recommendedUserIds.Contains(u.Id))
                                            .ToList();

                ViewBag.FollowRecomand = recommendedUsers;
            }
            return View(ViewBag);
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
