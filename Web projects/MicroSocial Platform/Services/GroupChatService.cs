using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform.Services
{
    public class GroupChatService : IGroupChatService
    {
        private readonly AppContext appContext;
        private readonly UserManager<User> userManager;

        public GroupChatService(AppContext context, UserManager<User> _userManager)
        {
            appContext = context;
            userManager = _userManager;
        }

        public async Task<string> CreateGroupAsync(string groupName, string moderatorId, List<string> participantIds, string visibility, string groupDescription = "")
        {
            if (string.IsNullOrWhiteSpace(groupName) || string.IsNullOrWhiteSpace(moderatorId) || !participantIds.Any())
            {
                throw new ArgumentException("Group name, moderator, and participants are required.");
            }

            var group = new GroupChat
            {
                GroupChatId = Guid.NewGuid().ToString(),
                Description = groupDescription,
                Name = groupName,
                ModeratorId = moderatorId,
                Visibility = visibility == "Private" ? GroupVisibility.Private : GroupVisibility.Public
            };

            appContext.GroupChats.Add(group);
            await appContext.SaveChangesAsync();

            var participants = participantIds.Select(userId => new GroupChatParticipant
            {
                GroupChatId = group.GroupChatId,
                UserId = userId,
            }).ToList();

            foreach (var participant in participants)
            {

                // Trimite notificarea
                var creatorName = await appContext.Users.Where(u => u.Id == moderatorId).Select(u => u.UserName).FirstOrDefaultAsync();

                var notification = new Notification
                {
                    Type = "GroupRequest",
                    SenderId = moderatorId, // ID-ul utilizatorului care a creat grupul
                    RecipientId = participant.UserId,
                    Context = $"{creatorName} has invited you to join {group.Name}.",
                    Timestamp = DateTime.Now
                };

                appContext.Notifications.Add(notification);

                var joinRequest = new JoinRequest
                {
                    GroupChatId = group.GroupChatId,
                    UserId = participant.UserId,
                    Status = JoinRequestStatus.Pending,
                    RequestDate = DateTime.Now,
                    Notification = notification
                };

                appContext.JoinRequests.Add(joinRequest);
            }

            //appContext.GroupChatParticipants.AddRange(participants);
            await appContext.SaveChangesAsync();

            return group.GroupChatId;
        }

        public async Task<bool> AddParticipantAsync(string groupId, string userId)
        {
            if (await appContext.GroupChatParticipants.AnyAsync(p => p.GroupChatId == groupId && p.UserId == userId))
            {
                return false;
            }
            var participant = new GroupChatParticipant
            {
                GroupChatId = groupId,
                UserId = userId,
            };

            appContext.GroupChatParticipants.Add(participant);
            await appContext.SaveChangesAsync();
            return true;
        }

        public async Task<bool> RemoveParticipantAsync(string groupId, string userId, string currentUserId, bool sendNotification = true)
        {
            var participant = await appContext.GroupChatParticipants.FirstOrDefaultAsync(p => p.GroupChatId == groupId && p.UserId == userId);
            var group = await appContext.GroupChats.FirstOrDefaultAsync(p => p.GroupChatId == groupId);

            var isAdmin = await userManager.IsInRoleAsync(await userManager.FindByIdAsync(userId), "Admin");

            if (participant == null || group == null || (currentUserId != group.ModeratorId && userId != currentUserId && !isAdmin))
            {
                return false;
            }

            if (sendNotification)
            {
                var notification = new Notification
                {
                    Type = "ParticipantRemoved",
                    SenderId = currentUserId,
                    RecipientId = userId,
                    Context = $"You have been removed from the group {group.Name}.",
                    Timestamp = DateTime.Now
                };
                appContext.Notifications.Add(notification);
            }

            appContext.GroupChatParticipants.Remove(participant);
            await appContext.SaveChangesAsync();
            return true;
        }

        public async Task<List<GroupChatParticipant>> GetGroupParticipantsAsync(string groupId)
        {
            return await appContext.GroupChatParticipants
                .Where(p => p.GroupChatId == groupId)
                .ToListAsync();
        }

        public async Task<List<GroupChatMessage>> GetGroupMessagesAsync(string groupId)
        {
            return await appContext.GroupChatMessages
                .Where(m => m.GroupChatId == groupId)
                .OrderBy(m => m.Timestamp)
                .ToListAsync();
        }

        public async Task<int> SendGroupMessageAsync(string groupId, string senderId, string content)
        {
            if (string.IsNullOrWhiteSpace(content))
            {
                throw new ArgumentException("Message content cannot be empty.");
            }

            var message = new GroupChatMessage
            {
                GroupChatId = groupId,
                SenderId = senderId,
                Content = content,
                Timestamp = DateTime.Now
            };

            appContext.GroupChatMessages.Add(message);
            await appContext.SaveChangesAsync();
            return message.Id;
        }

        public async Task<int> SendGroupVoiceMessageAsync(string groupId, string senderId, byte[] mediaContent)
        {
            if(mediaContent == null)
            {
                throw new ArgumentException("Voice message content cannot be empty.");
            }
            var message = new GroupChatMessage
            {
                GroupChatId = groupId,
                SenderId = senderId,
                MediaContent = mediaContent,
                ContentMimeType = "Audio",
                Timestamp = DateTime.Now
            };

            appContext.GroupChatMessages.Add(message);
            await appContext.SaveChangesAsync();
            return message.Id;
        }

        public async Task<int> SendGroupMediaMessageAsync(string groupId, string senderId, IFormFile mediaFile)
        {
            if (mediaFile == null || mediaFile.Length == 0)
            {
                throw new ArgumentException("Media file cannot be empty.");
            }

            using (var memoryStream = new MemoryStream())
            {
                await mediaFile.CopyToAsync(memoryStream);
                var groupMessage = new GroupChatMessage
                {
                    GroupChatId = groupId,
                    SenderId = senderId,
                    MediaContent = memoryStream.ToArray(),
                    ContentMimeType = mediaFile.ContentType,
                    Timestamp = DateTime.Now
                };

                appContext.GroupChatMessages.Add(groupMessage);
                await appContext.SaveChangesAsync();

                return groupMessage.Id;
            }
        }

        public async Task<List<GroupChatNotification>> GetGroupNotificationsAsync(string userId)
        {
            return await appContext.GroupChatNotifications
                .Where(n => appContext.GroupChatParticipants.Any(p => p.UserId == userId && p.GroupChatId == n.GroupChatId))
                .ToListAsync();
        }

        public async Task<List<GroupChat>> GetUserGroupsAsync(string userId)
        {
            var groupChatIds = await appContext.GroupChatParticipants
                .Where(p => p.UserId == userId)
                .Select(p => p.GroupChatId)
                .Distinct()
                .ToListAsync();

            return await appContext.GroupChats
                .Where(g => groupChatIds.Contains(g.GroupChatId) || g.ModeratorId == userId)
                .ToListAsync();
        }

        public async Task<List<GroupChat>> GetAvailableGroupsAsync(string userId)
        {
            var groupChatIds = await appContext.GroupChatParticipants
                .Where(p => p.UserId == userId)
                .Select(p => p.GroupChatId)
                .Distinct()
                .ToListAsync();

            // grupurile in care s-a trimis deja un request
            var groupChatIdsWithRequest = await appContext.JoinRequests
                .Where(jr => jr.UserId == userId && jr.Status == JoinRequestStatus.Pending)
                .Select(jr => jr.GroupChatId)
                .Distinct()
                .ToListAsync();

            var availableGroups = await appContext.GroupChats
                .Where(g => !groupChatIds.Contains(g.GroupChatId)        // utilizatorul este deja membru
                        && !groupChatIdsWithRequest.Contains(g.GroupChatId)  // deja are request
                        && g.ModeratorId != userId)                        // utilizatorul e moderator
                .ToListAsync();

            return availableGroups;
        }


        public async Task<bool> EditGroupMessageAsync(string groupChatId, int messageId, string newContent, string userId)
        {
            if (string.IsNullOrWhiteSpace(newContent))
            {
                throw new ArgumentException("New message content cannot be empty.");
            }

            var message = await appContext.GroupChatMessages.FirstOrDefaultAsync(m => m.GroupChatId == groupChatId && m.Id == messageId);

            if (message == null)
            {
                return false;
            }

            // Permite editarea doar pentru autor
            if (message.SenderId != userId)
            {
                return false;
            }

            message.Content = newContent + " (edited)";

            await appContext.SaveChangesAsync();
            return true;
        }

        public async Task<bool> EditGroupAsync(string groupChatId, string userId, string groupName, string groupDescription, string visibility)
        {
            if (string.IsNullOrWhiteSpace(groupName) || string.IsNullOrWhiteSpace(groupDescription) || string.IsNullOrWhiteSpace(visibility))
            {
                throw new ArgumentException("New content cannot be empty.");
            }
            var group = await appContext.GroupChats.FirstOrDefaultAsync(gc => gc.GroupChatId == groupChatId);
            var isAdmin = await userManager.IsInRoleAsync(await userManager.FindByIdAsync(userId), "Admin");
            if (group == null || (group.ModeratorId != userId && !isAdmin))
            {
                return false;
            }

            group.Name = groupName;
            group.Description = groupDescription;
            group.Visibility = visibility == "Private" ? GroupVisibility.Private : GroupVisibility.Public;

            await appContext.SaveChangesAsync();
            return true; 
        }

        public async Task<bool> DeleteGroupMessageAsync(string groupChatId, int messageId, string userId)
        {
            var message = await appContext.GroupChatMessages.FirstOrDefaultAsync(m => m.GroupChatId == groupChatId && m.Id == messageId);

            if (message == null)
            {
                return false;
            }

            var group = await appContext.GroupChats.FirstOrDefaultAsync(g => g.GroupChatId == groupChatId);

            if (group == null)
            {
                return false; 
            }

            var isAdmin = await userManager.IsInRoleAsync(await userManager.FindByIdAsync(userId), "Admin");
            if (message.SenderId != userId && !isAdmin)
            {
                return false; // Utilizator neautorizat
            }

            appContext.GroupChatMessages.Remove(message);
            await appContext.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteGroupAsync(string groupChatId, string userId)
        {
            var group = await appContext.GroupChats.FirstOrDefaultAsync(gc => gc.GroupChatId == groupChatId);
            var isAdmin = await userManager.IsInRoleAsync(await userManager.FindByIdAsync(userId), "Admin");
            if (group == null || (group.ModeratorId != userId && !isAdmin))
            {
                return false;
            }
            appContext.GroupChats.Remove(group);
            await appContext.SaveChangesAsync();
            return true;
        }

        public async Task<bool> SetModeratorAsync(string groupChatId, string userId)
        {
            var group = await appContext.GroupChats.FirstOrDefaultAsync(gc => gc.GroupChatId == groupChatId);
            if (group == null)
            {
                return false;
            }
            await AddParticipantAsync(groupChatId, group.ModeratorId);
            group.ModeratorId = userId;
            await appContext.SaveChangesAsync();
            return true;
        }
    }

}
