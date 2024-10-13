#include "BlackAdjust.h"

void BlackAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void BlackAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    float blackAdjustFactor = value / 255.0f;

    for (int i = 0; i < height * width; i++) {
        float r = imagePixels[i].r / 255.0f;
        float g = imagePixels[i].g / 255.0f;
        float b = imagePixels[i].b / 255.0f;

        // black adjust
        r -= blackAdjustFactor;
        g -= blackAdjustFactor;
        b -= blackAdjustFactor;

        r = fminf(fmaxf(r, 0.0f), 1.0f);
        g = fminf(fmaxf(g, 0.0f), 1.0f);
        b = fminf(fmaxf(b, 0.0f), 1.0f);

        // Convertire
        imagePixels[i].r = (unsigned char)(r * 255.0f);
        imagePixels[i].g = (unsigned char)(g * 255.0f);
        imagePixels[i].b = (unsigned char)(b * 255.0f);
    }
}
