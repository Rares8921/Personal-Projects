#include "SmoothnessAdjust.h"

void SmoothnessAdjust::adjustPixel(Color& imagePixel, int value)
{
}

void SmoothnessAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
    // Peste matricea de valori, incerc sa calculez noile rgb-uri in functie de valorile vecinilor
    // https://www.scaler.com/topics/smoothing-in-image-processing/
    // Simulez un filtru bilateral
    Color* tempPixels = new Color[height * width];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int sumR = 0, sumG = 0, sumB = 0, count = 0;
            // Parcurg submatricea de 3x3 cu centrul in (y, x)
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int newX = x + dx;
                    int newY = y + dy;
                    // Sa fie in matrice
                    if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                        Color neighborPixel = imagePixels[newY * width + newX];
                        sumR += neighborPixel.r;
                        sumG += neighborPixel.g;
                        sumB += neighborPixel.b;
                        count++;
                    }
                }
            }
            int avgR = sumR / count, avgG = sumG / count, avgB = sumB / count;

            int r = imagePixels[y * width + x].r + (avgR - imagePixels[y * width + x].r) * value;
            int g = imagePixels[y * width + x].g + (avgG - imagePixels[y * width + x].g) * value;
            int b = imagePixels[y * width + x].b + (avgB - imagePixels[y * width + x].b) * value;

            // clamp
            r = (r < 0) ? 0 : (r > 255) ? 255 : r;
            g = (g < 0) ? 0 : (g > 255) ? 255 : g;
            b = (b < 0) ? 0 : (b > 255) ? 255 : b;
            tempPixels[y * width + x] = { (unsigned char)r, (unsigned char)g, (unsigned char)b, imagePixels[y * width + x].a };
        }
    }
    for (int i = 0; i < height * width; ++i) {
        imagePixels[i] = tempPixels[i];
    }
    delete[] tempPixels;
}
