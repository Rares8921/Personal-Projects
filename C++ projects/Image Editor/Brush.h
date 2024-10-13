#pragma once

#include "raylib.h"
#include "DrawTool.h"

class Brush : public DrawTool {
public:
	Brush();
	virtual ~Brush() = default;
	Image drawPixel(Image image, int x, int y);
};

