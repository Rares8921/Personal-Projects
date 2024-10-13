#pragma once

#include "Adjusters.h"

class GlamourAdjust : public Adjusters {
public:
	GlamourAdjust() = default;
	~GlamourAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};
