using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class JoinRequest
    {
        public int JoinRequestId { get; set; }

        public string GroupChatId { get; set; }
        public virtual GroupChat GroupChat { get; set; }

        public string UserId { get; set; } = "";
        public virtual User User { get; set; }

        public virtual Notification Notification { get; set; }

        public JoinRequestStatus Status { get; set; } = JoinRequestStatus.Pending;

        public DateTime RequestDate { get; set; } = DateTime.Now;
    }

    public enum JoinRequestStatus
    {
        Pending,
        Approved,
        Rejected
    }

}
