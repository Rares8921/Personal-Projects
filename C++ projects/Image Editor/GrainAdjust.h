#pragma once

#include <cstdlib> // rand() srand()
#include <ctime>   // time()
#include "Adjusters.h"

class GrainAdjust : public Adjusters {
public:
	GrainAdjust() = default;
	~GrainAdjust() = default;
	void adjustPixel(Color& imagePixel, int value);
	void adjustImage(Color*& imagePixels, int height, int width, int value);
};

