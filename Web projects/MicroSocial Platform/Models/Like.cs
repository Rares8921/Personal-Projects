using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MicroSocial_Platform.Models
{
    public class Like
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int LikeId { get; set; }
        public string UserId { get; set; } = "";
        public int? ContentId { get; set; }
        public int? CommentId { get; set; }
        public int? AnswerId { get; set; }

        public DateTime timeStamp;
       
        public virtual User? User { get; set; }
        public virtual Content? Content { get; set; }
        public virtual Comment? Comment { get; set; }
        public virtual Answer? Answer { get; set; }
    }
}
