#include "GlamourAdjust.h"

void GlamourAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void GlamourAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Glamour - vignette + blur
    if (value < 25) {
        return;
    }
    float blurIntensity = (float)value / 100.0f;
    float brightness = -0.2f + 0.4f * blurIntensity;
    float contrast = 1.0f + 0.5f * blurIntensity;
    float vignetteAmount = 0.5f * blurIntensity;

    // Calculez centrul
    float centerX = width / 2.0f;
    float centerY = height / 2.0f;

    // Distanta de la centru la colturi
    float maxDist = sqrt(centerX * centerX + centerY * centerY);

    Color* tempPixels = (Color*)malloc(width * height * sizeof(Color));

    // Kernel
    int blurRadius = (int)(5 * blurIntensity);
    float* kernel = (float*)malloc((2 * blurRadius + 1) * sizeof(float));
    float kernelSum = 0;
    for (int i = -blurRadius; i <= blurRadius; i++) {
        kernel[i + blurRadius] = expf(-(float)(i * i) / (2 * blurRadius * blurRadius));
        kernelSum += kernel[i + blurRadius];
    }
    // Normalizare
    for (int i = 0; i < 2 * blurRadius + 1; i++) {
        kernel[i] /= kernelSum;
    }

    // Blur orizontal
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0, g = 0, b = 0;
            for (int i = -blurRadius; i <= blurRadius; i++) {
                int nx = x + i;
                if (nx < 0) nx = 0;
                if (nx >= width) nx = width - 1;
                Color pixel = imagePixels[y * width + nx];
                float weight = kernel[i + blurRadius];
                r += pixel.r * weight;
                g += pixel.g * weight;
                b += pixel.b * weight;
            }
            tempPixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, imagePixels[y * width + x].a };
        }
    }

    // Blur vertical
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0, g = 0, b = 0;
            for (int i = -blurRadius; i <= blurRadius; i++) {
                int ny = y + i;
                if (ny < 0) ny = 0;
                if (ny >= height) ny = height - 1;
                Color pixel = tempPixels[ny * width + x];
                float weight = kernel[i + blurRadius];
                r += pixel.r * weight;
                g += pixel.g * weight;
                b += pixel.b * weight;
            }
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, tempPixels[y * width + x].a };
        }
    }

    free(tempPixels);
    free(kernel);

    // Acum aplic efectul
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            // Distanta de la centrul imaginii pana la pixelul curent
            float distX = x - centerX;
            float distY = y - centerY;
            float dist = sqrt(distX * distX + distY * distY);

            // Calculez vignette in functie de distanta
            float factor = 1.0f - (dist / maxDist) * vignetteAmount;
            factor = (factor < 0.0f) ? 0.0f : (factor > 1.0f) ? 1.0f : factor;

            Color pixel = imagePixels[y * width + x];

            int r = (int)(pixel.r * factor);
            int g = (int)(pixel.g * factor);
            int b = (int)(pixel.b * factor);

            r = (int)((r - 127.0f) * contrast + 127.0f + brightness);
            g = (int)((g - 127.0f) * contrast + 127.0f + brightness);
            b = (int)((b - 127.0f) * contrast + 127.0f + brightness);

            // clamp
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
}
