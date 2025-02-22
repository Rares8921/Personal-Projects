using Microsoft.Extensions.Options;
using SendGrid.Helpers.Mail;
using Microsoft.AspNetCore.Identity.UI.Services;
using SendGrid;

namespace MicroSocial_Platform
{
    public class EmailSender : IEmailSender
    {
        private readonly SendGridOptions _options;

        public EmailSender(IOptions<SendGridOptions> options)
        {
            _options = options.Value;
        }

        public async Task SendEmailAsync(string email, string subject, string htmlMessage)
        {
            var client = new SendGridClient(_options.ApiKey);
            var from = new EmailAddress("sebimarcus23@gmail.com", "Rares Catalin");
            var to = new EmailAddress(email);
            var msg = MailHelper.CreateSingleEmail(from, to, subject, "", htmlMessage);
            await client.SendEmailAsync(msg);
        }
    }

    public class SendGridOptions
    {
        public required string ApiKey { get; set; }
    }
}
