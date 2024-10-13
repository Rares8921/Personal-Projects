#include "BrigthnessAdjust.h"
#include <iostream>

void BrigthnessAdjust::adjustPixel(Color& imagePixel, int value) {}

void BrigthnessAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
	float brigthness = value / 100.f;
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			Color pixel = imagePixels[i * height + j];
			if (pixel.a > 0) {
				pixel.r = round(pixel.r * brigthness);
				pixel.g = round(pixel.g * brigthness);
				pixel.b = round(pixel.b * brigthness);
				imagePixels[i * height + j] = pixel;
			}
		}
	}
}
