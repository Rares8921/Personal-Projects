using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Security.Claims;

namespace MicroSocial_Platform
{
    public class ReportController : Controller
    {
        private readonly AppContext appContext;

        public ReportController(AppContext context)
        {
            appContext = context;
        }

        [HttpPost]
        public async Task<IActionResult> Submit([FromBody] ReportRequest request)
        {
            Console.WriteLine("Request Body: " + JsonConvert.SerializeObject(request));
            var sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);

            if (string.IsNullOrWhiteSpace(request.ReportContent) || sessionUser == null)
            {
                return BadRequest("Invalid report content.");
            }

            var report = new Report
            {
                ReportContent = request.ReportContent,
                Timestamp = DateTime.Now,
                UserId = sessionUser,
                User = await appContext.Users.FindAsync(sessionUser)
            };

            appContext.Reports.Add(report);
            await appContext.SaveChangesAsync();

            return Ok(new { success = true });
        }

        public class ReportRequest
        {
            public string ReportContent { get; set; }
        }
    }
}
