using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;
using System.Text;
using System.Text.RegularExpressions;

namespace MicroSocial_Platform.Controllers
{
    public class PostController : Controller
    {

        private readonly AppContext appContext;

        public PostController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        public IActionResult Index()
        {
            // Daca user-ul din sesiunea curenta nu este logat
            if (User.FindFirstValue(ClaimTypes.NameIdentifier) == null)
            {
                return Unauthorized();
            }
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View("~/Views/Content/CreatePost.cshtml");
        }

        public IActionResult Detail(int id)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            Post? post = appContext.Posts.Include(p => p.User).Include(p => p.PostComments).FirstOrDefault(p => p.ContentId == id);
            if(post == null)
            {
                return RedirectToAction("Index", "Home");
            }
            string? sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (sessionUser == null)
            {
                ViewBag.IsSavedPost = null;
            }
            else
            {
                ViewBag.IsSavedPost = appContext.PostsSaved.Where(ps => ps.UserId == sessionUser && ps.PostId == id).Any();
            }
            return View("~/Views/Content/Detail.cshtml", post);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(string content, string mimeType, IFormFile? mediaContent, string? embedUrl, string? tags)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (currentUserId == null || content.Length > 500)
            {
                return Unauthorized();
            }

            var user = await appContext.Users.FindAsync(currentUserId);
            if (user == null)
            {
                return NotFound();
            }

            if (string.IsNullOrWhiteSpace(content))
            {
                ModelState.AddModelError("content", "Post text is required.");
                return View("CreatePost");
            }

            // Creare postare
            var post = new Post
            {
                User = user,
                TextContent = content,
                UserId = currentUserId,
                TimeStamp = DateTime.Now,
                LikeCounter = 0,
            };

