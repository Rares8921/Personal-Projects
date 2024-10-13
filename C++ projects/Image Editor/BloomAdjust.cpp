#include "BloomAdjust.h"

void BloomAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void BloomAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    int threshold = (int)((255 * value) / 100);
    float intensity = (float)value / 100.0f;

    Color* tempPixels = (Color*)malloc(width * height * sizeof(Color));
    memcpy(tempPixels, imagePixels, width * height * sizeof(Color));

    for (int i = 0; i < width * height; i++) {
        Color pixel = imagePixels[i];
        if (pixel.r > threshold || pixel.g > threshold || pixel.b > threshold) {
            tempPixels[i].r = (unsigned char)fminf(pixel.r + (255 - pixel.r) * intensity, 255);
            tempPixels[i].g = (unsigned char)fminf(pixel.g + (255 - pixel.g) * intensity, 255);
            tempPixels[i].b = (unsigned char)fminf(pixel.b + (255 - pixel.b) * intensity, 255);
        }
    }

    // Acum aplic gaussian blur
    int blurRadius = (int)(5 * intensity);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            float r = 0, g = 0, b = 0;
            int count = 0;
            for (int dy = -blurRadius; dy <= blurRadius; dy++) {
                for (int dx = -blurRadius; dx <= blurRadius; dx++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        Color pixel = tempPixels[ny * width + nx];
                        r += pixel.r; g += pixel.g; b += pixel.b;
                        count++;
                    }
                }
            }
            r /= count; g /= count; b /= count;
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, tempPixels[y * width + x].a };
        }
    }
    free(tempPixels);
}
