#pragma once

#include "raylib.h"

class Transformer {
public:
	Transformer() = default;
	virtual ~Transformer() = default;
	virtual void transformPixel(Color& imagePixel) = 0;
	virtual void transformImage(Color* imagePixels, int height, int width, int destHeight, int destWidth) = 0;
};

