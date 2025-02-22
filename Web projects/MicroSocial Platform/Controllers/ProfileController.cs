using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class ProfileController : Controller
    {

        private readonly AppContext appContext;
        private readonly SignInManager<User> signInManager;
        private readonly UserManager<User> userManager;

        public ProfileController(AppContext _appContext, SignInManager<User> _signInManager, UserManager<User> _userManager)
        {
            appContext = _appContext;
            signInManager = _signInManager;
            userManager = _userManager;
        }

        public async Task<IActionResult> Index(string? userId)
        {
            string? currentUserId = userId;
            string? sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                currentUserId = sessionUser;
            }
            // Daca user-ul din sesiunea curenta nu este logat
            if (currentUserId == null)
            {
                return Unauthorized();
            }
            var user = appContext.Users.FirstOrDefault(u => u.Id == currentUserId);
            // Daca nu exista user-ul in baza de date
            if (user == null)
            {
                return NotFound();
            }

            // Aici realizez deserializarea imaginii
            string imageUrl = Convert.ToBase64String(user.ProfilePicture);
            // Aici este metoda standard de transformare a byte[] uri in imagine
            // Exemplu: https://stackoverflow.com/questions/31822820/convert-image-data-uri-to-image-php
            ViewBag.ProfilePicture = $"data:image/png;base64,{imageUrl}";
            ViewBag.IsLoggedIn = (sessionUser != null);
            ViewBag.CurrentUserIsAdmin = (await userManager.GetRolesAsync(user)).Contains("Admin");


            ViewBag.IsCurrentUser = currentUserId == sessionUser;

            ViewBag.AlreadyFollowed = appContext.Followers.Any(f => f.FollowerUserId == sessionUser && f.FollowedUserId == currentUserId);
            ViewBag.Followers = await appContext.Users
                                .Where(u => appContext.Followers
                                    .Any(f => f.FollowerUserId == u.Id && f.FollowedUserId == currentUserId)).ToListAsync();
            ViewBag.Followed = await appContext.Users
                                .Where(u => appContext.Followers
                                    .Any(f => f.FollowerUserId == currentUserId && f.FollowedUserId == u.Id)).ToListAsync();

            Dictionary<string, string> followersProfilePictures = new Dictionary<string, string>();
            Dictionary<string, string> followedProfilePictures = new Dictionary<string, string>();

            foreach (var follower in ViewBag.Followers)
            {
                string ImageUrl = Convert.ToBase64String(follower.ProfilePicture);
                string base64Image = $"data:image/png;base64,{ImageUrl}";

                followersProfilePictures[follower.Id] = base64Image;
            }

            foreach (var follower in ViewBag.Followed)
            {
                string ImageUrl = Convert.ToBase64String(follower.ProfilePicture);
                string base64Image = $"data:image/png;base64,{ImageUrl}";

                followedProfilePictures[follower.Id] = base64Image;
            }
            ViewBag.FollowersProfilePictures = followersProfilePictures;
            ViewBag.FollowedProfilePictures = followedProfilePictures;
            ViewBag.Stories = await appContext.Stories.Where(u => u.UserId == currentUserId).ToListAsync();

            return View("Profile", user);
        }

        public IActionResult Edit(string? userId)
        {
            string? currentUserId = userId ?? User.FindFirstValue(ClaimTypes.NameIdentifier);
            string? sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);
            // Daca user-ul din sesiunea curenta nu este logat
            if (currentUserId == null)
            {
                return Unauthorized();
            }
            var user = appContext.Users.FirstOrDefault(u => u.Id == currentUserId);
            // Daca nu exista user-ul in baza de date
            if (user == null)
            {
                return NotFound();
            } else
            {
                appContext.Entry(user).Collection(u => u.Posts).Load();
            }

            // Aici realizez deserializarea imaginii
            string imageUrl = Convert.ToBase64String(user.ProfilePicture);
            // Aici este metoda standard de transformare a byte[] uri in imagine
            // Exemplu: https://stackoverflow.com/questions/31822820/convert-image-data-uri-to-image-php
            ViewBag.ProfilePicture = $"data:image/png;base64,{imageUrl}";

            ViewBag.IsCurrentUser = currentUserId == sessionUser;
            ViewBag.IsLoggedIn = (sessionUser != null);

            return View(user);
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> EditProfile(IFormFile? ProfilePicture, [Bind("UserName,Biography,PrivateAccount")] User updatedUser)
        {

            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            var currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var user = await appContext.Users.FindAsync(currentUserId);

            if (user == null)
            {
                return NotFound();
            }

            // Aici incerc sa schimb numele si verific sa nu fie nil
            if (!string.IsNullOrEmpty(updatedUser.UserName) && updatedUser.UserName != user.UserName)
            {
                var isUsernameTaken = await appContext.Users.AnyAsync(u => u.UserName == updatedUser.UserName);
                if (isUsernameTaken)
                {
                    ModelState.AddModelError("UserName", "The username is already taken.");
                } else
                {
                    user.UserName = updatedUser.UserName;
                }
            }

            if (!string.IsNullOrEmpty(updatedUser.Biography) && updatedUser.Biography != user.Biography)
            {
                user.Biography = updatedUser.Biography;
            }
            else if (string.IsNullOrEmpty(updatedUser.Biography) && user.Biography != null)
            {
                ModelState.AddModelError("Biography", "Biography cannot be empty.");
            }

            user.PrivateAccount = Request.Form["PrivateAccountCheck"] == "on";

            // Handle profile picture if uploaded
            if (ProfilePicture != null)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await ProfilePicture.CopyToAsync(memoryStream);
                    user.ProfilePicture = memoryStream.ToArray();
                }
            } else
            {
                ViewBag.ProfilePicture = user.ProfilePicture;
            }

            if (!ModelState.IsValid)
            {
                ModelState.AddModelError("Email", "An error occured.");
                return View("Edit", user);
            }

            // Save changes to the database
            await appContext.SaveChangesAsync();

            return RedirectToAction("Index", "Profile");
        }

        [HttpPost]
        public async Task<IActionResult> DeleteAccount(string userId)
        {
            if (string.IsNullOrEmpty(userId))
            {
                return BadRequest("UserId cannot be empty.");
            }

            var user = await appContext.Users.FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null)
            {
                return NotFound();
            }

            var answers = await appContext.Answers.Where(c => c.UserId == userId).ToListAsync();
            appContext.Answers.RemoveRange(answers);

            var comments = await appContext.Comments.Where(c => c.UserId == userId).ToListAsync();
            appContext.Comments.RemoveRange(comments);

            var posts = await appContext.Posts.Where(p => p.UserId == userId).ToListAsync();
            appContext.Posts.RemoveRange(posts);

            var postsSaved = await appContext.PostsSaved.Where(p => p.UserId == userId).ToListAsync();
            appContext.PostsSaved.RemoveRange(postsSaved);

            var stories = await appContext.Stories.Where(p => p.UserId == userId).ToListAsync();
            appContext.Stories.RemoveRange(stories);

            var savedPosts = await appContext.PostsSaved.Where(ps => ps.UserId == userId).ToListAsync();
            appContext.PostsSaved.RemoveRange(savedPosts);

            var likes = await appContext.Likes.Where(l => l.UserId == userId).ToListAsync();
            appContext.Likes.RemoveRange(likes);

            var followRequests = await appContext.FollowEngines.Where(fe => fe.User1 == userId || fe.User2 == userId).ToListAsync();
            appContext.FollowEngines.RemoveRange(followRequests);

            var follows = await appContext.Followers.Where(fe => fe.FollowerUserId == userId || fe.FollowedUserId == userId).ToListAsync();
            appContext.Followers.RemoveRange(follows);

            var notifications = await appContext.Notifications.Where(n => n.SenderId == userId || n.RecipientId == userId).ToListAsync();
            appContext.Notifications.RemoveRange(notifications);

            var notificationOptions = await appContext.NotificationOptions.Where(n => n.UserId == userId).ToListAsync();
            appContext.NotificationOptions.RemoveRange(notificationOptions);

            var joinRequests = await appContext.JoinRequests.Where(jr => jr.UserId == userId).ToListAsync();
            appContext.JoinRequests.RemoveRange(joinRequests);

            var chatrooms = await appContext.Chatrooms.Where(cr => cr.RecipientId == userId || cr.SenderId == userId).ToListAsync();
            appContext.Chatrooms.RemoveRange(chatrooms);

            var chatMessages = await appContext.ChatMessages.Where(cr => cr.RecipientId == userId || cr.SenderId == userId).ToListAsync();
            appContext.ChatMessages.RemoveRange(chatMessages);
            
            var chatroomParticipants = await appContext.ChatroomParticipants.Where(cr => cr.UserId == userId).ToListAsync();
            appContext.ChatroomParticipants.RemoveRange(chatroomParticipants);

            var groupChatParticipants = await appContext.GroupChatParticipants.Where(cr => cr.UserId == userId).ToListAsync();
            appContext.GroupChatParticipants.RemoveRange(groupChatParticipants);

            var groupChatMessages = await appContext.GroupChatMessages.Where(cr => cr.SenderId == userId).ToListAsync();
            appContext.GroupChatMessages.RemoveRange(groupChatMessages);

            var reports = await appContext.Reports.Where(cr => cr.UserId == userId).ToListAsync();
            appContext.Reports.RemoveRange(reports);

            appContext.Users.Remove(user);
            await appContext.SaveChangesAsync();

            var currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (currentUserId == userId)
            {
                await signInManager.SignOutAsync();
            }
            return RedirectToAction("Index", "Home");
        }

    }

}