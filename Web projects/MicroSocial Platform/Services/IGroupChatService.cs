using MicroSocial_Platform.Models;

namespace MicroSocial_Platform.Services
{
    public interface IGroupChatService
    {
        Task<string> CreateGroupAsync(string groupName, string moderatorId, List<string> participantIds, string visibility, string groupDescription);
        Task<bool> AddParticipantAsync(string groupId, string userId);
        Task<bool> RemoveParticipantAsync(string groupId, string userId, string currentUserId, bool sendNotification = true);
        Task<List<GroupChatParticipant>> GetGroupParticipantsAsync(string groupId);
        Task<List<GroupChat>> GetAvailableGroupsAsync(string userId);
        Task<List<GroupChatMessage>> GetGroupMessagesAsync(string groupId);
        Task<int> SendGroupMessageAsync(string groupId, string senderId, string content);
        Task<int> SendGroupVoiceMessageAsync(string groupId, string senderId, byte[] mediaContent);
        Task<int> SendGroupMediaMessageAsync(string groupId, string senderId, IFormFile mediaFile);
        Task<List<GroupChatNotification>> GetGroupNotificationsAsync(string userId);
        Task<List<GroupChat>> GetUserGroupsAsync(string userId);
        Task<bool> EditGroupMessageAsync(string groupChatId, int messageId, string newContent, string userId);
        Task<bool> EditGroupAsync(string groupChatId, string userId, string groupName, string groupDescription, string visibility);
        Task<bool> DeleteGroupMessageAsync(string groupChatId, int messageId, string userId);
        Task<bool> DeleteGroupAsync(string groupChatId, string userId);
        Task<bool> SetModeratorAsync(string groupChatId, string userId);
    }
}
