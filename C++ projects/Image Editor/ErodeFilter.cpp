#include "ErodeFilter.h"

void ErodeFilter::applyFilterToPixel(Color& color) {}

void ErodeFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
    for (int i = 1; i < height - 1; ++i) {
        for (int j = 1; j < width - 1; ++j) {
            Color pixel = imagePixels[i * width + j];
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dx = -1; dx <= 1; ++dx) {
                    int newIndex = (i + dy) * width + (j + dx);
                    if (imagePixels[newIndex].a > 5) {
                        if (imagePixels[newIndex].r < pixel.r) {
                            pixel.r = imagePixels[newIndex].r;
                        }
                        if (imagePixels[newIndex].g < pixel.g) {
                            pixel.g = imagePixels[newIndex].g;
                        }
                        if (imagePixels[newIndex].b < pixel.b) {
                            pixel.b = imagePixels[newIndex].b;
                        }
                    }
                }
            }
            imagePixels[i * width + j].r -= pixel.r;
            imagePixels[i * width + j].g -= pixel.g;
            imagePixels[i * width + j].b -= pixel.b;
        }
    }
}