namespace MicroSocial_Platform.Models
{
    public class PostSaved
    {
        public string UserId { get; set; }
        public int PostId { get; set; } 

        public virtual required User User { get; set; }
        public virtual required Post Post { get; set; }

        public DateTime Timestamp { get; set; }
    }

}
