#include "ExposureAdjust.h"

void ExposureAdjust::adjustPixel(Color& imagePixel, int value) { }

void ExposureAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Calculez factorul de exposure
    float factor = powf(2.0f, value / 100.0f);

    for (int i = 0; i < height * width; i++) {
        float r = imagePixels[i].r / 255.0f * factor;
        float g = imagePixels[i].g / 255.0f * factor;
        float b = imagePixels[i].b / 255.0f * factor;

        r = fminf(fmaxf(r, 0.0f), 1.0f);
        g = fminf(fmaxf(g, 0.0f), 1.0f);
        b = fminf(fmaxf(b, 0.0f), 1.0f);

        imagePixels[i].r = (unsigned char)(r * 255.0f);
        imagePixels[i].g = (unsigned char)(g * 255.0f);
        imagePixels[i].b = (unsigned char)(b * 255.0f);
    }
}
