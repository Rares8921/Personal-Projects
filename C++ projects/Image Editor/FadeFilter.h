#pragma once
#include "Filter.h"
class FadeFilter : public Filter {
private:
	Color fadeColor;
	float fadeFactor;
public:
	FadeFilter();
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

