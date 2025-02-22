using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class GroupChatMessage
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string GroupChatId { get; set; }

        [Required]
        public required string SenderId { get; set; }

        public string? Content { get; set; }
        public byte[]? MediaContent { get; set; }
        public string? ContentMimeType { get; set; }

        [Required]
        public DateTime Timestamp { get; set; } = DateTime.Now;
    }

}
