#include "GrayscaleFilter.h"

GrayscaleFilter::GrayscaleFilter() : Filter() {
	grayscaleColor = 0;
}

GrayscaleFilter::~GrayscaleFilter(){}

int GrayscaleFilter::filterRGB(int rgb) {
	int r = (rgb >> 16) & 0xff;
	int g = (rgb >> 8) & 0xff;
	int b = rgb & 0xff;
	int a = rgb & 0xff000000;
	// https://en.wikipedia.org/wiki/Luma_(video)
	rgb = (r * 61 + g * 174 + b * 21) >> 8;
	return grayscaleColor = (a | (rgb << 16) | (rgb << 8) | rgb);
}

int GrayscaleFilter::filterRGB(int r, int g, int b) {
	return filterRGB(getRGBEncoding(r, g, b));
}

void GrayscaleFilter::applyFilterToPixel(Color& color) {
	int rgb = filterRGB(color.r, color.g, color.b);
	color.r = (rgb >> 16) & 0xff;
	color.g = (rgb >> 8) & 0xff;
	color.b = rgb & 0xff;
}

void GrayscaleFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			applyFilterToPixel(imagePixels[i * width + j]);
		}
	}
}


