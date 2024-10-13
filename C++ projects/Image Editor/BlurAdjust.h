#pragma once

#include "Adjusters.h"

class BlurAdjust : public Adjusters {
public:
	BlurAdjust() = default;
	~BlurAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

