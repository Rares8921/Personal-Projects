#pragma once

#include "raylib.h"
#include "Eraser.h"
#include "FillTool.h"
#include <iostream>

class Pen;
class Fill;
class Shape;

class DrawTool {
protected:
	int brushSize;
	Color brushColor;
public:
	DrawTool();
	virtual ~DrawTool() = default;
	virtual Image drawPixel(Image image, int x, int y);
	void setBrushSize(int size);
	void setBrushColor(Color color);
	const int& getBrushSize() const;
	const Color& getBrushColor() const;
	template<class ToolType>
	void drawPixel(ToolType tool, Image& image, int x, int y);
};


template<>
inline void DrawTool::drawPixel<FillTool*>(FillTool* tool, Image& image, int x, int y) {
	tool->floodFill(image, x, y);
}

template<>
inline void DrawTool::drawPixel<Eraser*>(Eraser* tool, Image& image, int x, int y) {
	float toolSize = tool->getBrushSize() * 1.f;
	ImageDrawRectangle(&image, x, y, toolSize, toolSize, tool->getBrushColor());
}

template<class ToolType>
inline void DrawTool::drawPixel(ToolType tool, Image& image, int x, int y) {
	if (typeid(tool) == typeid(DrawTool)) {
		try {
			float toolSize = tool.getBrushSize() * 1.f;
			ImageDrawRectangle(&image, x, y, toolSize, toolSize, tool.getBrushColor());
		} catch (...) {
			std::cout << "Nu s-a putut desena!\n";
		}
	} else {
		std::cout << "Tip de date incorect!\n";
	}
}
