#include "ShineFilter.h"

ShineFilter::ShineFilter() {
	shineIntensity = 0.7f;
	shineWidth = 0.1f;
	distanceToCenter = shineFactor = 0.f;
}

void ShineFilter::applyFilterToPixel(Color& color) {
	color.r = (unsigned char) std::min(color.r + shineFactor * 255.f, 255.f);
	color.g = (unsigned char) std::min(color.g + shineFactor * 255.f, 255.f);
	color.b = (unsigned char) std::min(color.b + shineFactor * 255.f, 255.f);
}

void ShineFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int y = 0; y < height; ++y) {
		for(int x = 0; x < width; ++x) {
			// Distanta de la centrul imaginii la pixel
			distanceToCenter = sqrt((x - width / 2) * (x - width / 2) + (y - height / 2) * (y - height / 2));
			shineFactor = shineIntensity * (1.2f - distanceToCenter / (width / 2));
			shineFactor = std::max(shineFactor - shineWidth, 0.f);
			applyFilterToPixel(imagePixels[y * width + x]);
		}
	}
}
