#include "HueAdjust.h"

void HueAdjust::adjustPixel(Color& imagePixel, int value) {
}

void HueAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];

            // rgb to hsv
            float r = pixel.r / 255.0f;
            float g = pixel.g / 255.0f;
            float b = pixel.b / 255.0f;

            float cmax = fmaxf(fmaxf(r, g), b);
            float cmin = fminf(fminf(r, g), b);
            float delta = cmax - cmin;

            float hue = 0.0f;

            if (delta == 0) {
                hue = 0.0f;
            } else if (cmax == r) {
                hue = 60.0f * fmodf((g - b) / delta, 6.0f);
            } else if (cmax == g) {
                hue = 60.0f * ((b - r) / delta + 2.0f);
            } else {
                hue = 60.0f * ((r - g) / delta + 4.0f);
            }
            if (hue < 0.0f) {
                hue += 360.0f;
            }
            // Adjustez hue
            hue += value;
            if (hue >= 360.0f)
                hue -= 360.0f;
            else if (hue < 0.0f)
                hue += 360.0f;

            // hsv la rgb
            float h = hue / 60.0f;
            float s = (cmax == 0.0f) ? 0.0f : delta / cmax;
            float v = cmax;

            // In functie de ce hue s-a calculat, actualizez rgb-ul cu permutari din (p, q, t)
            int i = (int)floorf(h);
            float f = h - i;
            float p = v * (1.0f - s);
            float q = v * (1.0f - s * f);
            float t = v * (1.0f - s * (1.0f - f));

            switch (i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            default:
                r = v;
                g = p;
                b = q;
                break;
            }

            pixel.r = (unsigned char)(r * 255.0f);
            pixel.g = (unsigned char)(g * 255.0f);
            pixel.b = (unsigned char)(b * 255.0f);
            imagePixels[y * width + x] = pixel;
        }
    }
}

