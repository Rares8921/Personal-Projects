using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MicroSocial_Platform.Models
{
    public class Notification
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public string Id { get; set; }
        public string Type { get; set; }
        public string RecipientId { get; set; }
        public string SenderId { get; set; }
        public string Context { get; set; }
        public bool IsRead { get; set; } = false;
        public DateTime Timestamp { get; set; }
    }
}
