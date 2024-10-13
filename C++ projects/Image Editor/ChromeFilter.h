#pragma once
#include "Filter.h"

class ChromeFilter : public Filter {
public:
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

