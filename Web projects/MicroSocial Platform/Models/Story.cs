using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class Story : Content
    {
        [Required]
        public string Name { get; set; }
        public byte[] MediaLinks { get; set; }
        public string MimeType { get; set; }
    }
}
