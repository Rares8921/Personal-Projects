#include "HighlightsAdjust.h"

void HighlightsAdjust::adjustPixel(Color& imagePixel, int value){}

void HighlightsAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];
            unsigned char maxComponent = std::max(pixel.r, std::max(pixel.g, pixel.b));

            // Verific daca pixelul este deja luminat
            if (maxComponent > 255 * 0.7) { // threshold = 0.7, se poate schimba
                pixel.r = std::min((unsigned char)(pixel.r * value), (unsigned char)255);
                pixel.g = std::min((unsigned char)(pixel.g * value), (unsigned char)255);
                pixel.b = std::min((unsigned char)(pixel.b * value), (unsigned char)255);
            }

            imagePixels[y * width + x] = pixel;
        }
    }
}
