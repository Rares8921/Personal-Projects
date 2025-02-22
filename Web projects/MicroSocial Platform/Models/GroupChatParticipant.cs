using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class GroupChatParticipant
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string GroupChatId { get; set; }

        [Required]
        public required string UserId { get; set; }
        public virtual GroupChat GroupChat { get; set; }
    }

}
