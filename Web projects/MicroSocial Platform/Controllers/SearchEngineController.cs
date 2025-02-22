using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class SearchEngineController : Controller
    {

        private readonly AppContext appContext;

        public SearchEngineController(AppContext _appContext)
        {
            appContext = _appContext;
        }

        // Functiile de mai jos se apeleaza din view
        // Pentru a rula task-urile in fundal, trebuie sa rulam asincron
        // De thread-ul aplicatiei si obtinem paralelism
        // Nu avem livelock pentru ca e prestabilit un timp maxim de cautare al datelor
        // In caz de eroare se da throw unei exceptii si opresc thread-urile (sunt scoase si din thread poll)
        // Si nu blochez nici thread-ul de UI daca va fi vreun crash

        public async Task<IActionResult> SearchTags(string tagText)
        {
            if (string.IsNullOrWhiteSpace(tagText))
            {
                return BadRequest("Search term cannot be empty.");
            }

            // Trebuie sa preiau datele din baza de date in memorie
            var posts = await appContext.Posts.AsNoTracking().ToListAsync();
            var validPosts = posts
                .Where(post => !post.User.PrivateAccount && post.Tags != null && post.Tags.Any(tag => tag.Contains(tagText, StringComparison.OrdinalIgnoreCase)))
                .Select(post => new
                {
                    type = "Tag",
                    id = post.ContentId,
                    textContent = post.TextContent,
                    mediaLink = post.MediaLinks != null ? Convert.ToBase64String(post.MediaLinks) : null,
                    embedUrl = post.EmbedUrl,
                    mimeType = post.MimeType
                }).ToList();
            return Json(validPosts);
        }

        // Aici preiau tipul de date ajutandu-ma de magic numbers
        // Documentatie: https://en.wikipedia.org/wiki/List_of_file_signatures
        private string? GetMediaType(byte[] media)
        {
            if (media == null || media.Length < 4)
                return null;

            if (media.Take(4).SequenceEqual(new byte[] { 0x89, 0x50, 0x4E, 0x47 })) // PNG
                return "image/png";
            if (media.Take(2).SequenceEqual(new byte[] { 0xFF, 0xD8 })) // JPEG
                return "image/jpeg";
            if (media.Take(4).SequenceEqual(new byte[] { 0x47, 0x49, 0x46, 0x38 })) // GIF
                return "image/gif";
            if (media.Take(4).SequenceEqual(new byte[] { 0x66, 0x74, 0x79, 0x70}) &&
                (media[4] == 0x69 || media[4] == 0x4D) ) // MP4
                return "video/mp4";

            return "unknown";
        }


        public async Task<IActionResult> SearchUsers(string userNameText)
        {
            if (string.IsNullOrWhiteSpace(userNameText))
            {
                return BadRequest("Search term cannot be empty.");
            }

            //https://learn.microsoft.com/en-us/dotnet/api/system.identitymodel.claims.claimtypes.nameidentifier?view=net-8.0-pp
            var currentUserId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            return Json(await appContext.Users
                .Where(u => u.UserName.ToLower().Contains(userNameText.ToLower()) && u.Id != currentUserId) // Match cu orice nume si exclud utilizatorul curent
                .Select(u => new { type = "User", name = u.UserName, id = u.Id } ) // Aici am nevoie si de id-ul utilizatorului pentru a ma redirectiona dupa id
                //.Take(15) // la fel
                .ToListAsync());
        }



    }
}

