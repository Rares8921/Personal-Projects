using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Identity;

namespace MicroSocial_Platform
{
    public class UpdateLastActive
    {
        // Ideea de a lucra asa cu request-uri am preluat-o din delegate pattern si:
        // https://java-design-patterns.com/patterns/delegation/
        private readonly RequestDelegate next;

        public UpdateLastActive(RequestDelegate _next)
        {
            next = _next;
        }

        // Am un context de la http prin care primesc informatii despre sesiunea curenta a user-ului
        // Aici actualizez ultimul timp cand a fost activ in mod asincron
        public async Task InvokeAsync(HttpContext context, UserManager<User> userManager)
        {
            if(context.User.Identity?.IsAuthenticated == true)
            {
                var user = await userManager.GetUserAsync(context.User);
                if (user != null)
                {
                    try
                    {
                        user.LastActive = DateTime.Now;
                        await userManager.UpdateAsync(user);
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine($"Error updating last active: {ex.Message}");
                    }
                }
            }
            await next(context);
        }
    }
}
