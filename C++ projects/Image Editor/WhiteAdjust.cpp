#include "WhiteAdjust.h"

void WhiteAdjust::adjustPixel(Color& imagePixel, int value){}

void WhiteAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            // Get the current pixel color
            Color pixel = imagePixels[y * width + x];
            // Practic adjustez brigthness-ul
            pixel.r = Clamp(pixel.r + value, 0, 255);
            pixel.g = Clamp(pixel.g + value, 0, 255);
            pixel.b = Clamp(pixel.b + value, 0, 255);
            imagePixels[y * width + x] = pixel;
        }
    }
}