            if (mediaContent != null)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await mediaContent.CopyToAsync(memoryStream);
                    post.MediaLinks = memoryStream.ToArray();
                }
            }
            else if (!string.IsNullOrEmpty(embedUrl))
            {
                post.EmbedUrl = embedUrl;
            }
            post.MimeType = mimeType;

            // Validare tag-uri
            if (!string.IsNullOrEmpty(tags))
            {
                var tagNames = tags.Split(',').Select(t => t.Trim()).ToList();

                var invalidTags = tagNames.Where(tag => !Regex.IsMatch(tag, @"^[a-z]+$")).ToList();
                if (invalidTags.Any())
                {
                    ModelState.AddModelError("tags", "The tags should be lower case!");
                    return View("CreatePost", post);
                }

                if (tagNames.Count > 50)
                {
                    ModelState.AddModelError("tags", "The content exceeds the maximum of 50 tags allowed.");
                    return View("CreatePost", post);
                }

                post.Tags = tagNames;
            }

            appContext.Posts.Add(post);
            await appContext.SaveChangesAsync();

            return RedirectToAction("Index", "Profile");
        }



        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult EditPage(int id)
        {
            Post? post = appContext.Posts.Include(p => p.User).Include(p => p.PostComments).FirstOrDefault(p => p.ContentId == id);
            if (post == null)
            {
                return RedirectToAction("Index", "Home");
            }
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            return View("~/Views/Content/EditPost.cshtml", post);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, string? newTextContent, IFormFile? newMediaContent, string? newEmbedUrl, string? newTags)
        {
            ViewBag.IsLoggedIn = User.Identity.IsAuthenticated;
            Post? post = await appContext.Posts.Include(p => p.User).FirstOrDefaultAsync(p => p.ContentId == id);
            if (post == null)
            {
                return NotFound();
            }

            var currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier); 
            var isAdmin = User.IsInRole("Admin");
            if (post.UserId != currentUserId && !isAdmin)
            {
                return Unauthorized();
            }

            bool isModified = false;

            // Actualizare text
            if (!string.IsNullOrWhiteSpace(newTextContent))
            {
                if (newTextContent.Length > 500)
                {
                    ModelState.AddModelError("newTextContent", "The content exceeds the maximum length of 500 characters.");
                    return View("~/Views/Content/EditPost.cshtml", post);
                }
                post.TextContent = newTextContent;
                isModified = true;
            }             else
            {
                ModelState.AddModelError("newMediaContent", "You must provide text for your post.");
                return View("~/Views/Content/EditPost.cshtml", post);
            }

            if (newMediaContent != null)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await newMediaContent.CopyToAsync(memoryStream);
                    post.MediaLinks = memoryStream.ToArray();
                }
                isModified = true;
            }
            else if (!string.IsNullOrEmpty(newEmbedUrl))
            {
                post.MediaLinks = Encoding.UTF8.GetBytes(newEmbedUrl);
                isModified = true;
            }

            // Actualizare tag-uri
            if (!string.IsNullOrEmpty(newTags))
            {
                var tagList = newTags.Split(',').Select(tag => tag.Trim()).ToList();

                var invalidTags = tagList.Where(tag => !Regex.IsMatch(tag, @"^[a-z]+$")).ToList();
                if (invalidTags.Any())
                {
                    ModelState.AddModelError("newTags", "All tags must be formed from lowercase letters.");
                    return View("~/Views/Content/EditPost.cshtml", post);
                }

                if (tagList.Count > 50)
                {
                    ModelState.AddModelError("newTags", "You can only add up to 50 tags.");
                    return View("~/Views/Content/EditPost.cshtml", post);
                }

                post.Tags = tagList;
                isModified = true;
            }

            if (!isModified)
            {
                ModelState.AddModelError("EditError", "No valid changes were provided.");
                return View("~/Views/Content/EditPost.cshtml", post);
            }

            await appContext.SaveChangesAsync();
            TempData["Message"] = "Post updated successfully!";
            return RedirectToAction("Detail", "Post", new { id });
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Delete(int id)
        {
            var post = await appContext.Posts.FindAsync(id);
            if (post == null)
            {
                return NotFound();
            }

            var currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var isAdmin = User.IsInRole("Admin");
            if (post.UserId != currentUserId && !isAdmin)
            {
                return Unauthorized();
            }

            foreach (var comment in post.PostComments.ToList())
            {
                var redirectResult = RedirectToAction("Delete", "Comment", new { id = comment.CommentId, postId = post.ContentId });
                await redirectResult.ExecuteResultAsync(ControllerContext);
            }

            var likes = appContext.Likes.Where(l => l.ContentId == post.ContentId);
            appContext.Likes.RemoveRange(likes);

            appContext.Posts.Remove(post);

            await appContext.SaveChangesAsync();

            return RedirectToAction("Index", "Profile");
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> SavePost(int postId)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return Unauthorized();
            }

            var user = await appContext.Users.Include(u => u.SavedPosts).ThenInclude(sp => sp.Post).FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null)
            {
                return NotFound();
            }

            var post = await appContext.Posts.FirstOrDefaultAsync(p => p.ContentId == postId);
            if (post == null)
            {
                return NotFound();
            }

            var existingPostSaved = await appContext.PostsSaved.FirstOrDefaultAsync(ps => ps.UserId == userId && ps.PostId == post.ContentId);

            if (existingPostSaved == null)
            {
                var postSaved = new PostSaved
                {
                    UserId = user.Id,
                    PostId = post.ContentId,
                    Timestamp = DateTime.Now,
                    User = user,
                    Post = post
                };

                appContext.PostsSaved.Add(postSaved);
                await appContext.SaveChangesAsync();
            }

            return RedirectToAction("Detail", "Post", new { id = postId });
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> RemoveSavedPost(int postId, string redirectString)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (userId == null)
            {
                return Unauthorized();
            }

            var user = await appContext.Users.Include(u => u.SavedPosts).ThenInclude(sp => sp.Post).FirstOrDefaultAsync(u => u.Id == userId);

            if (user == null)
            {
                return NotFound();
            }

            var savedPost = user.SavedPosts.FirstOrDefault(sp => sp.PostId == postId);
            if (savedPost != null)
            {
                appContext.PostsSaved.Remove(savedPost);
                await appContext.SaveChangesAsync();
            }

            if (redirectString == "profile")
            {
                return RedirectToAction("Index", "Profile");
            }
            return RedirectToAction("Detail", "Post", new { id = postId });
        }

    }
}