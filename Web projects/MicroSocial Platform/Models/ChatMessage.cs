using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class ChatMessage
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string ChatId { get; set; }

        [Required]
        public required string SenderId { get; set; }

        [Required]
        public required string RecipientId { get; set; }

        public string? Content { get; set; }
        public byte[]? MediaContent { get; set; }
        public string? ContentMimeType { get; set; }

        [Required]
        public DateTime Timestamp { get; set; } = DateTime.Now;

    }
}
