#pragma once

#include "Adjusters.h"

class VibranceAdjust : public Adjusters {
public:
	VibranceAdjust() = default;
	~VibranceAdjust() = default; 
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

