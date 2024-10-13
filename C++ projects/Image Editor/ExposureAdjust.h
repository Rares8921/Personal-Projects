#pragma once

#include "Adjusters.h"

class ExposureAdjust : public Adjusters {
public:
	ExposureAdjust() = default;
	~ExposureAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

