#pragma once

#include "Adjusters.h"

class HueAdjust : public Adjusters {
public:
	HueAdjust() = default;
	~HueAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};


