#include "VignetteAdjust.h"

void VignetteAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void VignetteAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    value /= 3; // Ca sa nu fie prea mult vignette
    // Calculez centrul imaginii
    float centerX = width / 2.0f;
    float centerY = height / 2.0f;

    // Distanta de la centru la colturi
    float maxDist = sqrt(centerX * centerX + centerY * centerY);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            // Distanta de la centrul imaginii la pixel
            float distX = x - centerX;
            float distY = y - centerY;
            float dist = sqrt(distX * distX + distY * distY);

            // Factor bazat pe distanta
            float factor = 1.0f - (dist / maxDist) * value;

            // clamp
            factor = (factor < 0.0f) ? 0.0f : (factor > 1.0f) ? 1.0f : factor;

            Color pixel = imagePixels[y * width + x];
            int r = (int)(pixel.r * factor);
            int g = (int)(pixel.g * factor);
            int b = (int)(pixel.b * factor);
            imagePixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, pixel.a };
        }
    }
}
