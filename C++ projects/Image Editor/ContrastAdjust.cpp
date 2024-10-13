#include "ContrastAdjust.h"

void ContrastAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void ContrastAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    float exposure = 0.0f;
    float contrast = 0.0f;

    if (value < 0) {
        exposure = (float)value / 100.0f;
    } else {
        contrast = (float)value / 100.0f;
    }

    float exposureFactor = exposure * exposure;
    float contrastFactor = 1.0f + contrast;

    // Actualizez fiecare pixel
    for (int i = 0; i < height * width; i++) {
        float r = (float)imagePixels[i].r / 255.0f;
        float g = (float)imagePixels[i].g / 255.0f;
        float b = (float)imagePixels[i].b / 255.0f;

        r *= exposureFactor;
        g *= exposureFactor;
        b *= exposureFactor;

        r = ((r - 0.5f) * contrastFactor) + 0.5f;
        g = ((g - 0.5f) * contrastFactor) + 0.5f;
        b = ((b - 0.5f) * contrastFactor) + 0.5f;

        // clamp
        r = fminf(fmaxf(r, 0.0f), 1.0f);
        g = fminf(fmaxf(g, 0.0f), 1.0f);
        b = fminf(fmaxf(b, 0.0f), 1.0f);

        imagePixels[i].r = (unsigned char)(r * 255.0f);
        imagePixels[i].g = (unsigned char)(g * 255.0f);
        imagePixels[i].b = (unsigned char)(b * 255.0f);
    }
}
