using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class GroupChat
    {

        [Key]
        [Required]
        public required string GroupChatId { get; set; }

        [Required]
        public required string Name { get; set; }

        public string? Description { get; set; }
        public GroupVisibility Visibility { get; set; } = GroupVisibility.Public;

        [Required]
        public required string ModeratorId { get; set; }

        public virtual ICollection<GroupChatParticipant> Participants { get; set; } = new List<GroupChatParticipant>();
        public virtual ICollection<JoinRequest> JoinRequests { get; set; } = new List<JoinRequest>();
    }

    public enum GroupVisibility
    {
        Public,
        Private
    }

}
