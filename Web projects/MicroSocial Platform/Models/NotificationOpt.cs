using System.ComponentModel.DataAnnotations;
using System.Linq.Expressions;

namespace MicroSocial_Platform.Models
{
    public class NotificationOpt
    {
        [Key]
        public string UserId { get; set; }
        public bool LD_Mode { get; set; } = true;      // true = dark ; false = light
        public bool ChatMessages { get; set; } = true;
        public bool LikesPost { get; set; } = true;
        public bool CommentsPost { get; set; } = true;
        public bool NewFollowers { get; set; } = true;
    }
}
