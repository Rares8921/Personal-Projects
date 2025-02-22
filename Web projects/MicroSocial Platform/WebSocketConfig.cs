using Microsoft.AspNetCore.SignalR; // Hub
using System.Text.Json;

namespace MicroSocial_Platform
{

    // Stomp este echivalent cu SignalR
    // Stomp = Simple Text Oriented Messaging Protocol
    // Streamline websocket
    public class WebSocketConfig
    {

        // Make data accessible to third parties
        public class MessageBroker : Hub
        {
            public async Task SendMessage(string userId, string userName, DateTime timeStamp, string content, int id)
            {
                try
                {
                    await Clients.All.SendAsync("Message_Received", userId, userName, timeStamp, content, id);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error sending message: {ex.Message}");
                    throw; // exceptia sa fie trimisa la client
                }
            }

            public async Task UpdateChat(string chatId, string senderId, string senderName, string recipientName, string recipientId)
            {
                try
                {
                    await Clients.All.SendAsync("UdpateChat_Received", chatId, senderId, senderName, recipientName, recipientId);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error sending media message: {ex.Message}");
                    throw;
                }
            }
            public async Task DeleteMessage(string userId, int messageId)
            {
                try
                {
                    await Clients.All.SendAsync("Message_Deleted", userId, messageId);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error deleting message: {ex.Message}");
                    throw;
                }
            }
        }
        
        public static void ConfigureServices(IServiceCollection services)
        {
            services.AddSignalR().AddJsonProtocol(options => {
                options.PayloadSerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.CamelCase;
            });
            //https://aws.amazon.com/what-is/cross-origin-resource-sharing/
            // Set message and data resolver&converter to JSON
            services.AddCors(options => {
                options.AddPolicy("CorsPolicy", builder => {
                    builder.AllowAnyHeader().AllowAnyMethod().AllowAnyOrigin().SetIsOriginAllowed(_ => true).AllowCredentials();
                });
            });
        }

        public static void ConfigureStompEndpoints(IApplicationBuilder app)
        {
            app.UseCors("CorsPolicy");
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapHub<MessageBroker>("/ws"); // Similar STOMP endpoint
            });
        }
    }
}
