using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class GroupChatNotification
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string GroupChatId { get; set; } 

        [Required]
        public required string SenderId { get; set; }

        [Required]
        public required string Content { get; set; }

        public DateTime Timestamp { get; set; } = DateTime.Now;
    }

}
