#include "GrainAdjust.h"

void GrainAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void GrainAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Zgomotul maxim
    int maxNoise = (int)(255 * value);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {

            Color pixel = imagePixels[y * width + x];

            int noiseR = (rand() % (2 * maxNoise + 1)) - maxNoise;
            int noiseG = (rand() % (2 * maxNoise + 1)) - maxNoise;
            int noiseB = (rand() % (2 * maxNoise + 1)) - maxNoise;

            int r = (int)pixel.r + noiseR;
            int g = (int)pixel.g + noiseG;
            int b = (int)pixel.b + noiseB;

            // clamp
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;

            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
}
