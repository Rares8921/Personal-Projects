using Microsoft.AspNetCore.Mvc;

namespace MicroSocial_Platform.Models
{
    public class ChatroomParticipant
    {
        public int ChatroomId { get; set; }
        public virtual required Chatroom Chatroom { get; set; }

        public required string UserId { get; set; }
        public virtual required User User { get; set; }
    }

}
