#pragma once

#include "Adjusters.h"

class SharpenAdjust : public Adjusters {
public:
	SharpenAdjust() = default;
	~SharpenAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};
