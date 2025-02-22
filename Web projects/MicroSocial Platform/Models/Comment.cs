namespace MicroSocial_Platform.Models
{
    public class Comment
    {
        public int CommentId { get; set; }
       
        public required string StringContent { get; set;}
        public int likeCounter { get; set; }
        public string UserId { get; set; } = "";
        public DateTime TimeStamp { get; set; } = DateTime.Now;

        // Pentru viteza
        public virtual required User User { get; set; }
        public virtual ICollection<Answer> Replies { get; set; } = new List<Answer>();
    }
}
