#pragma once
#include "Filter.h"

class RotateFilter : public Filter {
private:
public:
	RotateFilter();
	virtual ~RotateFilter() = default;
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
	void applyLeftFilterToImage(Color*& imagePixels, int height, int width);
	//void applyFilterByDegree(Color*& imagePixels, int height, int width, float angleDeg);
};


