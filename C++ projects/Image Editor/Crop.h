#pragma once

#include "Transformer.h"

class Crop : public Transformer {
private:
	int x, y, width, height;
public:
	Crop() = default;
	Crop(int _x, int _y, int _width, int _height);
	void setX(int _x);
	const int& getX() const;
	void setY(int _y);
	const int& getY() const;
	void setWidth(int _width);
	const int& getWidth() const;
	void setHeight(int _height);
	const int& getHeight() const;
	void transformPixel(Color& imagePixel);
	void transformImage(Color* imagePixels, int height, int width, int destHeight, int destWidth);
};

