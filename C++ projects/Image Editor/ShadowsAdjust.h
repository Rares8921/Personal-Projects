#pragma once
#include "Adjusters.h" 

class ShadowsAdjust : public Adjusters {
public:
	ShadowsAdjust() = default;
	~ShadowsAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

