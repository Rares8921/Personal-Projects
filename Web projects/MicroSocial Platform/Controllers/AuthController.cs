using MicroSocial_Platform.Areas.Identity.Pages.Account;
using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Net.Http.Headers;
using System.Security.Claims;
using System.Text.Json;

namespace MicroSocial_Platform.Controllers
{
    public class AuthController : Controller
    {

        // De userManager si context am nevoie pentru a gestiona inregistariile 
        private readonly UserManager<User> userManager;  
        private readonly AppContext context;
        // De signmanager am nevoie pentru logout-uri securizate
        private readonly SignInManager<User> signInManager;

        public AuthController(UserManager<User> _userManager, AppContext _context, SignInManager<User> _signInManager)
        {
            userManager = _userManager;
            context = _context;
            signInManager = _signInManager;
        }


        public IActionResult Login()
        {
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Register(RegisterViewModel model)
        {
            if (!ModelState.IsValid && model.Password != null && model.ConfirmPassword != null)
            {
                return View(model);
            }

            var user = new User
            {
                UserName = model.UserName,
                Email = model.Email,
                Biography = string.Empty,
                AccountCreation = DateTime.Now,
                DateOfBirth = model.DateOfBirth,
                ProfilePicture = [],
                PrivateAccount = false
            };

            var result = await userManager.CreateAsync(user, model.Password);

            if (result.Succeeded)
            {
                return RedirectToAction("CompleteProfile", "Auth");
            }

            return View(model);
        }

        public IActionResult RedirectToRegister(string userName, string email, DateTime dateOfBirth, string profilePicture)
        {
            TempData["UserName"] = userName;
            TempData["Email"] = email;
            TempData["DateOfBirth"] = dateOfBirth.ToString("yyyy-MM-dd");
            TempData["ProfilePicture"] = profilePicture;

            return Redirect("/Identity/Account/Register");
        }

        // Pentru atacuri CSFR (cross-site forgery requests) ca sa fim siguri ca nu sunt logouturi nedorite din surse externe
        // Info aici: https://stackoverflow.com/questions/38555299/anti-forgery-better-understanding-how-it-works
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Logout()
        {
            await signInManager.SignOutAsync();
            return Redirect("/Identity/Account/Login"); // Redirect catre login
        }

        [HttpPost]
        public async Task<IActionResult> CompleteProfile(UserProfileFormViewModel model)
        {
            if (!ModelState.IsValid)
                return View(model);

            var user = await userManager.GetUserAsync(User);
            if (user == null)
                return Redirect("/Identity/Account/Login");

            if (context.Users.Any(u => u.UserName == model.UserName))
            {
                ModelState.AddModelError("UserName", "Username already taken!");
                return View(model);
            }

            user.UserName = model.UserName;
            user.Biography = model.Bio;
            user.PrivateAccount = model.PrivateAccount;
            user.DateOfBirth = model.DateOfBirth;

            if (model.ProfilePicture != null)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await model.ProfilePicture.CopyToAsync(memoryStream);
                    user.ProfilePicture = memoryStream.ToArray();
                }
            }

            context.Update(user);
            // Commit asincron
            await context.SaveChangesAsync();

            return RedirectToAction("Index", "Home");
        }

        public async Task<IActionResult> ConfirmEmail(string userId, string token)
        {
            if (userId == null || token == null)
            {
                return RedirectToAction("Index", "Home");
            }

            var user = await userManager.FindByIdAsync(userId);
            if (user == null)
            {
                return NotFound();
            }

            var result = await userManager.ConfirmEmailAsync(user, token);
            if (result.Succeeded)
            {
                var notificationOptions = new NotificationOpt
                {
                    UserId = user.Id,
                    ChatMessages = true,
                    LikesPost = true,
                    CommentsPost = true,
                    NewFollowers = true
                };
                context.NotificationOptions.Add(notificationOptions);
                await context.SaveChangesAsync();
                return Redirect("/Identity/Account/ConfirmEmail");
            }

            return View("Error");
        }

        [HttpGet]
        public IActionResult LoginWithGoogle(string returnUrl = null)
        {
            var redirectUrl = Url.Action("GoogleResponse", "Auth", new { returnUrl });
            var properties = signInManager.ConfigureExternalAuthenticationProperties("Google", redirectUrl);
            return Challenge(properties, "Google");
        }

        public async Task<IActionResult> GoogleResponse(string returnUrl = null)
        {
            var info = await signInManager.GetExternalLoginInfoAsync();
            if (info == null)
            {
                return RedirectToAction(nameof(Login));
            }

            var email = info.Principal.FindFirstValue(ClaimTypes.Email);
            var userName = info.Principal.FindFirstValue(ClaimTypes.Name);
            var profilePicture = info.Principal.FindFirstValue("picture") ?? "https://i.sstatic.net/34AD2.jpg";

            var dateOfBirth = await GetBirthdateFromGoogle(info);

            var user = await userManager.FindByEmailAsync(email);
            if (user == null)
            {
                return RedirectToAction("RedirectToRegister", "Auth", new { userName, email, dateOfBirth, profilePicture });
            }

            await signInManager.SignInAsync(user, isPersistent: false);
            return Redirect(returnUrl ?? "~/");
        }

        private async Task<byte[]> GetProfilePicture(string url)
        {
            if (string.IsNullOrEmpty(url) || !Uri.IsWellFormedUriString(url, UriKind.Absolute))
            {
                return null;
            }
            using (var httpClient = new HttpClient())
            {
                var response = await httpClient.GetAsync(url);
                return await response.Content.ReadAsByteArrayAsync();
            }
        }

        private async Task<DateTime> GetBirthdateFromGoogle(ExternalLoginInfo info)
        {
            var accessToken = info.AuthenticationTokens.FirstOrDefault(t => t.Name == "access_token")?.Value;

            if (string.IsNullOrEmpty(accessToken))
            {
                return DateTime.Now;
            }

            using (var httpClient = new HttpClient())
            {
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", accessToken);
                var response = await httpClient.GetAsync("https://people.googleapis.com/v1/people/me?personFields=birthdays");

                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    var birthdayData = JsonSerializer.Deserialize<GoogleDate>(json);

                    if (birthdayData != null)
                    {
                        return new DateTime(birthdayData.Year, birthdayData.Month, birthdayData.Day);
                    }
                }
            }

            return DateTime.Now;
        }

        // Clase folosite la deserializare din api
        public class GoogleDate
        {
            public int Year { get; set; }
            public int Month { get; set; }
            public int Day { get; set; }
        }
    }
}