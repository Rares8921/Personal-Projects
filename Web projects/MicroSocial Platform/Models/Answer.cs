using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MicroSocial_Platform.Models
{
    public class Answer
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int AnswerId { get; set; }
        public string UserId { get; set; } = "";
        public int CommentId { get; set; }

        [Required]
        public required string Content { get; set; }

        public int LikeCounter { get; set; }
        public DateTime TimeStamp {  get; set; } = DateTime.Now;

        public virtual required User User { get; set; }
        public virtual required Comment Comment { get; set; }

        // Raspunsul parinte &/ raspunsul copil
        public int? ParentAnswerId { get; set; } // e null daca nu exista raspuns la raspuns
        public virtual Answer? ParentAnswer { get; set; }
        public virtual ICollection<Answer> Replies { get; set; } = new List<Answer>();
    }

}
