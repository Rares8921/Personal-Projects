#pragma once

#include "Adjusters.h"

class TintAdjust : public Adjusters {
public:
	TintAdjust() = default;
	~TintAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};