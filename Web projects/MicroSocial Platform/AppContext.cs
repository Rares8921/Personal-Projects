using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform
{
    public class AppContext : IdentityDbContext<User>
    {

        public DbSet<User> Users { get; set; }

        public DbSet<Follower> Followers { get; set; }

        public DbSet<Comment> Comments { get; set; }
        public DbSet<Post> Posts { get; set; }

        public DbSet<Story> Stories { get; set; }
        public DbSet<Chatroom> Chatrooms { get; set; }
        public DbSet<ChatMessage> ChatMessages { get; set; }
        public DbSet<ChatNotification> ChatNotifications { get; set; }
        public DbSet<ChatroomParticipant> ChatroomParticipants { get; set; }

        public DbSet<GroupChat> GroupChats {  get; set; }
        public DbSet<GroupChatMessage> GroupChatMessages { get; set; }
        public DbSet<GroupChatParticipant> GroupChatParticipants { get; set; }
        public DbSet<GroupChatNotification> GroupChatNotifications { get; set; }
        public DbSet<JoinRequest> JoinRequests { get; set; }
        public DbSet<Answer> Answers { get; set; }
        public DbSet <Like> Likes { get; set; }
        public DbSet<Report> Reports { get; set; }

        public DbSet<FollowEngine> FollowEngines { get; set; }
        public DbSet<NotificationOpt> NotificationOptions { get; set; }
        public DbSet<Notification> Notifications { get; set; }
        public DbSet<PostSaved> PostsSaved { get; set; }
        public  AppContext(DbContextOptions<AppContext> options) : base(options) { }


        /**
         * Entitatea USERS
         */

        // Update asincron in background pentru a nu interveni peste thread-ul aplicatiei
        public void addUser(User user)
        {
            new Thread(() =>
            {
                Users.Add(user);
            }).Start();
        }

        public void fetchUsers()
        {
            new Thread(() =>
            {
                foreach (User user in Users)
                {
                    Console.WriteLine("Fetched users: ");
                }
            }).Start();
        }

        // toate atributele care trebuie sa fie unice
        protected override void OnModelCreating(ModelBuilder modelBuilder) {
            base.OnModelCreating(modelBuilder);


            // Aici specific atributele care trebuie sa fie unice

            modelBuilder.Entity<Comment>().HasIndex(u => u.CommentId).IsUnique();


            modelBuilder.Entity<PostSaved>().HasKey(ps => new { ps.UserId, ps.PostId });

            modelBuilder.Entity<FollowEngine>().HasKey(fe => new { fe.User1, fe.User2 });
            modelBuilder.Entity<GroupChat>().HasKey(gc => gc.GroupChatId);
            modelBuilder.Entity<GroupChat>().HasIndex(gc => gc.GroupChatId).IsUnique();

            // many to many intre chatroom si user
            modelBuilder.Entity<ChatroomParticipant>().HasKey(cp => new { cp.ChatroomId, cp.UserId });

            modelBuilder.Entity<ChatroomParticipant>().HasOne(cp => cp.Chatroom).WithMany(c => c.Participants).HasForeignKey(cp => cp.ChatroomId);

            modelBuilder.Entity<ChatroomParticipant>().HasOne(cp => cp.User).WithMany().HasForeignKey(cp => cp.UserId);

            //GroupChat si GroupChatParticipant
            modelBuilder.Entity<GroupChatParticipant>().HasOne(gcp => gcp.GroupChat).WithMany(gc => gc.Participants).HasForeignKey(gcp => gcp.GroupChatId).OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<GroupChat>().HasMany(gc => gc.Participants).WithOne(gcp => gcp.GroupChat).HasForeignKey(gcp => gcp.GroupChatId);

            // Group si request-uri
            modelBuilder.Entity<JoinRequest>().HasOne(j => j.GroupChat).WithMany(g => g.JoinRequests).HasForeignKey(j => j.GroupChatId).OnDelete(DeleteBehavior.Cascade);

            // SavedPosts din User
            modelBuilder.Entity<PostSaved>().HasOne(ps => ps.User).WithMany(u => u.SavedPosts) .HasForeignKey(ps => ps.UserId).OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<PostSaved>().HasOne(ps => ps.Post).WithMany().HasForeignKey(ps => ps.PostId).OnDelete(DeleteBehavior.Restrict);

            //Aici setez cheile pentru subentitati sau entitati cu chei compuse
            modelBuilder.Entity<Content>().HasKey(c => c.ContentId);
            //modelBuilder.Entity<SavedPosts>().HasKey(sp => new { sp.UserId, sp.ContentId });
            //modelBuilder.Entity<Follower>().HasKey(f => new { f.FollowerUserId, f.FollowedUserId });

            // Un utilizator poate lasa mai multe comentarii, respectiv raspunsuri
            modelBuilder.Entity<Comment>().HasOne(c => c.User).WithMany().HasForeignKey(c => c.UserId).OnDelete(DeleteBehavior.NoAction);
            modelBuilder.Entity<Answer>().HasOne(a => a.User).WithMany().HasForeignKey(a => a.UserId).OnDelete(DeleteBehavior.NoAction);

            // Un utilizator poate salva mai multe postari
            modelBuilder.Entity<Follower>().HasOne(f => f.FollowerUser).WithMany().HasForeignKey(f => f.FollowerUserId).OnDelete(DeleteBehavior.NoAction);
            modelBuilder.Entity<Follower>().HasOne(f => f.FollowedUser).WithMany().HasForeignKey(f => f.FollowedUserId).OnDelete(DeleteBehavior.NoAction);

            // Pt delete-uri
            modelBuilder.Entity<Answer>().HasOne(a => a.User).WithMany().HasForeignKey(a => a.UserId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Like>().HasOne(l => l.User).WithMany().HasForeignKey(l => l.UserId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Like>().HasOne(l => l.Content).WithMany().HasForeignKey(l => l.ContentId).OnDelete(DeleteBehavior.Restrict);
            modelBuilder.Entity<Like>().HasOne(l => l.Comment).WithMany().HasForeignKey(l => l.CommentId).OnDelete(DeleteBehavior.Restrict);
            modelBuilder.Entity<Like>().HasOne(l => l.Answer).WithMany().HasForeignKey(l => l.AnswerId).OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<User>().HasMany(u => u.Posts).WithOne(p => p.User).HasForeignKey(p => p.UserId).OnDelete(DeleteBehavior.Cascade);

            //modelBuilder.Entity<Content>().HasOne(c => c.User).WithMany(u => u.Posts).HasForeignKey(c => c.UserId).OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Post>().HasOne(p => p.User).WithMany(u => u.Posts).HasForeignKey(p => p.UserId).OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Comment>().HasIndex(u => u.CommentId).IsUnique();
            modelBuilder.Entity<PostSaved>().HasKey(ps => new { ps.UserId, ps.PostId });
            modelBuilder.Entity<FollowEngine>().HasKey(fe => new { fe.User1, fe.User2 });
            modelBuilder.Entity<GroupChat>().HasKey(gc => gc.GroupChatId);
            modelBuilder.Entity<GroupChat>().HasIndex(gc => gc.GroupChatId).IsUnique();
            modelBuilder.Entity<ChatroomParticipant>().HasKey(cp => new { cp.ChatroomId, cp.UserId });
            modelBuilder.Entity<ChatroomParticipant>().HasOne(cp => cp.Chatroom).WithMany(c => c.Participants).HasForeignKey(cp => cp.ChatroomId);
            modelBuilder.Entity<ChatroomParticipant>().HasOne(cp => cp.User).WithMany().HasForeignKey(cp => cp.UserId);
            modelBuilder.Entity<GroupChatParticipant>().HasOne(gcp => gcp.GroupChat).WithMany(gc => gc.Participants).HasForeignKey(gcp => gcp.GroupChatId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<GroupChat>().HasMany(gc => gc.Participants).WithOne(gcp => gcp.GroupChat).HasForeignKey(gcp => gcp.GroupChatId);
            modelBuilder.Entity<JoinRequest>().HasOne(j => j.GroupChat).WithMany(g => g.JoinRequests).HasForeignKey(j => j.GroupChatId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<PostSaved>().HasOne(ps => ps.User).WithMany(u => u.SavedPosts).HasForeignKey(ps => ps.UserId).OnDelete(DeleteBehavior.Restrict);
            modelBuilder.Entity<PostSaved>().HasOne(ps => ps.Post).WithMany().HasForeignKey(ps => ps.PostId).OnDelete(DeleteBehavior.Restrict);
            modelBuilder.Entity<Content>().HasKey(c => c.ContentId);
            modelBuilder.Entity<Comment>().HasOne(c => c.User).WithMany().HasForeignKey(c => c.UserId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Answer>().HasOne(a => a.User).WithMany().HasForeignKey(a => a.UserId).OnDelete(DeleteBehavior.NoAction);
            modelBuilder.Entity<Follower>().HasOne(f => f.FollowerUser).WithMany().HasForeignKey(f => f.FollowerUserId).OnDelete(DeleteBehavior.NoAction);
            modelBuilder.Entity<Follower>().HasOne(f => f.FollowedUser).WithMany().HasForeignKey(f => f.FollowedUserId).OnDelete(DeleteBehavior.NoAction);
            modelBuilder.Entity<Answer>().HasOne(a => a.User).WithMany().HasForeignKey(a => a.UserId).OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Like>().HasOne(l => l.User).WithMany().HasForeignKey(l => l.UserId).OnDelete(DeleteBehavior.Cascade);

        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseLazyLoadingProxies();
        }
    }
}
