#pragma once

#include "Adjusters.h"

class DehazeAdjust : public Adjusters {
public:
	DehazeAdjust() = default;
	~DehazeAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

