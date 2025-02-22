using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MicroSocial_Platform.Models
{
    public class Post : Content
    {
        public required string TextContent { get; set; }
        // Link-urile media vor fi serializate, acestea sunt efectiv continutul dintr-o postare(clip, gif, poza etc)
        public byte[]? MediaLinks { get; set; } // aici sunt imaginile/clipurile din postare
        public int LikeCounter { get; set; }
        public string MimeType { get; set; } = "Image";
        public string EmbedUrl { get; set; } = "";
        public List<string> Tags { get; set; } = new List<string>();
        public virtual ICollection<Comment> PostComments { get; set; } = new List<Comment>();

    }
}
