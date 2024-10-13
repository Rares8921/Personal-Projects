#include "FlipFilter.h"

FlipFilter::FlipFilter() : Filter() {}

void FlipFilter::applyFilterToPixel(Color& color) {}

void FlipFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width / 2; ++j) {
            std::swap(imagePixels[i * width + j], imagePixels[i * width + (width - j - 1)]);
        }
    }
}

void FlipFilter::applyVerticalFilterToImage(Color*& imagePixels, int height, int width) {
    for (int i = 0; i < height / 2; ++i) {
        for (int j = 0; j < width; ++j) {
            std::swap(imagePixels[i * width + j], imagePixels[(height - i - 1) * width + j]);
        }
    }
}