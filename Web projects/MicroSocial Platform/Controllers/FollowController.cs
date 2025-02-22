using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class FollowController : Controller
    {
        private readonly AppContext appContext;
        public FollowController(AppContext _appContext)
        {
            appContext = _appContext;
        }


        // functionalitatea butonului de follow
        [HttpPost]
        public IActionResult FollowProfile(string userId)
        {
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);

            var sessionUser = appContext.Users.Find(sessionUserId);
            var currentUser = appContext.Users.Find(userId);


            if (currentUser.PrivateAccount) // trebuie sa trimita cerere
            {
                var existingRequest = appContext.FollowEngines.FirstOrDefault(fe => fe.User1 == sessionUserId && fe.User2 == userId);

                if (existingRequest != null)
                {
                    return RedirectToAction("Index", "Profile", userId);
                }

                var followEngine = new FollowEngine
                {
                    User1 = sessionUserId,
                    User2 = userId,
                    Timestamp = DateTime.Now
                };

                var notification = new Notification
                {
                    Type = "FollowRequest",
                    SenderId = sessionUserId,
                    RecipientId = userId,
                    Context = $"{sessionUser.UserName} sent a follow request.",
                    Timestamp = DateTime.Now
                };

                appContext.Notifications.Add(notification);

                appContext.FollowEngines.Add(followEngine);
            }
            else
            {  // nu trebuie sa trimita cerere
                var existingFollower = appContext.Followers.FirstOrDefault(f => f.FollowerUserId == sessionUserId && f.FollowedUserId == userId);

                if (existingFollower == null)
                {
                    var follower = new Follower
                    {
                        FollowerUserId = sessionUserId,
                        FollowerUser = sessionUser,
                        FollowedUserId = userId,
                        FollowedUser = currentUser,
                        TimeStamp = DateTime.Now
                    };

                    appContext.Followers.Add(follower);
                }
                else
                {
                    return RedirectToAction("Index", "Profile", userId);
                }

                var notificationOptions = appContext.NotificationOptions.FirstOrDefault(n => n.UserId == userId);
                if (notificationOptions != null && notificationOptions.NewFollowers)
                {
                    var NewNotification = new Notification
                    {
                        Type = "Follow",
                        SenderId = sessionUserId,
                        RecipientId = userId,
                        Context = $"{sessionUser.UserName} has started following you.",
                        Timestamp = DateTime.Now
                    };

                    appContext.Notifications.Add(NewNotification);
                }

            }
            appContext.SaveChanges();
            return RedirectToAction("Index", "Profile", userId);
        }

        [HttpPost]
        public IActionResult UnFollowProfile(string userId)
        {
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);

            var follower = appContext.Followers.FirstOrDefault(f => f.FollowerUserId == sessionUserId && f.FollowedUserId == userId);

            if (follower != null)
            {
                appContext.Followers.Remove(follower);
                appContext.SaveChanges();
            }

            return RedirectToAction("Index", "Profile", userId);
        }

        [HttpPost]
        public IActionResult RemoveProfile(string userId)
        {
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);

            var follower = appContext.Followers.FirstOrDefault(f => f.FollowerUserId == userId && f.FollowedUserId == sessionUserId);

            if (follower != null)
            {
                appContext.Followers.Remove(follower);
                appContext.SaveChanges();
            }

            return RedirectToAction("Index", "Profile");
        }

        [HttpPost]
        public async Task<IActionResult> AcceptFollowRequest(string userId)
        {
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var sessionUser = await appContext.Users.FindAsync(sessionUserId);

            var followRequest = await appContext.FollowEngines
                .FirstOrDefaultAsync(fe => fe.User1 == userId && fe.User2 == sessionUserId);

            if (followRequest != null && userId != null && sessionUserId != null && sessionUser != null)
            {
                // Daca exista deja followerul
                var existingFollower = await appContext.Followers.FirstOrDefaultAsync(f => f.FollowerUserId == userId && f.FollowedUserId == sessionUserId);

                if (existingFollower == null) 
                {
                    var follower = new Follower
                    {
                        FollowerUserId = userId,
                        FollowedUserId = sessionUserId,
                        TimeStamp = DateTime.Now,
                        FollowedUser = await appContext.Users.FirstOrDefaultAsync(u => u.Id == sessionUserId),
                        FollowerUser = await appContext.Users.FirstOrDefaultAsync(u => u.Id == userId)
                    };

                    appContext.Followers.Add(follower);
                }

                appContext.FollowEngines.Remove(followRequest);

                var notification = await appContext.Notifications.FirstOrDefaultAsync(n => n.SenderId == userId && n.RecipientId == sessionUserId && n.Type == "FollowRequest");
                if (notification != null)
                {
                    appContext.Notifications.Remove(notification);
                }

                var acceptNotification = new Notification
                {
                    Type = "FollowAccepted",
                    SenderId = sessionUserId,
                    RecipientId = userId,
                    Context = $"{sessionUser.UserName} has accepted your follow request.",
                    Timestamp = DateTime.Now
                };
                appContext.Notifications.Add(acceptNotification);

                await appContext.SaveChangesAsync();
            }

            return RedirectToAction("Index", "Profile", userId);
        }

        [HttpPost]
        public async Task<IActionResult> RejectFollowRequest(string userId)
        {
            string? sessionUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var sessionUser = await appContext.Users.FindAsync(sessionUserId);

            var followRequest = await appContext.FollowEngines
                .FirstOrDefaultAsync(fe => fe.User1 == userId && fe.User2 == sessionUserId);

            if (followRequest != null && userId != null && sessionUserId != null && sessionUser != null)
            {
                appContext.FollowEngines.Remove(followRequest);

                var notification = await appContext.Notifications.FirstOrDefaultAsync(n => n.SenderId == userId && n.RecipientId == sessionUserId && n.Type == "FollowRequest");
                if (notification != null)
                {
                    appContext.Notifications.Remove(notification);
                }

                var rejectNotification = new Notification
                {
                    Type = "FollowRejected",
                    SenderId = sessionUserId,
                    RecipientId = userId,
                    Context = $"{sessionUser.UserName} has rejected your follow request.",
                    Timestamp = DateTime.Now
                };
                appContext.Notifications.Add(rejectNotification);

                appContext.SaveChanges();
            }

            return RedirectToAction("Index", "Profile", userId);
        }
    }
}