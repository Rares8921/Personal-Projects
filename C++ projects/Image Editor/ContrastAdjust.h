#pragma once

#include "Adjusters.h"

class ContrastAdjust : public Adjusters {
public:
	ContrastAdjust() = default;
	~ContrastAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

