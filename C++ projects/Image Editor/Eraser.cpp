#include "Eraser.h"

Eraser* Eraser::eraserInstance = nullptr;

Eraser::Eraser() {
	eraserSize = 5;
	eraserColor = { 255, 255, 255, 125 };
}

Eraser* Eraser::getInstance() {
	if (eraserInstance == nullptr) {
		eraserInstance = new Eraser();
	}
	return eraserInstance;
}

void Eraser::setEraserColor(Color _eraserColor) {
	eraserColor = _eraserColor;
}

void Eraser::setEraserSize(int _eraserSize) {
	eraserSize = _eraserSize;
}

const int& Eraser::getBrushSize() const {
	return eraserSize;
}

const Color& Eraser::getBrushColor() const {
	return eraserColor;
}
