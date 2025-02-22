using MicroSocial_Platform.Models;
using MicroSocial_Platform.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.ML;
using System;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace MicroSocial_Platform.Controllers
{
    public class ChatMessageController : Controller
    {
        private readonly AppContext appContext;
        private readonly IChatroomService chatroomService;

        public ChatMessageController(AppContext context, IChatroomService _chatroomService)
        {
            appContext = context;
            chatroomService = _chatroomService;
        }

        [HttpPost]
        public async Task<IActionResult> Save(string messageContent, string ChatId, string senderId, string recipientId)
        {

            if (string.IsNullOrWhiteSpace(messageContent))
            {
                return BadRequest(new { Error = "Message content is required." });
            }

            if (string.IsNullOrWhiteSpace(senderId) || string.IsNullOrWhiteSpace(ChatId))
            {
                return BadRequest(new { Error = "SenderId and ChatId are required." });
            }

            // creez mesajul
            var chatMessage = new ChatMessage
            {
                Content = messageContent,
                SenderId = senderId,
                ChatId = ChatId,
                RecipientId = recipientId,
                Timestamp = DateTime.Now
            };

            try
            {
                appContext.ChatMessages.Add(chatMessage);
                await appContext.SaveChangesAsync();

                var senderName = await appContext.Users.Where(u => u.Id == senderId).Select(u => u.UserName).FirstOrDefaultAsync();
                var result = new
                {
                    chatMessage.Id,
                    chatMessage.ChatId,
                    chatMessage.SenderId,
                    SenderName = senderName,
                    chatMessage.RecipientId,
                    chatMessage.Content,
                    chatMessage.Timestamp
                };

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = $"An error occurred while saving the message: {ex.Message}" });
            }
        }


        [HttpGet]
        public async Task<IActionResult> FindChatMessages(string chatId)
        {
            if (string.IsNullOrEmpty(chatId))
            {
                return BadRequest("ChatId is required.");
            }

            var messages = await appContext.ChatMessages
                .Where(m => m.ChatId == chatId)
                .OrderBy(m => m.Timestamp)
                .Select(m => new
                {
                    m.Id,
                    m.ChatId,
                    m.SenderId,
                    SenderName = appContext.Users.Where(u => u.Id == m.SenderId).Select(u => u.UserName).FirstOrDefault(),
                    SenderPfp = Convert.ToBase64String(appContext.Users.Where(u => u.Id == m.SenderId).Select(u => u.ProfilePicture).FirstOrDefault()),
                    m.Content,
                    m.Timestamp,
                    m.ContentMimeType,
                    MediaContent = m.MediaContent != null ? Convert.ToBase64String(m.MediaContent) : null
                })
                .ToListAsync();

            if (messages == null || !messages.Any())
            {
                return Ok(new List<object>());
            }

            return Ok(messages);
        }

        [HttpPost]
        public async Task<IActionResult> EditChatMessage(string chatId, int messageId, string senderId, string newContent)
        {
            var success = await chatroomService.EditChatMessageAsync(chatId, messageId, senderId, newContent);

            if (!success)
            {
                return Unauthorized(new { Error = "You are not authorized to edit this message!" });
            }
            var senderName = await appContext.Users.Where(u => u.Id == senderId).Select(u => u.UserName).FirstOrDefaultAsync();
            var recipientId = await appContext.ChatMessages.Where(cm => cm.Id == messageId).Select(cm => cm.RecipientId).FirstOrDefaultAsync();
            var recipientName = await appContext.Users.Where(u => u.Id == recipientId).Select(u => u.UserName).FirstOrDefaultAsync();
            return Ok(new { success = true, senderName, recipientId, recipientName });
        }

        [HttpDelete]
        public async Task<IActionResult> DeleteChatMessage(string chatId, int messageId, string senderId)
        {
            var success = await chatroomService.DeleteChatMessageAsync(chatId, messageId, senderId);

            if (!success)
            {
                return Unauthorized(new { Error = "You are not authorized to delete this message!" });
            }
            return Ok(new { success = true });
        }

        [HttpPost]
        public async Task<IActionResult> SendVoiceMessage(IFormFile voiceMessage, string chatId, string senderId, string recipientId)
        {
            if (voiceMessage == null || voiceMessage.Length == 0)
            {
                return BadRequest(new { Error = "Voice message is required." });
            }

            if (string.IsNullOrWhiteSpace(senderId) || string.IsNullOrWhiteSpace(chatId) || string.IsNullOrWhiteSpace(recipientId))
            {
                return BadRequest(new { Error = "SenderId, ChatId, and RecipientId are required." });
            }

            using (var memoryStream = new MemoryStream())
            {
                await voiceMessage.CopyToAsync(memoryStream);
                var messageId = await chatroomService.SaveVoiceMessageAsync(chatId, senderId, recipientId, memoryStream.ToArray());

                if (messageId != null)
                {
                    // Iau numele user-ului
                    var senderName = await appContext.Users.Where(u => u.Id == senderId).Select(u => u.UserName).FirstOrDefaultAsync();
                    var recipientName = await appContext.Users.Where(u => u.Id == recipientId).Select(u => u.UserName).FirstOrDefaultAsync();
                    return Ok(new { success = true, senderName, messageId, senderId, chatId, recipientId });
                }
                else
                {
                    return StatusCode(500, new { Error = "Failed to save voice message." });
                }
            }
        }

        [HttpPost]
        public async Task<IActionResult> SendMediaMessage(string chatId, string senderId, string recipientId, IFormFile mediaFile)
        {
            if (string.IsNullOrWhiteSpace(chatId) || string.IsNullOrWhiteSpace(senderId) || mediaFile == null)
            {
                return BadRequest(new { Error = "ChatId, SenderId, and MediaFile are required." });
            }

            try
            {
                var messageId = await chatroomService.SaveMediaMessageAsync(chatId, senderId, recipientId, mediaFile);
                var senderName = await appContext.Users.Where(u => u.Id == senderId).Select(u => u.UserName).FirstOrDefaultAsync();
                var recipientName = await appContext.Users.Where(u => u.Id == recipientId).Select(u => u.UserName).FirstOrDefaultAsync();
                return Ok(new { success = true, senderName, recipientName, messageId, chatId, recipientId, senderId });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = $"An error occurred while sending the media message: {ex.Message}" });
            }
        }

    }
}
