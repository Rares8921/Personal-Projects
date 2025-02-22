namespace MicroSocial_Platform.Services
{
    public class StoryCleanupHostedService : IHostedService, IDisposable
    {
        private readonly IServiceScopeFactory scopeFactory;
        private Timer timer;

        public StoryCleanupHostedService(IServiceScopeFactory _scopeFactory)
        {
            scopeFactory = _scopeFactory;
        }

        public Task StartAsync(CancellationToken cancellationToken)
        {
            timer = new Timer(DoWork, null, TimeSpan.Zero, TimeSpan.FromHours(1));
            return Task.CompletedTask;
        }

        private void DoWork(object state)
        {
            using (var scope = scopeFactory.CreateScope())
            {
                var cleanupService = scope.ServiceProvider.GetRequiredService<StoryCleanupService>();
                cleanupService.CleanupOldStoriesAsync().Wait();
            }
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            timer?.Change(Timeout.Infinite, 0);
            return Task.CompletedTask;
        }

        public void Dispose()
        {
            timer?.Dispose();
        }
    }
}
