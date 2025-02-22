using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class Chatroom
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string ChatId { get; set; }

        [Required]
        public required string SenderId { get; set; }

        [Required]
        public required string RecipientId { get; set; }

        [Required]
        public bool IsGroup { get; set; } = false;

        public virtual ICollection<ChatroomParticipant> Participants { get; set; } = new List<ChatroomParticipant>();
    }
}
