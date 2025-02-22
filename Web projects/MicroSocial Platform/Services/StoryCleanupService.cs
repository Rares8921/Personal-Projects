using Microsoft.EntityFrameworkCore;

namespace MicroSocial_Platform.Services
{
    public class StoryCleanupService
    {
        private readonly AppContext appContext;

        public StoryCleanupService(AppContext context)
        {
            appContext = context;
        }

        public async Task CleanupOldStoriesAsync()
        {
            var limitDate = DateTime.Now.AddHours(-24);
            var oldStories = await appContext.Stories.Where(s => s.TimeStamp < limitDate).ToListAsync();

            if (oldStories.Any())
            {
                appContext.Stories.RemoveRange(oldStories);
                await appContext.SaveChangesAsync();
            }
        }
    }
}
