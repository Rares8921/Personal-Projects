using MicroSocial_Platform.Models;
using MicroSocial_Platform.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using NuGet.Protocol.Plugins;
using System.Text.Json;

namespace MicroSocial_Platform.Controllers
{
    public class GroupChatMessageController : Controller
    {

        private readonly AppContext appContext;
        private readonly IGroupChatService groupChatService;

        public GroupChatMessageController(AppContext _appContext, IGroupChatService _groupChatService)
        {
            appContext = _appContext;
            groupChatService = _groupChatService;
        }

        [HttpPost]
        public async Task<IActionResult> SendGroupMessage(string groupId, string senderId, string content)
        {

            if (string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(senderId) || string.IsNullOrWhiteSpace(content))
            {
                return BadRequest(new { Error = "Group ID, Sender ID, and Content are required." });
            }

            // Trimit mesajul
            try
            {
                int messageId = await groupChatService.SendGroupMessageAsync(groupId, senderId, content);

                var result = new
                {
                    Id = messageId,
                    GroupChatId = groupId,
                    SenderName = appContext.Users.FirstOrDefault(u => u.Id == senderId)?.UserName,
                    SenderId = senderId,
                    content,
                    Timestamp = DateTime.Now,
                    ok = true
                };

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = $"An error occurred while sending the message: {ex.Message}" });
            }
        }

        [HttpPost]
        public async Task<IActionResult> SendGroupVoiceMessage(IFormFile voiceMessage, string groupId, string senderId)
        {
            if (voiceMessage == null || voiceMessage.Length == 0)
            {
                return BadRequest(new { Error = "Voice message is required." });
            }

            if (string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(senderId))
            {
                return BadRequest(new { Error = "Group ID and Sender ID are required." });
            }

            // Trimit mesajul
            using (var memoryStream = new MemoryStream())
            {
                await voiceMessage.CopyToAsync(memoryStream);
                try
                {
                    int messageId = await groupChatService.SendGroupVoiceMessageAsync(groupId, senderId, memoryStream.ToArray());

                    var result = new
                    {
                        messageId,
                        GroupChatId = groupId,
                        SenderName = appContext.Users.FirstOrDefault(u => u.Id == senderId)?.UserName,
                        SenderId = senderId,
                        Timestamp = DateTime.Now,
                        success = true,
                        chatId = groupId,
                        RecipientId = "",
                        RecipientName = ""
                    };

                    return Ok(result);
                }
                catch (Exception ex)
                {
                    return StatusCode(500, new { Error = $"An error occurred while sending the message: {ex.Message}" });
                }
            }
        }

        [HttpPost]
        public async Task<IActionResult> SendGroupMediaMessage(string groupId, string senderId, IFormFile mediaFile)
        {
            if (string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(senderId) || mediaFile == null)
            {
                return BadRequest(new { Error = "GroupId, SenderId, and MediaFile are required." });
            }

            try
            {
                var messageId = await groupChatService.SendGroupMediaMessageAsync(groupId, senderId, mediaFile);
                var senderName = appContext.Users.FirstOrDefault(u => u.Id == senderId)?.UserName;
                var message = await appContext.GroupChatMessages.FirstOrDefaultAsync(gm => gm.Id == messageId);
                return Ok(new { success = true, senderId, senderName, messageId, recipientName="",recipientId="", chatId=groupId});
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = $"An error occurred while sending the media message: {ex.Message}" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetGroupMessages(string groupId)
        {

            if (string.IsNullOrWhiteSpace(groupId))
            {
                return BadRequest(new { Error = "Group ID is required." });
            }

            // Obtin mesajele
            try
            {
                var messages = await groupChatService.GetGroupMessagesAsync(groupId);
                var result = messages.Select(m => new
                {
                    m.Id,
                    m.GroupChatId,
                    ModeratorId = appContext.GroupChats.FirstOrDefault(gc => gc.GroupChatId == groupId)?.ModeratorId,
                    m.SenderId,
                    m.Content,
                    SenderName = appContext.Users.FirstOrDefault(u => u.Id == m.SenderId)?.UserName,
                    m.Timestamp,
                    m.ContentMimeType,
                    m.MediaContent
                }).ToList();

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = $"An error occurred while retrieving messages: {ex.Message}" });
            }
        }

        [HttpPost]
        public async Task<IActionResult> EditGroupMessage(string groupChatId, int messageId, string newContent, string userId)
        {
            if (string.IsNullOrWhiteSpace(newContent))
            {
                return BadRequest(new { Error = "New message content cannot be empty." });
            }

            var result = await groupChatService.EditGroupMessageAsync(groupChatId, messageId, newContent, userId);

            if (!result)
            {
                return Unauthorized(new { Error = "You are not authorized to edit this message." });
            }
            var senderName = await appContext.Users.Where(u => u.Id == userId).Select(u => u.UserName).FirstOrDefaultAsync();
            return Ok(new { Success = "Message edited successfully.", senderName, recipientId="", recipientName="" } );
        }

        [HttpDelete]
        public async Task<IActionResult> DeleteGroupMessage(string groupChatId, int messageId, string userId)
        {

            var result = await groupChatService.DeleteGroupMessageAsync(groupChatId, messageId, userId);

            if (!result)
            {
                return Unauthorized(new { Error = "You are not authorized to delete this message." } );
            }

            return Ok(new { Success = "Message deleted successfully." } );
        }


    }
}
