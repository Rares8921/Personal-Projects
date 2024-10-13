#pragma once
#include "Filter.h"
class ShineFilter : public Filter {
private:
	float shineIntensity, shineWidth;
	float distanceToCenter;
	float shineFactor;
public:
	ShineFilter();
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

