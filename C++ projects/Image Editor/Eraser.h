#pragma once

#include "raylib.h"

class Eraser {
private:
	int eraserSize;
	Color eraserColor;
	static Eraser* eraserInstance;
public:
	Eraser();
	Eraser(const Eraser& eraserObj) = delete;
	static Eraser* getInstance();
	virtual ~Eraser() = default;
	void setEraserColor(Color _eraserColor);
	void setEraserSize(int _eraserSize);
	const int& getBrushSize() const;
	const Color& getBrushColor() const;
};

