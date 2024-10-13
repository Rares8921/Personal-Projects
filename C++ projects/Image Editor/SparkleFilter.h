#pragma once
#include "Filter.h"
class SparkleFilter : public Filter {
private:
	Color sparkleColor;
	float density;
public:
	SparkleFilter();
	void setSparkleColor(Color _sparkleColor);
	void setDensity(float _density);
	void applyFilterToPixel(Color& color);
	void applyFilterToImage(Color*& imagePixels, int height, int width);
};

