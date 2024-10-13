#pragma once

#include "Adjusters.h"

class VignetteAdjust : public Adjusters {
public:
	VignetteAdjust() = default;
	~VignetteAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

