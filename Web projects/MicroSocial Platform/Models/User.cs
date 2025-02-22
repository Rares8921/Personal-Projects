using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;

namespace MicroSocial_Platform.Models
{
    public class User : IdentityUser
    {
        public DateTime AccountCreation { get; set; } = DateTime.Now; // adica va fi generat automat la creare contului
        public DateTime LastActive { get; set; }

        [Required]
        public required DateTime DateOfBirth { get; set;  }
        [Required]
        public required string Biography { get; set; }
        public byte[]? ProfilePicture { get; set; }
        [Required]
        public required bool PrivateAccount { get; set; }

        //Lazy loading
        public virtual ICollection<Post> Posts { get; set; } = new List<Post>();
        public virtual ICollection<PostSaved> SavedPosts { get; set; } = new List<PostSaved>();

    }
}
