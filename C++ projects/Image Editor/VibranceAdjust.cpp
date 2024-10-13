#include "VibranceAdjust.h"
#include <cmath>

void VibranceAdjust::adjustPixel(Color& imagePixel, int value) {}

void VibranceAdjust::adjustImage(Color*& imagePixels, int height, int width, int vibranceInt) {
    // Referinta: https://docs.opencv.org/3.1.0/de/d25/imgproc_color_conversions.html
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Color pixel = imagePixels[y * width + x];

            // Convert la hsv
            float r = pixel.r / 255.0f;
            float g = pixel.g / 255.0f;
            float b = pixel.b / 255.0f;

            float cmax = fmaxf(fmaxf(r, g), b);
            float cmin = fminf(fminf(r, g), b);
            float delta = cmax - cmin;

            float hue = 0.0f;
            if (delta != 0) {
                if (cmax == r) {
                    hue = fmodf(((g - b) / delta), 6.0f);
                } else if (cmax == g) {
                    hue = ((b - r) / delta) + 2.0f;
                } else if (cmax == b) {
                    hue = ((r - g) / delta) + 4.0f;
                }
                hue *= 60.0f;
                if (hue < 0) {
                    hue += 360.0f;
                }
            }

            float saturation = (cmax == 0) ? 0 : delta / cmax;
            float value = cmax;

            saturation += (vibranceInt / 100.0f);
            saturation = fminf(fmaxf(saturation, 0.0f), 1.0f);

            // hsv la rgb
            int i;
            float f, p, q, t;
            if (saturation == 0) {
                pixel.r = pixel.g = pixel.b = value * 255;
            } else {
                hue /= 60;
                i = (int)hue;
                f = hue - i;
                p = value * (1 - saturation);
                q = value * (1 - saturation * f);
                t = value * (1 - saturation * (1 - f));
                switch (i) {
                case 0: 
                    pixel.r = value * 255; pixel.g = t * 255; pixel.b = p * 255;
                    break;
                case 1: 
                    pixel.r = q * 255; pixel.g = value * 255; pixel.b = p * 255; 
                    break;
                case 2:
                    pixel.r = p * 255; pixel.g = value * 255; pixel.b = t * 255; 
                    break;
                case 3: 
                    pixel.r = p * 255; pixel.g = q * 255; pixel.b = value * 255; 
                    break;
                case 4: 
                    pixel.r = t * 255; pixel.g = p * 255; pixel.b = value * 255;
                    break;
                default: 
                    pixel.r = value * 255; pixel.g = p * 255; pixel.b = q * 255; 
                    break;
                }
            }
            imagePixels[y * width + x] = pixel;
        }
    }
}
