#pragma once
#include "Filter.h"
class ErodeFilter : public Filter {
private:
public:
	ErodeFilter() = default;
	~ErodeFilter() = default;
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

