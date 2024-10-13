#pragma once

#include "Filter.h"
class FlareFilter : public Filter {
private:
	float xCenter, yCenter;
	float intensity;
	Color flareColor;
	float flareIntensity;
public:
	FlareFilter();
	void setXCenter(float _xCenter);
	void setYCenter(float _yCenter);
	void setIntencity(float _intensity);
	void setFlareColor(Color _flareColor);
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

