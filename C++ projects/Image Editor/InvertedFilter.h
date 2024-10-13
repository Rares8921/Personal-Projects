#pragma once

#include "Filter.h"

class InvertedFilter : public Filter {
public:
	InvertedFilter();
	virtual ~InvertedFilter();
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

