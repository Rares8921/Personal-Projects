#include "ShadowsAdjust.h"

void ShadowsAdjust::adjustPixel(Color& imagePixel, int value) {}

void ShadowsAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];
            // Aduc culoarea cat mai aproape de 0,0,0
            int r = pixel.r - value;
            int g = pixel.g - value;
            int b = pixel.b - value;

            // clamp
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
}
