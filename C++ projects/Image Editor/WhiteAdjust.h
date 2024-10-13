#pragma once
#include "Adjusters.h"
class WhiteAdjust : public Adjusters {
public:
	WhiteAdjust() = default;
	~WhiteAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

