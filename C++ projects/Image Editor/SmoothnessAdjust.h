#pragma once

#include "Adjusters.h"

class SmoothnessAdjust : public Adjusters {
public:
	SmoothnessAdjust() = default;
	~SmoothnessAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};
