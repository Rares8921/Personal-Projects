#include "TintAdjust.h"

void TintAdjust::adjustPixel(Color& imagePixel, int value) {}

void TintAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
	int alphaValue = round((value / 2.f * 2.05f) + ((value + 1) / 2.f * 3.05));
	Color colorTint;
	// Dau tint cu mov la negativ, cu verde la pozitiv
	if (value > 0) {
		colorTint = { 49, 143, 78, (unsigned char)alphaValue };
	} else {
		value = -value;
		alphaValue = round((value / 2.f * 2.05f) + ((value + 1) / 2.f * 3.05));
		colorTint = { 137, 45, 124, (unsigned char)alphaValue };
	}
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			Color pixel = imagePixels[i * height + j];
			pixel.r = (unsigned char) ((float) pixel.r * colorTint.r / 255);
			pixel.g = (unsigned char) ((float) pixel.g * colorTint.g / 255);
			pixel.b = (unsigned char) ((float) pixel.b * colorTint.b / 255);
			pixel.r = Clamp(pixel.r, 0, 255);
			pixel.g = Clamp(pixel.g, 0, 255);
			pixel.b = Clamp(pixel.b, 0, 255);
			imagePixels[i * height + j] = pixel;
		}
	}
}
