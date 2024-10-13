#pragma once

#include "Adjusters.h"

class OpacityAdjust : public Adjusters {
public:
	OpacityAdjust() = default;
	~OpacityAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

