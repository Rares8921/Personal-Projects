namespace MicroSocial_Platform.Services
{
    public interface IChatroomService
    {
        Task<string?> GetChatroomIdAsync(string senderId, string recipientId, bool createRoom);
        Task<string> CreateChatIdAsync(string senderId, string recipientId);
        Task<bool> EditChatMessageAsync(string chatId, int messageId, string senderId, string newContent);
        Task<bool> DeleteChatMessageAsync(string chatId, int messageId, string senderId);
        Task<int?> SaveVoiceMessageAsync(string chatId, string senderId, string recipientId, byte[] voiceMessage);
        Task<int> SaveMediaMessageAsync(string chatId, string senderId, string recipientId, IFormFile mediaFile);
    }
}
