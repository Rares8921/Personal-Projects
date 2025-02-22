using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class ChatNotification
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string SenderId { get; set; }

        [Required]
        public required string RecipientId { get; set; }

        [Required]
        public required string Content { get; set; }
    }
}
