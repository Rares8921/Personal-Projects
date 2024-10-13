#include "SparkleFilter.h"

SparkleFilter::SparkleFilter() {
	sparkleColor = {255, 200, 0, 255};
	density = 20.f;
}

void SparkleFilter::setSparkleColor(Color _sparkleColor) {
	sparkleColor = _sparkleColor;
}

void SparkleFilter::setDensity(float _density) {
	density = _density;
}

void SparkleFilter::applyFilterToPixel(Color& color) {
	color = sparkleColor;
}

void SparkleFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	// Ca sa nu se genereze doua valori consecutive egale
	srand(time(0));
	int totalSparkles = (density * width * height / 2000);
	for (int i = 0; i < totalSparkles;) {
		int x = rand() % width;
		int y = rand() % height;
		if (imagePixels[y * width + x].a > 5) {
			applyFilterToPixel(imagePixels[y * width + x]);
			++i;
		}
	}
}
