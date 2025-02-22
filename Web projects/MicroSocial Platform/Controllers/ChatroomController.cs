using MicroSocial_Platform.Models;
using MicroSocial_Platform.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.ML;
using System.Security.Claims;

namespace MicroSocial_Platform.Controllers
{
    public class ChatroomController : Controller
    {
        private readonly AppContext appContext;
        private readonly IChatroomService chatroomService;

        public ChatroomController(AppContext context, IChatroomService _chatroomService)
        {
            appContext = context;
            chatroomService = _chatroomService;
        }

        [HttpGet]
        public async Task<IActionResult> GetChatroomID(string senderId, string recipientId, bool createRoom)
        {

            //Console.WriteLine($"Sender ID: {senderId}, Recipient ID: {recipientId}");

            if (string.IsNullOrWhiteSpace(senderId) || string.IsNullOrWhiteSpace(recipientId))
            {
                return BadRequest("Invalid sender or recipient ID.");
            }
            // Caut chatroom-ul curent
            try
            {
                var chatId = await chatroomService.GetChatroomIdAsync(senderId, recipientId, createRoom);
                if (chatId == null)
                {
                    return NotFound("Chatroom not found.");
                }

                return Ok(new { ChatId = chatId });
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // toate chat-urile pentru utilizatorul curent
        [HttpGet]
        public async Task<IActionResult> GetUserChatrooms(string userId)
        {
            if (string.IsNullOrEmpty(userId))
            {
                return BadRequest("User ID is required.");
            }

            var chatrooms = await appContext.Chatrooms
                .Where(c => c.SenderId == userId || c.RecipientId == userId)
                .Select(c => new
                {
                    c.ChatId,
                    RecipientId = c.SenderId == userId ? c.RecipientId : c.SenderId,
                    RecipientName = c.SenderId == userId
                        ? appContext.Users.Where(u => u.Id == c.RecipientId).Select(u => u.UserName).FirstOrDefault()
                        : appContext.Users.Where(u => u.Id == c.SenderId).Select(u => u.UserName).FirstOrDefault(),
                    ProfilePicture = c.SenderId == userId
                        ? appContext.Users.Where(u => u.Id == c.RecipientId).Select(u => u.ProfilePicture).FirstOrDefault()
                        : appContext.Users.Where(u => u.Id == c.SenderId).Select(u => u.ProfilePicture).FirstOrDefault()
                })
                .Distinct()
                .ToListAsync();

            var chatroomWithDeserializedRecipient = chatrooms.Select(chat => new
            {
                chat.ChatId,
                chat.RecipientId,
                chat.RecipientName,
                ProfilePictureBase64 = $"data:image/png;base64,{Convert.ToBase64String(chat.ProfilePicture)}"
            }
            ).ToList();
            if (chatroomWithDeserializedRecipient != null && chatroomWithDeserializedRecipient.Any())
            {
                return Ok(new { ChatRooms = chatroomWithDeserializedRecipient });
            }

            return Ok(new { ChatRooms = new List<object>() });
        }


    }
}
