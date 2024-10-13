#include "Crop.h"

Crop::Crop(int _x, int _y, int _width, int _height)
	: x(_x), y(_y), width(_width), height(_height) {}

void Crop::setX(int _x) {
	x = _x;
}

const int& Crop::getX() const {
	return x;
}

void Crop::setY(int _y) {
	y = _y;
}

const int& Crop::getY() const {
	return y;
}

void Crop::setWidth(int _width) {
	width = _width;
}

const int& Crop::getWidth() const {
	return width;
}

void Crop::setHeight(int _height) {
	height = _height;
}

const int& Crop::getHeight() const {
	return height;
}

void Crop::transformPixel(Color& imagePixel) {}

void Crop::transformImage(Color* imagePixels, int height, int width, int destHeight, int destWidth) {
	
}
