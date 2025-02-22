using MicroSocial_Platform.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Identity.UI.Services; // IEmailSender
using MicroSocial_Platform.Services;
using static MicroSocial_Platform.WebSocketConfig;
using static ProfanityDetectionMiddleware;
using Microsoft.AspNetCore.Authentication.OAuth;


namespace MicroSocial_Platform
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.WebHost.UseUrls("https://0.0.0.0:7283");


            // Add services to the container.
            builder.Services.AddControllersWithViews();
            builder.Services.AddSignalR();

            //Baza de date
            var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
            builder.Services.AddDbContext<AppContext>(options =>
                options.UseSqlServer(connectionString));

            //Identity
            builder.Services.AddIdentity<User, IdentityRole>(options => options.SignIn.RequireConfirmedAccount = true).AddEntityFrameworkStores<AppContext>().AddDefaultTokenProviders().AddDefaultUI();

            builder.Services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = IdentityConstants.ApplicationScheme;
                options.DefaultChallengeScheme = IdentityConstants.ApplicationScheme;
            })
            .AddGoogle(options =>
            {
                options.ClientId = "";
                options.ClientSecret = "";
                options.Events = new OAuthEvents
                {
                    OnRemoteFailure = context =>
                    {
                        context.Response.Redirect("/Identity/Account/Login");
                        context.HandleResponse();
                        return Task.CompletedTask;
                    }
                };
                options.Scope.Add("email");
                options.Scope.Add("profile");
                options.SaveTokens = true;
            });



            // Adaug DI pentru service, create o data per request in scope
            builder.Services.AddScoped<IChatroomService, ChatroomService>();
            builder.Services.AddScoped<IGroupChatService, GroupChatService>();

            builder.Services.AddScoped<StoryCleanupService>();
            builder.Services.AddHostedService<StoryCleanupHostedService>();

            // Aici creez la fiecare request si e ok pentru ca e serviciu stateless
            // https://softwareengineering.stackexchange.com/questions/101337/whats-the-difference-between-stateful-and-stateless
            builder.Services.AddTransient<IEmailSender, EmailSender>();
            builder.Services.Configure<SendGridOptions>(builder.Configuration.GetSection("SendGrid"));

            var app = builder.Build();

            // Dupa terminarea blocului toate resursele sunt dealocate
            using (var scope = app.Services.CreateScope())
            {
                var services = scope.ServiceProvider;
                SeedData.Initialize(services);
            }

            // Configure the HTTP request pipeline.
            if (!app.Environment.IsDevelopment())
            {
                app.UseExceptionHandler("/Home/Error");
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }


            app.UseHttpsRedirection();
            app.UseStaticFiles();

            app.UseRouting();

            app.UseCors("CorsPolicy");

            app.UseAuthentication(); // Sistemul de auth
            app.UseAuthorization();

            // Middleware-urile trebuiesc definite dupa ce s-au initializat celelalte siteme
            //app.UseMiddleware<UpdateLastActive>();
            //MLModelLoader.InitializeModel();
            //app.UseMiddleware<ProfanityDetectionMiddleware>();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapHub<MessageBroker>("/ws");
            });

            // TL;DR mai jos, o gramada de rute, la 534 se termina

            // Authentication
            app.MapControllerRoute(
                name: "login",
                pattern: "auth/login",
                defaults: new { controller = "Auth", action = "Login" });

            app.MapControllerRoute(
                name: "register",
                pattern: "auth/register",
                defaults: new { controller = "Auth", action = "Register" });

            app.MapControllerRoute(
                name: "logout",
                pattern: "auth/logout",
                defaults: new { controller = "Auth", action = "Logout" });

            // User Profile

            app.MapControllerRoute(
                name: "profile-edit",
                pattern: "profile/edit/{id?}",
                defaults: new { controller = "Profile", action = "Edit" });

            app.MapControllerRoute(
                name: "profile-edit-action",
                pattern: "profile/edit-action",
                defaults: new { controller = "Profile", action = "EditProfile" });

            app.MapControllerRoute(
                name: "profile",
                pattern: "profile/{userId?}",
                defaults: new { controller = "Profile", action = "Index" });

            //Posts
            app.MapControllerRoute(
                name: "create-post",
                pattern: "post/create",
                defaults: new { controller = "Post", action = "Index" });

            app.MapControllerRoute(
                name: "add-post",
                pattern: "post/add",
                defaults: new { controller = "Post", action = "Create" });

            app.MapControllerRoute(
                name: "delete-post",
                pattern: "post/delete/{id}",
                defaults: new { controller = "Post", action = "Delete" });

            app.MapControllerRoute(
                name: "post-detail",
                pattern: "post/detail/{id}",
                defaults: new { controller = "Post", action = "Detail" });

            app.MapControllerRoute(
                name: "edit-post",
                pattern: "post/edit/{id}",
                defaults: new { controller = "Post", action = "Edit" });

            app.MapControllerRoute(
                name: "save-post",
                pattern: "post/save/{postId}",
                defaults: new { controller = "Post", action = "SavePost" });

            app.MapControllerRoute(
                name: "remove-saved-post",
                pattern: "post/remove-saved/{postId}/{redirectString}",
                defaults: new { controller = "Post", action = "RemoveSavedPost" });

            // Comments
            app.MapControllerRoute(
                name: "add-comment",
                pattern: "comment/add/{postId}/{content}",
                defaults: new { controller = "Comment", action = "Add" });

            app.MapControllerRoute(
                name: "edit-comment",
                pattern: "comment/edit/{id}/{commentId}",
                defaults: new { controller = "Comment", action = "Edit" });

            app.MapControllerRoute(
                name: "delete-comment",
                pattern: "comment/delete/{id}/{postId}",
                defaults: new { controller = "Comment", action = "Delete" });

            // Stories
            app.MapControllerRoute(
                name: "create-story",
                pattern: "story/create",
                defaults: new { controller = "Story", action = "Create" });

            app.MapControllerRoute(
                name: "view-story",
                pattern: "story/{id}",
                defaults: new { controller = "Story", action = "Index" });

            app.MapControllerRoute(
                name: "delete-story",
                pattern: "story/delete/{id}",
                defaults: new { controller = "Story", action = "Delete" });

            // Messaging
            app.MapControllerRoute(
                name: "messages",
                pattern: "messages",
                defaults: new { controller = "Message", action = "Inbox" });

            app.MapControllerRoute(
                name: "message-thread",
                pattern: "messages/{conversationId}",
                defaults: new { controller = "Message", action = "Conversation" });

            app.MapControllerRoute(
                name: "send-message",
                pattern: "messages/send",
                defaults: new { controller = "Message", action = "Send" });

            // Notifications
            app.MapControllerRoute(
                name: "notifications",
                pattern: "notifications",
                defaults: new { controller = "Notification", action = "Index" });

            app.MapControllerRoute(
                name: "mark-notification-read",
                pattern: "notifications/read/{id}",
                defaults: new { controller = "Notification", action = "MarkRead" });

            app.MapControllerRoute(
                name: "getJoinRequests",
                pattern: "GroupChat/GetJoinRequests",
                defaults: new { controller = "GroupChat", action = "GetJoinRequests" });

            app.MapControllerRoute(
                name: "acceptJoinRequest",
                pattern: "GroupChat/AcceptJoinRequest/{requestId}",
                defaults: new { controller = "GroupChat", action = "AcceptJoinRequest" });

            app.MapControllerRoute(
                name: "rejectJoinRequest",
                pattern: "GroupChat/RejectJoinRequest/{requestId}",
                defaults: new { controller = "GroupChat", action = "RejectJoinRequest" });

            app.MapControllerRoute(
                name: "manageJoinRequest",
                pattern: "GroupChat/ManageJoinRequest/{notificationId}/{requestAction}",
                defaults: new { controller = "GroupChat", action = "ManageJoinRequest" });

            // Saved Posts
            app.MapControllerRoute(
                name: "saved-posts",
                pattern: "saved",
                defaults: new { controller = "SavedPost", action = "Index" });

            app.MapControllerRoute(
                name: "save-post",
                pattern: "saved/save/{id}",
                defaults: new { controller = "SavedPost", action = "Save" });

            app.MapControllerRoute(
                name: "unsave-post",
                pattern: "saved/unsave/{id}",
                defaults: new { controller = "SavedPost", action = "Unsave" });

            // Rute pt Following/Followers
            app.MapControllerRoute(                                                                                     
                name: "follow",
                pattern: "FollowProfile/{id?}",
                defaults: new { controller = "Follow", action = "FollowProfile" });

            app.MapControllerRoute(                                                                                         
                name: "unfollow",
                pattern: "UnFollowProfile/{id?}",
                defaults: new { controller = "Follow", action = "UnFollowProfile" });

            // Search
            app.MapControllerRoute(
                name: "searchTags",
                pattern: "SearchEngine/SearchTags/{tagText}",
                defaults: new { controller = "SearchEngine", action = "SearchTags" }
            );

            app.MapControllerRoute(
                name: "searchUsers",
                pattern: "SearchEngine/SearchUsers/{userNameText}",
                defaults: new { controller = "SearchEngine", action = "SearchUsers" }
            );

            // like
            app.MapControllerRoute(
                name: "likePost",
                pattern: "like/{contentType}/{contentId}/{postId}",
                defaults: new { controller = "Like", action = "Index" }
            );

            // answers
            app.MapControllerRoute(
                name: "answerComment",
                pattern: "answer/add/{commentId}/{content}/{contentId}/{parentAnswerId?}",
                defaults: new { controller = "Answer", action = "Add" }
            );

            app.MapControllerRoute(
                name: "editAnswer",
                pattern: "answer/edit/{answerId}/{content}",
                defaults: new { controller = "Answer", action = "Edit" }
            );

            app.MapControllerRoute(
                name: "deleteAnswer",
                pattern: "answer/delete/{answerId}/{typeOfAnswer}",
                defaults: new { controller = "Answer", action = "Delete" }
            );

            // Messages
            app.MapControllerRoute(
                name: "message-section",
                pattern: "chat/messages",
                defaults: new { controller = "Message", action = "Index" }
            );

            app.MapControllerRoute(
                name: "chatroom",
                pattern: "Chatroom/GetChatroomID/{senderId}/{recipientId}/{createRoom}",
                defaults: new { controller = "Chatroom", action = "GetChatroomID" });

            app.MapControllerRoute(
                name: "getChatMessages",
                pattern: "ChatMessage/getChatMessages/{chatId}",
                defaults: new { controller = "ChatMessage", action = "FindChatMessages" });

            app.MapControllerRoute(
                name: "saveChatMessage",
                pattern: "ChatMessage/saveChatMessage/{messageContent}/{chatId}/{senderId}/{recipientId}",
                defaults: new { controller = "ChatMessage", action = "Save" });

            app.MapControllerRoute(
                name: "userChatrooms",
                pattern: "Chatroom/GetUserChatrooms/{userId}",
                defaults: new { controller = "Chatroom", action = "GetUserChatrooms" });

            app.MapControllerRoute(
                name: "editChatMessage",
                pattern: "chat/messages/edit/{chatId}/{messageId}/{senderId}/{newContent}",
                defaults: new { controller = "ChatMessage", action = "EditChatMessage" });

            app.MapControllerRoute(
                name: "deleteChatMessage",
                pattern: "chat/messages/delete/{chatId}/{messageId}/{senderId}",
                defaults: new { controller = "ChatMessage", action = "DeleteChatMessage" });

            // Groups
            app.MapControllerRoute(
                name: "sendGroupMessage",
                pattern: "groupchat/messages/send/{groupId}/{senderId}/{content}",
                defaults: new { controller = "GroupChatMessage", action = "SendGroupMessage" });

            app.MapControllerRoute(
                name: "getGroupMessages",
                pattern: "groupchat/messages/get/{groupId}",
                defaults: new { controller = "GroupChatMessage", action = "GetGroupMessages" });

            app.MapControllerRoute(
                name: "createGroup",
                pattern: "groupchat/create/{groupName}/{moderatorId}/{userIds}/{visibility}/{groupDescription}",
                defaults: new { controller = "GroupChat", action = "CreateGroup" });

            app.MapControllerRoute(
                name: "editGroup",
                pattern: "groupchat/edit/{groupId}/{userId}/{groupName}/{groupDescription}/{visibility}",
                defaults: new { controller = "GroupChat", action = "EditGroup" });

            app.MapControllerRoute(
                name: "availableGroups",
                pattern: "groupchat/available/{userId}",
                defaults: new { controller = "GroupChat", action = "AvailableGroups" });

            app.MapControllerRoute(
                name: "addGroupParticipant",
                pattern: "groupchat/addParticipant/{groupId}/{userId}",
                defaults: new { controller = "GroupChat", action = "AddParticipant" });

            app.MapControllerRoute(
                name: "removeGroupParticipant",
                pattern: "groupchat/removeParticipant/{groupId}/{userId}/{sendNotification?}",
                defaults: new { controller = "GroupChat", action = "RemoveParticipant" });

            app.MapControllerRoute(
                name: "getGroupParticipants",
                pattern: "groupchat/getParticipants/{groupId}",
                defaults: new { controller = "GroupChat", action = "GetGroupParticipants" });

            app.MapControllerRoute(
                name: "getUserGroups",
                pattern: "groupchat/getUserGroups/{userId}",
                defaults: new { controller = "GroupChat", action = "GetUserGroups" });

            app.MapControllerRoute(
                name: "deleteGroup",
                pattern: "groupchat/deleteGroup/{groupChatId}/{userId}",
                defaults: new { controller = "GroupChat", action = "DeleteGroup" });

            // Request-uri
            app.MapControllerRoute(
                name: "joinRequest",
                pattern: "groupchat/join-request/{groupChatId}/{userId}",
                defaults: new { controller = "GroupChat", action = "SendJoinRequest" });

            app.MapControllerRoute(
                name: "getJoinRequests",
                pattern: "groupchat/join-requests/{groupChatId}",
                defaults: new { controller = "GroupChat", action = "GetJoinRequests" });

            app.MapControllerRoute(
                name: "manageJoinRequest",
                pattern: "groupchat/manage-join-request/{joinRequestId}/{requestAction}",
                defaults: new { controller = "GroupChat", action = "ManageJoinRequest" });

            app.MapControllerRoute(
                name: "manageJoinRequest",
                pattern: "groupchat/manage-group-join-request/{joinRequestId}/{requestAction}",
                defaults: new { controller = "GroupChat", action = "ManageSelfJoinRequest" });

            app.MapControllerRoute(
                name: "setModerator",
                pattern: "groupchat/setModerator/{groupChatId}/{userId}/{userAction}",
                defaults: new { controller = "GroupChat", action = "SetModerator" });

            app.MapControllerRoute(
                name: "editGroupMessage",
                pattern: "groupchat/messages/edit/{groupChatId}/{messageId}/{newContent}/{userId}",
                defaults: new { controller = "GroupChatMessage", action = "EditGroupMessage" });

            app.MapControllerRoute(
                name: "deleteGroupMessage",
                pattern: "groupchat/messages/delete/{groupChatId}/{messageId}/{userId}",
                defaults: new { controller = "GroupChatMessage", action = "DeleteGroupMessage" });


            app.MapControllerRoute(
                name: "getGroupNotifications",
                pattern: "groupchat/notifications/{userId}",
                defaults: new { controller = "GroupChatNotification", action = "GetGroupNotificationsAsync" });

            //Stergerea unui cont

            app.MapControllerRoute(
                name: "deleteAccount",
                pattern: "profile/deleteaccount/{userId}",
                defaults: new { controller = "Profile", action = "DeleteAccount" });

            // Inbox
            app.MapControllerRoute(                                                                                 
                name: "Inbox",
                pattern: "Inbox/Index",
                defaults: new { controller = "Inbox", action = "Index" }
            );
            // Setting
            app.MapControllerRoute(                                                                                 
                name: "Setting",
                pattern: "Setting/Edit/{userId?}",
                defaults: new { controller = "Setting", action = "Edit" }
            );

            // Apeluri audio si video

            app.MapControllerRoute(
                name: "videocall",
                pattern: "conference/videocall/{userId}/{roomId?}",
                defaults: new { controller = "Conference", action = "VideoCall" });

            app.MapControllerRoute(
                name: "conference",
                pattern: "conference",
                defaults: new { controller = "Conference", action = "Index" });

            // Mesaje vocale si media
            app.MapControllerRoute(
                name: "sendVoiceMessage",
                pattern: "ChatMessage/SendVoiceMessage",
                defaults: new { controller = "ChatMessage", action = "SendVoiceMessage" });

            app.MapControllerRoute(
                name: "sendGroupVoiceMessage",
                pattern: "GroupChatMessage/SendVoiceMessage",
                defaults: new { controller = "GroupChatMessage", action = "SendGroupVoiceMessage" });

            app.MapControllerRoute(
                name: "sendMediaMessage",
                pattern: "ChatMessage/SendMediaMessage",
                defaults: new { controller = "ChatMessage", action = "SendMediaMessage" });

            app.MapControllerRoute(
                name: "sendGroupMediaMessage",
                pattern: "GroupChatMessage/SendGroupMediaMessage",
                defaults: new { controller = "GroupChatMessage", action = "SendGroupMediaMessage" });

            // FY si Discovery
            app.MapControllerRoute(
                name: "foryou",
                pattern: "api/discovery/foryou",
                defaults: new { controller = "Discovery", action = "ForYou" }
            );

            app.MapControllerRoute(
                name: "explore",
                pattern: "api/discovery/explore",
                defaults: new { controller = "Discovery", action = "Explore" }
            );

            // Report
            app.MapControllerRoute(
                name: "report",
                pattern: "report/submit",
                defaults: new { controller = "Report", action = "Submit" }
            );

            // END

            app.MapControllerRoute(
                name: "default",
                pattern: "{controller=Home}/{action=Index}/{id?}");

            app.MapRazorPages(); // Razor Pages pt Identity

            app.Run();

        }
    }
}
