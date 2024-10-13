#pragma once

#include "raylib.h"
#include <raymath.h>
#include <cmath>

class Adjusters {
protected:
	int rgbToHsv(int code); // Hue, Saturation, Value
	int maxim(int a, int b);
	int maxim(int a, int b, int c);
	int minim(int a, int b);
	int minim(int a, int b, int c);
public:
	Adjusters() = default;
	virtual ~Adjusters() = default;
	virtual void adjustPixel(Color& imagePixel, int value) = 0;
	virtual void adjustImage(Color*& imagePixels, int height, int width, int value) = 0;
};

