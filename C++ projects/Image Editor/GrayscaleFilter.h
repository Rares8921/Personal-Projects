#pragma once

#include "Includes.h"
#include "Filter.h"

// Laplace sharpening

class GrayscaleFilter : public Filter {
private:
	int grayscaleColor;
public:
	GrayscaleFilter();
	~GrayscaleFilter();
	int filterRGB(int rgb);
	int filterRGB(int r, int g, int b);
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

