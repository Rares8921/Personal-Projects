using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{

    public class Follower
    {
        [Key]
        public int Id { get; set; }

        [ForeignKey(nameof(FollowerUser))]
        public string FollowerUserId { get; set; } = ""; // ID-ul user-ului care urmareste

        [ForeignKey(nameof(FollowedUser))]
        public string FollowedUserId { get; set; } = ""; // ID-ul user-ului urmarit

        public DateTime TimeStamp { get; set; }

        public virtual required User FollowerUser { get; set; }
        public virtual required User FollowedUser { get; set; }
    }

}
