#pragma once

#include "Adjusters.h" 

class BlackAdjust : public Adjusters {
public:
	BlackAdjust() = default;
	~BlackAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

