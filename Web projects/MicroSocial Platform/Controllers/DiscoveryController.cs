using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class DiscoveryController : Controller
    {
        private readonly AppContext appContext;

        public DiscoveryController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        [HttpGet("ForYou")]
        public async Task<IActionResult> ForYou()
        {
            try
            {
                string currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);

                var followedUsers = await appContext.Followers
                    .Where(f => f.FollowerUserId == currentUserId)
                    .Select(f => f.FollowedUserId)
                    .ToListAsync();

                 var posts = await appContext.Posts.Where(post => followedUsers.Contains(post.UserId)).
                    OrderByDescending(post => post.TimeStamp).
                    ThenByDescending(post => post.LikeCounter)
                    .ToListAsync();
                var validPosts = posts
                    .Select(post => new
                    {
                        type = "Tag",
                        contentId = post.ContentId,
                        textContent = post.TextContent,
                        mediaLink = post.MediaLinks != null ? Convert.ToBase64String(post.MediaLinks) : null,
                        embedUrl = post.EmbedUrl,
                        timeStamp = post.TimeStamp,
                        mimeType = post.MimeType,
                        likeCounter = post.LikeCounter
                    }).ToList();

                return Json(validPosts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error: " + ex.Message);
            }
        }

        [HttpGet("Explore")]
        public async Task<IActionResult> Explore(string timeFrame)
        {
            try
            {
                var posts = await appContext.Posts.Where(post => !post.User.PrivateAccount).OrderByDescending(post => post.LikeCounter).ToListAsync();

                if (!string.IsNullOrEmpty(timeFrame))
                {
                    DateTime limitDate = DateTime.Now;

                    switch (timeFrame)
                    {
                        case "1day":
                            limitDate = limitDate.AddDays(-1);
                            break;
                        case "1week":
                            limitDate = limitDate.AddDays(-7);
                            break;
                        case "1month":
                            limitDate = limitDate.AddMonths(-1);
                            break;
                        case "1year":
                            limitDate = limitDate.AddYears(-1);
                            break;
                    }

                    posts = posts.Where(post => post.TimeStamp >= limitDate).ToList();
                }

                var validPosts = posts.Select(post => new
                {
                    contentId = post.ContentId,
                    textContent = post.TextContent,
                    mediaLink = post.MediaLinks != null ? Convert.ToBase64String(post.MediaLinks) : null,
                    embedUrl = post.EmbedUrl,
                    mimeType = post.MimeType,
                    likeCounter = post.LikeCounter,
                    timeStamp = post.TimeStamp
                }).ToList();

                return Json(validPosts);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error: " + ex.Message);
            }
        }
    }
}