#include "TemperatureAdjust.h"

void TemperatureAdjust::adjustPixel(Color& imagePixel, int value){}

void TemperatureAdjust::adjustImage(Color*& imagePixels, int height, int width, int temperature) {
    float mireds = 1000000.0f / temperature;
    // Referinte: https://en.wikipedia.org/wiki/Color_temperature
    // si https://im.snibgo.com/coltemp.htm
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];

            // convertesc la rgb liniar
            float r = powf(pixel.r / 255.0f, 2.2f);
            float g = powf(pixel.g / 255.0f, 2.2f);
            float b = powf(pixel.b / 255.0f, 2.2f);

            float delta = mireds / 100.0f - 10.0f;
            r += delta * (0.0108f * r - 0.0288f * g + 0.0216f * b);
            g += delta * (0.0108f * g - 0.0288f * r + 0.0216f * b);
            b += delta * (0.0108f * b - 0.0288f * r + 0.0216f * g);

            // inapoi la rgb simplu
            r = powf(r, 1.0f / 2.2f);
            g = powf(g, 1.0f / 2.2f);
            b = powf(b, 1.0f / 2.2f);

            // clamp
            r = fminf(fmaxf(r, 0.0f), 1.0f);
            g = fminf(fmaxf(g, 0.0f), 1.0f);
            b = fminf(fmaxf(b, 0.0f), 1.0f);

            pixel.r = (unsigned char)(r * 255.0f);
            pixel.g = (unsigned char)(g * 255.0f);
            pixel.b = (unsigned char)(b * 255.0f);
            imagePixels[y * width + x] = pixel;
        }
    }
}
