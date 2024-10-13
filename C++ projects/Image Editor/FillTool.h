#pragma once

#include "raylib.h"
#include <vector>

class FillTool {
private:
	const int brushSize = 5;
	const int di[8] = { -1, 0, 1, 0, 1, 1, -1, -1 };
	const int dj[8] = { 0, 1, 0, -1, 1, -1, 1, -1 };
	std::vector<std::vector<bool>> matrFill;
	Color fillColor;
	static FillTool* fillInstance;
	Color* imagePixels;
public:
	FillTool();
	FillTool(const FillTool& fillObj) = delete;
	virtual ~FillTool() = default;
	static FillTool* getInstance();
	const int& getBrushSize() const;
	const Color& getBrushColor() const;
	void setFillColor(Color _fillColor);
	void floodFill(Image& currentImage, int x, int y);
};

