#pragma once

#include "Adjusters.h"

class ClarityAdjust : public Adjusters {
public:
	ClarityAdjust() = default;
	~ClarityAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

