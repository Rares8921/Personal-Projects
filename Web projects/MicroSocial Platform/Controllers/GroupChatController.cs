using Castle.Core.Logging;
using MicroSocial_Platform.Models;
using MicroSocial_Platform.Services;
using Microsoft.AspNetCore.Identity.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using NuGet.Protocol.Plugins;
using System.Security.Claims;
using System.Text.Json;

namespace MicroSocial_Platform.Controllers
{
    public class GroupChatController : Controller
    {
        private readonly IGroupChatService groupChatService;
        private readonly AppContext appContext;

        public GroupChatController(IGroupChatService _groupChatService, AppContext _appContext)
        {
            groupChatService = _groupChatService;
            appContext = _appContext;
        }

        [HttpPost]
        public async Task<IActionResult> CreateGroup(string groupName, string moderatorId, string userIds, string visibility, string groupDescription)
        {

            
            if (string.IsNullOrWhiteSpace(groupName) || string.IsNullOrWhiteSpace(moderatorId))
                return BadRequest(new { Error = "Group name and Moderator ID are required." });

            var participantIds = userIds.Split(',').ToList();

            if (participantIds == null || !participantIds.Any())
                return BadRequest(new { Error = "At least one participant is required." });

            try
            {
                var groupId = await groupChatService.CreateGroupAsync(groupName, moderatorId, participantIds, visibility, groupDescription);
                return Ok(new { GroupChatId = groupId });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> EditGroup(string groupId, string userId, string groupName, string groupDescription, string visibility)
        {
            if (string.IsNullOrWhiteSpace(groupName) || string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(groupDescription) || string.IsNullOrWhiteSpace(visibility))
                return BadRequest(new { Error = "All content must be set!" });

            try
            {
                var result = await groupChatService.EditGroupAsync(groupId, userId, groupName, groupDescription, visibility);
                if(!result)
                {
                    return BadRequest(new { Error = "Could not edit group." });
                }
                return Ok(new { success = true });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> AddParticipant(string groupId, string userId)
        {
            if (string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(userId))
                return BadRequest(new { Error = "Group ID and User ID are required." });

            try
            {
                var result = await groupChatService.AddParticipantAsync(groupId, userId);
                if(!result)
                {
                    return BadRequest(new { Error = "Could not add participant to group." });
                }
                return Ok(new { Message = "Participant added successfully.", success = true });
            }
            catch (Exception ex)
            {
                return BadRequest(new { Error = ex.Message });
            }
        }

        [HttpDelete]
        public async Task<IActionResult> RemoveParticipant(string groupId, string userId, bool? sendNotification)
        {
            if (string.IsNullOrWhiteSpace(groupId) || string.IsNullOrWhiteSpace(userId))
                return BadRequest(new { Error = "Group ID and User ID are required." });

            var notificationOptions = await appContext.NotificationOptions.FindAsync(userId);
            sendNotification = notificationOptions != null && notificationOptions.ChatMessages;

            try
            {
                string? currentUserId = User.FindFirstValue(ClaimTypes.NameIdentifier);
                if(currentUserId == null)
                {
                    return BadRequest(new { Error = "You do not have permission to acess this menu." });
                }
                bool result = await groupChatService.RemoveParticipantAsync(groupId, userId, currentUserId, sendNotification == true);
                if(!result)
                {
                    return BadRequest(new { Error = "You do not have permission to remove this user." });
                }
                return Ok(new { Message = "Participant removed successfully.", success = true });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetGroupParticipants(string groupId)
        {
            if (string.IsNullOrWhiteSpace(groupId))
                return BadRequest(new { Error = "Group ID is required." });
            try
            {
                var participants = await groupChatService.GetGroupParticipantsAsync(groupId);

                if(participants == null)
                {
                    return BadRequest(new { Error = "Invalid group." });
                }

                var result = participants.Select(async p => new
                {
                    UserId = p.UserId,
                    GroupChatId = p.GroupChatId,
                    UserName = await appContext.Users.Where(u => u.Id == p.UserId).Select(u => u.UserName).FirstOrDefaultAsync()
                });

                var participantsJson = JsonSerializer.Serialize(result, new JsonSerializerOptions
                {
                    WriteIndented = true,
                    ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles
                });
                return Ok(participantsJson);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetUserGroups(string userId)
        {
            if (string.IsNullOrWhiteSpace(userId))
                return BadRequest(new { Error = "User ID is required." });

            try
            {
                var groups = await groupChatService.GetUserGroupsAsync(userId);

                if(groups == null)
                {
                    return BadRequest(new { Error = "Invalid user." });
                }

                var groupsJson = JsonSerializer.Serialize(groups, new JsonSerializerOptions
                {
                    WriteIndented = true,
                    ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles
                });

                return Ok(groupsJson);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpDelete]
        public async Task<IActionResult> DeleteGroup(string groupChatId, string userId)
        {
            if (string.IsNullOrWhiteSpace(userId) || string.IsNullOrWhiteSpace(groupChatId))
                return BadRequest(new { Error = "User ID and groupId are required." });
            try
            {
                var result = await groupChatService.DeleteGroupAsync(groupChatId, userId);
                if(!result)
                {
                    return BadRequest(new { Error = "You do not have permission to delete this group." });
                }
                return Ok(new { success = true });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> SetModerator(string groupChatId, string userId, string userAction)
        {
            if (string.IsNullOrWhiteSpace(userId) || string.IsNullOrWhiteSpace(groupChatId))
                return BadRequest(new { Error = "User ID and groupId are required." });

            try
            {
                if (userAction == "leave")
                {
                    await RemoveParticipant(groupChatId, userId, false);
                }
                var result = await groupChatService.SetModeratorAsync(groupChatId, userId);
                if (!result)
                {
                    return BadRequest(new { Error = "The moderator could not be set!" });
                }
                return Ok(new { success = true });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }

        }

        [HttpGet]
        public async Task<IActionResult> AvailableGroups(string userId)
        {
            if (string.IsNullOrWhiteSpace(userId))
                return BadRequest(new { Error = "User ID and groupId are required." });
            try
            {
                var groups = await groupChatService.GetAvailableGroupsAsync(userId);

                if (groups == null)
                {
                    return BadRequest(new { Error = "Invalid user." });
                }

                var groupsJson = JsonSerializer.Serialize(groups, new JsonSerializerOptions
                {
                    WriteIndented = true,
                    ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles
                });

                return Ok(groupsJson);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { Error = ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> SendJoinRequest(string groupChatId, string userId)
        {
            var groupChat = await appContext.GroupChats.FirstOrDefaultAsync(g => g.GroupChatId == groupChatId);

            if (groupChat == null)
            {
                return BadRequest(new { success = false, message = "Group not found." });
            }

            var existingRequest = await appContext.JoinRequests.FirstOrDefaultAsync(jr => jr.UserId == userId && jr.GroupChatId == groupChatId);

            if (existingRequest != null)
            {
                return BadRequest(new { success = false, message = "Join request already exists." });
            }

            var joinRequest = new JoinRequest
            {
                GroupChatId = groupChatId,
                UserId = userId,
                Status = JoinRequestStatus.Pending
            };

            appContext.JoinRequests.Add(joinRequest);
            await appContext.SaveChangesAsync();

            var user = await appContext.Users.FindAsync(userId);
            if (groupChat != null && user != null)
            {
                var notificationOptions = await appContext.NotificationOptions.FindAsync(groupChat.ModeratorId);
                if (notificationOptions != null && notificationOptions.ChatMessages)
                {
                    var notification = new Notification
                    {
                        Type = "InfoRequest",
                        SenderId = userId,
                        RecipientId = groupChat.ModeratorId,
                        Context = $"{user.UserName} has requested to join the group {groupChat.Name}!. Check group panel",
                        Timestamp = DateTime.Now
                    };

                    appContext.Notifications.Add(notification);
                    await appContext.SaveChangesAsync();
                }
            }

            return Ok(new { success = true });
        }

        [HttpGet]
        public async Task<IActionResult> GetJoinRequests(string groupChatId)
        {
            var joinRequests = await appContext.JoinRequests
                .Where(jr => jr.GroupChatId == groupChatId && jr.Status == JoinRequestStatus.Pending)
                .Include(jr => jr.User) // detalii despre utilizator
                .ToListAsync();

            var response = joinRequests.Select(jr => new
            {
                jr.JoinRequestId,
                UserId = jr.UserId,
                UserName = jr.User.UserName,
                Status = jr.Status.ToString(),
                RequestDate = jr.RequestDate
            });

            return Ok(response);
        }

        [HttpPost]
        public async Task<IActionResult> AcceptJoinRequest(int requestId, string notificationId)
        {
            var joinRequest = await appContext.JoinRequests.FindAsync(requestId);
            var notification = await appContext.Notifications.FindAsync(notificationId);
            if (joinRequest != null && notification != null)
            {
                var groupChat = await appContext.GroupChats.FindAsync(joinRequest.GroupChatId);
                if (groupChat != null)
                {
                    groupChat.Participants.Add(new GroupChatParticipant { UserId = joinRequest.UserId, GroupChatId = groupChat.GroupChatId, GroupChat = groupChat });
                    appContext.JoinRequests.Remove(joinRequest);
                    appContext.Notifications.Remove(notification);
                    await appContext.SaveChangesAsync();
                }
            }
            return Ok(new { success = true });
        }

        [HttpPost]
        public async Task<IActionResult> RejectJoinRequest(int requestId, string notificationId)
        {
            var joinRequest = await appContext.JoinRequests.FindAsync(requestId);
            var notification = await appContext.Notifications.FindAsync(notificationId);
            if (joinRequest != null && notification != null)
            {
                appContext.JoinRequests.Remove(joinRequest);
                appContext.Notifications.Remove(notification);
                await appContext.SaveChangesAsync();
            }
            return Ok(new { success = true });
        }

        [HttpPost]
        public async Task<IActionResult> ManageJoinRequest(string notificationId, string requestAction)
        {
            var joinRequest = await appContext.JoinRequests.FirstOrDefaultAsync(jr => jr.Notification.Id == notificationId);
            if (joinRequest == null)
            {
                return NotFound(new { success = false, message = "Join request not found." });
            }

            if (requestAction == "Accept")
            {
                return await AcceptJoinRequest(joinRequest.JoinRequestId, notificationId);
            }
            else if (requestAction == "Reject")
            {
                return await RejectJoinRequest(joinRequest.JoinRequestId, notificationId);
            }
            else
            {
                return BadRequest(new { success = false, message = "Invalid action." });
            }
        }

        [HttpPost]
        public async Task<IActionResult> ManageSelfJoinRequest(int joinRequestId, string requestAction)
        {
            var joinRequest = await appContext.JoinRequests.Include(jr => jr.User).FirstOrDefaultAsync(jr => jr.JoinRequestId == joinRequestId);

            if (joinRequest == null)
            {
                return NotFound(new { success = false, message = "Join request not found." });
            }

            if (requestAction == "Accept")
            {
                var groupChat = await appContext.GroupChats.FindAsync(joinRequest.GroupChatId);
                if (groupChat == null)
                {
                    return NotFound(new { success = false, message = "Group chat not found." });
                }

                var participant = new GroupChatParticipant
                {
                    UserId = joinRequest.UserId,
                    GroupChatId = groupChat.GroupChatId,
                    GroupChat = groupChat
                };

                groupChat.Participants.Add(participant);
                appContext.JoinRequests.Remove(joinRequest);
                await appContext.SaveChangesAsync();

                string? sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);

                // Trimite notificare utilizatorului
                var notificationOptions = await appContext.NotificationOptions.FindAsync(joinRequest.UserId);
                if (notificationOptions != null && notificationOptions.ChatMessages)
                {
                    var notification = new Notification
                    {
                        SenderId = sessionUser,
                        RecipientId = joinRequest.UserId,
                        Context = $"You have been accepted into the group '{groupChat.Name}'.",
                        Timestamp = DateTime.Now
                    };
                    appContext.Notifications.Add(notification);
                    await appContext.SaveChangesAsync();
                }

                return Ok(new { success = true});
            }
            else if (requestAction == "Reject")
            {
                appContext.JoinRequests.Remove(joinRequest);
                await appContext.SaveChangesAsync();

                string? sessionUser = User.FindFirstValue(ClaimTypes.NameIdentifier);

                // Trimite notificare utilizatorului
                var notificationOptions = await appContext.NotificationOptions.FindAsync(joinRequest.UserId);
                if (notificationOptions != null && notificationOptions.ChatMessages)
                {
                    var notification = new Notification
                    {
                        SenderId = sessionUser,
                        RecipientId = joinRequest.UserId,
                        Context = $"Your request to join the group '{joinRequest.GroupChatId}' has been rejected.",
                        Timestamp = DateTime.Now
                    };
                    appContext.Notifications.Add(notification);
                    await appContext.SaveChangesAsync();
                }

                return Ok(new { success = true });
            }
            else
            {
                return BadRequest(new { success = false, message = "Invalid action." });
            }
        }
    }
}
