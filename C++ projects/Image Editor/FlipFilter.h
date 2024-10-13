#pragma once

#include "Filter.h"

class FlipFilter : public Filter {
private:
public:
	FlipFilter();
	virtual ~FlipFilter() = default;
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
	void applyVerticalFilterToImage(Color*& imagePixels, int height, int width);
};

