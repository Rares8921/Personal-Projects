#include "SaturationAdjust.h"
#include <raymath.h>

void SaturationAdjust::adjustPixel(Color& imagePixel, int value){}

void SaturationAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Referinta: https://en.wikipedia.org/wiki/HSL_and_HSV
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];

            // de la rgb la hsl
            float r = pixel.r / 255.0f;
            float g = pixel.g / 255.0f;
            float b = pixel.b / 255.0f;

            float cmax = fmaxf(fmaxf(r, g), b);
            float cmin = fminf(fminf(r, g), b);
            float delta = cmax - cmin;

            float hue = 0.0f;
            if (delta != 0) {
                if (cmax == r) hue = fmodf(((g - b) / delta), 6.0f);
                else if (cmax == g) hue = ((b - r) / delta) + 2.0f;
                else if (cmax == b) hue = ((r - g) / delta) + 4.0f;
                hue *= 60.0f;
                if (hue < 0) hue += 360.0f;
            }

            float lightness = (cmax + cmin) / 2.0f;

            // Schimb saturatia
            float newSaturation = fminf(fmaxf(value, -1.0f), 1.0f);
            float chroma = (1 - fabsf(2 * lightness - 1)) * newSaturation;
            float hueTemp = hue / 60.0f;
            float chromaX = chroma * (1 - fabsf(fmodf(hueTemp, 2) - 1));
            float m = lightness - chroma / 2.0f; 

            float rNew, gNew, bNew;
            if (hueTemp < 1) {
                rNew = chroma;
                gNew = chromaX;
                bNew = 0;
            }
            else if (hueTemp < 2) {
                rNew = chromaX;
                gNew = chroma;
                bNew = 0;
            }
            else if (hueTemp < 3) {
                rNew = 0;
                gNew = chroma;
                bNew = chromaX;
            }
            else if (hueTemp < 4) {
                rNew = 0;
                gNew = chromaX;
                bNew = chroma;
            }
            else if (hueTemp < 5) {
                rNew = chromaX;
                gNew = 0;
                bNew = chroma;
            }
            else {
                rNew = chroma;
                gNew = 0;
                bNew = chromaX;
            }

            pixel.r = (rNew + m) * 255.0f;
            pixel.g = (gNew + m) * 255.0f;
            pixel.b = (bNew + m) * 255.0f;

            // Assign the modified color back to the pixel
            imagePixels[y * width + x] = pixel;
        }
    }
}
