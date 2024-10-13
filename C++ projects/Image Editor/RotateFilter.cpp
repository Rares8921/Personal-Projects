#include "RotateFilter.h"
#include <iostream>

RotateFilter::RotateFilter() : Filter() {}

void RotateFilter::applyFilterToPixel(Color& color) {}

void RotateFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
    Color* newImagePixels = new Color[width * height];
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            newImagePixels[j * height + i] = imagePixels[(height - i - 1) * width + j];
        }
    }
    imagePixels = newImagePixels;
}

void RotateFilter::applyLeftFilterToImage(Color*& imagePixels, int height, int width) {
    Color* newImagePixels = new Color[width * height];
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            newImagePixels[j * height + i] = imagePixels[i * width + (width - j - 1)];
        }
    }
    imagePixels = newImagePixels;
}