#pragma once
#include "Filter.h"
class DiffOfGaussiansFilter : public Filter {
private:
	float sigma1, sigma2;
public:
	DiffOfGaussiansFilter();
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

