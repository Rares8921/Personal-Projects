    using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform.Services
{
    public class ChatroomService : IChatroomService
    {
        private readonly AppContext appContext;

        public ChatroomService(AppContext context)
        {
            appContext = context;
        }

        public async Task<string?> GetChatroomIdAsync(string senderId, string recipientId, bool createRoom)
        {
            if (string.IsNullOrWhiteSpace(senderId) || string.IsNullOrWhiteSpace(recipientId))
            {
                throw new ArgumentException("Sender or recipient ID is invalid.");
            }
            var chatId = await appContext.Chatrooms.Where(c => c.SenderId == senderId && c.RecipientId == recipientId)
                   .Select(c => c.ChatId)
                   .FirstOrDefaultAsync();

            if(chatId == null)
            {
                if(createRoom)
                {
                    return await CreateChatIdAsync(senderId, recipientId);
                }
                return null;
            }
            return chatId;
        }

        [NonAction]
        public async Task<string> CreateChatIdAsync(string senderId, string recipientId)
        {
            var chatId = $"{senderId}_{recipientId}";

            var senderRecipient = new Chatroom
            {
                ChatId = chatId,
                SenderId = senderId,
                RecipientId = recipientId
            };

            var recipientSender = new Chatroom
            {
                ChatId = chatId,
                SenderId = recipientId,
                RecipientId = senderId
            };

            // Save sender-recipient si recipient-sender

            appContext.Chatrooms.Add(senderRecipient);
            appContext.Chatrooms.Add(recipientSender);
            await appContext.SaveChangesAsync();

            var senderUser = await appContext.Users.Where(u => u.Id == senderId).FirstOrDefaultAsync();

            var recipientUser = await appContext.Users.Where(u => u.Id == recipientId).FirstOrDefaultAsync();

            // Utilizatorii trebuie sa existe
            if (senderUser == null || recipientUser == null)
            {
                throw new Exception("One or more users not found.");
            }

            var senderParticipant = new ChatroomParticipant
            {
                ChatroomId = senderRecipient.Id,
                UserId = senderId,
                Chatroom = senderRecipient,
                User = senderUser
            };

            var recipientParticipant = new ChatroomParticipant
            {
                ChatroomId = senderRecipient.Id,
                UserId = recipientId,
                Chatroom = recipientSender,
                User = recipientUser
            };

            appContext.ChatroomParticipants.Add(senderParticipant);
            appContext.ChatroomParticipants.Add(recipientParticipant);

            await appContext.SaveChangesAsync();

            return chatId;
        }

        public async Task<bool> EditChatMessageAsync(string chatId, int messageId, string senderId, string newContent)
        {
            var message = await appContext.ChatMessages.FirstOrDefaultAsync(m => m.ChatId == chatId && m.Id == messageId && m.SenderId == senderId);

            if (message == null || newContent.Length == 0)
            {
                return false; 
            }

            message.Content = newContent + " (edited)";
            await appContext.SaveChangesAsync();

            return true;
        }

        public async Task<bool> DeleteChatMessageAsync(string chatId, int messageId, string senderId)
        {
            var message = await appContext.ChatMessages.FirstOrDefaultAsync(m => m.ChatId == chatId && m.Id == messageId && m.SenderId == senderId);

            if (message == null)
            {
                return false;
            }

            appContext.ChatMessages.Remove(message);
            await appContext.SaveChangesAsync();

            return true;
        }

        public async Task<int?> SaveVoiceMessageAsync(string chatId, string senderId, string recipientId, byte[] voiceMessage)
        {
            if (voiceMessage == null || voiceMessage.Length == 0)
            {
                return null;
            }
            var chatMessage = new ChatMessage
            {
                ChatId = chatId,
                SenderId = senderId,
                RecipientId = recipientId,
                MediaContent = voiceMessage,
                ContentMimeType = "Audio",
                Timestamp = DateTime.Now
            };

            appContext.ChatMessages.Add(chatMessage);
            await appContext.SaveChangesAsync();

            return chatMessage.Id;
        }
        public async Task<int> SaveMediaMessageAsync(string chatId, string senderId, string recipientId, IFormFile mediaFile)
        {
            if (mediaFile == null || mediaFile.Length == 0)
            {
                throw new ArgumentException("Media file cannot be empty.");
            }

            using (var memoryStream = new MemoryStream())
            {
                await mediaFile.CopyToAsync(memoryStream);
                var chatMessage = new ChatMessage
                {
                    ChatId = chatId,
                    SenderId = senderId,
                    RecipientId = recipientId,
                    MediaContent = memoryStream.ToArray(),
                    ContentMimeType = mediaFile.ContentType,
                    Timestamp = DateTime.Now
                };

                appContext.ChatMessages.Add(chatMessage);
                await appContext.SaveChangesAsync();

                return chatMessage.Id;
            }
        }
    }
}
