#include "InvertedFilter.h"

InvertedFilter::InvertedFilter() : Filter() {}

InvertedFilter::~InvertedFilter(){}

void InvertedFilter::applyFilterToPixel(Color& color) {
	color.r = 255 - color.r;
	color.g = 255 - color.g;
	color.b = 255 - color.b;
}

void InvertedFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			applyFilterToPixel(imagePixels[i * width + j]);
		}
	}
}
