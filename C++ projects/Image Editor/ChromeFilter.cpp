#include "ChromeFilter.h"

void ChromeFilter::applyFilterToPixel(Color& color)
{}

void ChromeFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int y = 0; y < height; ++y) {
		for (int x = 0; x < width; ++x) {
			imagePixels[y * width + x].r = (unsigned char)std::min(imagePixels[y * width + x].r * 1.2f, 255.f);
			imagePixels[y * width + x].g = (unsigned char)std::min(imagePixels[y * width + x].g * 1.1f, 255.f);
			imagePixels[y * width + x].b = (unsigned char)(imagePixels[y * width + x].b * 0.9f);
		}
	}
}
