using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MicroSocial_Platform.Models
{
    public class Content
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int ContentId { get; set; }

        public string UserId { get; set; } = "";

        public DateTime TimeStamp { get; set; }

        // Pentru viteza
        public virtual required User User { get; set; }
    }
}
