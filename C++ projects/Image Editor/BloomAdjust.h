#pragma once

#include "Adjusters.h"
#include <cstring>

class BloomAdjust : public Adjusters {
public:
	BloomAdjust() = default;
	~BloomAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};
