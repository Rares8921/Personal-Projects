#include "FadeFilter.h"

FadeFilter::FadeFilter() {
	fadeColor = BLACK;
	fadeFactor = 0.25;
}

void FadeFilter::applyFilterToPixel(Color& color)
{
}

void FadeFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int y = 0; y < height; ++y) {
		for (int x = 0; x < width; ++x) {
			imagePixels[y * width + x].r = (unsigned char)((1.0f - fadeFactor) * imagePixels[y * width + x].r + fadeFactor * fadeColor.r);
			imagePixels[y * width + x].g = (unsigned char)((1.0f - fadeFactor) * imagePixels[y * width + x].g + fadeFactor * fadeColor.g);
			imagePixels[y * width + x].b = (unsigned char)((1.0f - fadeFactor) * imagePixels[y * width + x].b + fadeFactor * fadeColor.b);
		}
	}
}
