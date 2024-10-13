#pragma once

#include "Adjusters.h"

class BrigthnessAdjust : public Adjusters {
public:
	BrigthnessAdjust() = default;
	~BrigthnessAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

