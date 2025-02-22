using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class Report
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public required string ReportContent { get; set; }

        public required string UserId { get; set; }
        public DateTime Timestamp { get; set; }

        public required virtual User User { get; set; }
    }
}
