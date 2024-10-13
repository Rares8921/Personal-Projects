#pragma once

#include "Adjusters.h"
#include <iostream>

class HighlightsAdjust : public Adjusters {
public:
	HighlightsAdjust() = default;
	~HighlightsAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

