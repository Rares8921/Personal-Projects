#include "FlareFilter.h"

FlareFilter::FlareFilter() {
	xCenter = 0.5f;
	yCenter = 0.5f;
	intensity = 0.5f;
	flareColor = { 255, 150, 0, 255 };
	flareIntensity = 0.f;
}

void FlareFilter::setXCenter(float _xCenter) {
	xCenter = _xCenter;
}

void FlareFilter::setYCenter(float _yCenter) {
	yCenter = _yCenter;
}

void FlareFilter::setIntencity(float _intensity) {
	intensity = _intensity;
}

void FlareFilter::setFlareColor(Color _flareColor) {
	flareColor = _flareColor;
}

void FlareFilter::applyFilterToPixel(Color& pixel) {
	pixel.r = (unsigned char) Clamp(pixel.r + flareIntensity * flareColor.r, 0.f, 255.f);
	pixel.g = (unsigned char) Clamp(pixel.g + flareIntensity * flareColor.g, 0.f, 255.f);
	pixel.b = (unsigned char) Clamp(pixel.b + flareIntensity * flareColor.b, 0.f, 255.f);
}

void FlareFilter::applyFilterToImage(Color*& imagePixels, int height, int width) {
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			// Calculez distanta de la centrul imaginii pana la pixel
			float dx = (j - width * xCenter), dy = (i - height * yCenter);
			float distance = sqrt(dx * dx + dy * dy);
			flareIntensity = intensity * (1.f - (distance / (width * xCenter)));
			applyFilterToPixel(imagePixels[i * width + j]);
		}
	}
}

