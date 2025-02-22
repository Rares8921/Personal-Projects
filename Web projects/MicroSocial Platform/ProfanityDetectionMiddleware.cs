using MicroSocial_Platform;
using Microsoft.ML;
using System.Text.Json;

public class ProfanityDetectionMiddleware
{
    public static class MLModelLoader
    {
        private static Lazy<PredictionEngine<MLModel.ModelInput, MLModel.ModelOutput>> _predictionEngine;

        static MLModelLoader()
        {
            _predictionEngine = new Lazy<PredictionEngine<MLModel.ModelInput, MLModel.ModelOutput>>(CreatePredictionEngine);
        }

        private static PredictionEngine<MLModel.ModelInput, MLModel.ModelOutput> CreatePredictionEngine()
        {
            var mlContext = new MLContext();
            ITransformer mlModel = mlContext.Model.Load("MLModel.mlnet", out var modelInputSchema);
            return mlContext.Model.CreatePredictionEngine<MLModel.ModelInput, MLModel.ModelOutput>(mlModel);
        }

        public static PredictionEngine<MLModel.ModelInput, MLModel.ModelOutput> PredictionEngine => _predictionEngine.Value;
        public static void InitializeModel()
        {
            var _ = PredictionEngine;
        }
    }

    private readonly RequestDelegate _next;

    public ProfanityDetectionMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        if ((context.Request.Method == HttpMethods.Post || context.Request.Method == HttpMethods.Put))
        {

            if (context.Request.ContentType == "application/json")
            {
                context.Request.EnableBuffering();
                using var reader = new StreamReader(context.Request.Body, leaveOpen: true);
                var body = await reader.ReadToEndAsync();

                var jsonData = JsonDocument.Parse(body);
                foreach (var property in jsonData.RootElement.EnumerateObject())
                {
                    if (property.Value.ValueKind == JsonValueKind.String)
                    {
                        string content = property.Value.GetString();
                        if (!string.IsNullOrWhiteSpace(content))
                        {
                            bool containsProfanity = await CheckProfanity(content);
                            if (containsProfanity)
                            {
                                await HandleProfanityResponse(context);
                                return;
                            }
                        }
                    }
                }

                context.Request.Body.Position = 0;
            }
        }

        await _next(context);
    }

    private async Task<bool> CheckProfanity(string text)
    {
        var sampleData = new MLModel.ModelInput()
        {
            text = text,
        };

        // Predict output using the preloaded model
        var result = MLModelLoader.PredictionEngine.Predict(sampleData);
        return result.Score[1] >= 0.781234;
    }

    private async Task HandleProfanityResponse(HttpContext context)
    {
        context.Response.StatusCode = 400;
        context.Response.ContentType = "application/json";
        var response = new { error = "The content contains inappropriate language." };
        await context.Response.WriteAsJsonAsync(response);
    }
}