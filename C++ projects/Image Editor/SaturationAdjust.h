#pragma once

#include "Adjusters.h"

class SaturationAdjust : public Adjusters {
public:
	SaturationAdjust() = default;
	~SaturationAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

