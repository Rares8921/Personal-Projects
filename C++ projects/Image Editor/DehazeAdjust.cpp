#include "DehazeAdjust.h"

void DehazeAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void DehazeAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    float intensity = 0.5f + (float)value / 100.0f * 1.5f;

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];

            int r = (int)((pixel.r - 127.0f) * intensity + 127.0f);
            int g = (int)((pixel.g - 127.0f) * intensity + 127.0f);
            int b = (int)((pixel.b - 127.0f) * intensity + 127.0f);

            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;

            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
}
