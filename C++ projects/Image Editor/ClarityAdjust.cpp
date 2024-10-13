#include "ClarityAdjust.h"

void ClarityAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void ClarityAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Adjustez in functie de formele geometrice gasite in imagine
    Color* tempPixels = new Color[height * width];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            // Iau pixelul curent si vecinii
            Color pixel = imagePixels[y * width + x];
            Color leftPixel = (x > 0) ? imagePixels[y * width + (x - 1)] : pixel;
            Color rightPixel = (x < width - 1) ? imagePixels[y * width + (x + 1)] : pixel;
            Color topPixel = (y > 0) ? imagePixels[(y - 1) * width + x] : pixel;
            Color bottomPixel = (y < height - 1) ? imagePixels[(y + 1) * width + x] : pixel;

            // Avg dintre vecini
            int avgR = (leftPixel.r + rightPixel.r + topPixel.r + bottomPixel.r) / 4;
            int avgG = (leftPixel.g + rightPixel.g + topPixel.g + bottomPixel.g) / 4;
            int avgB = (leftPixel.b + rightPixel.b + topPixel.b + bottomPixel.b) / 4;

            int r = pixel.r + value * (pixel.r - avgR);
            int g = pixel.g + value * (pixel.g - avgG);
            int b = pixel.b + value * (pixel.b - avgB);

            // Clamp
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;

            tempPixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
    for (int i = 0; i < height * width; ++i) {
        imagePixels[i] = tempPixels[i];
    }
    delete[] tempPixels;
}
