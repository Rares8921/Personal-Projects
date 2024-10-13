#include "Filter.h"

Filter::Filter() {
	a = r = g = b = 255;
	height = width = 100;
}

Filter::Filter(Image inputImage) : width(inputImage.width), height(inputImage.height) {
	currentImage = std::make_shared<Image>(inputImage);
	a = r = g = b = 255;
}

const int& Filter::getR() const {
	return r;
}

const int& Filter::getG() const {
	return g;
}

const int& Filter::getB() const {
	return b;
}

const int& Filter::getA() const {
	return a;
}

const int& Filter::getRGBEncoding(int r, int g, int b) const {
	return 0xff000000 | (r << 16) | (g << 8) | b;
}

const int& Filter::getRGBAEncoding(int r, int g, int b, int a) const {
	return (0xff000000 & a) | (r << 16) | (g << 8) | b;
}

const int& Filter::getGreen(int color) const {
	return (color >> 8) & 0xff;
}

const int& Filter::getRed(int color) const {
	return (color >> 16) & 0xff;
}

const int& Filter::getBlue(int color) const {
	return color & 0xff;
}

const int& Filter::getOpacity(int color) const {
	return color & 0xff000000;
}

template<typename numType>
inline numType Filter::clampValue(numType value) {
	return (value < 0 ? 0 : (value > 255 ? 255 : value));
}

template<typename numType>
inline numType Filter::clampValue(numType value, numType infLim, numType maxLim)
{
	return (value < infLim ? infLim : (value > maxLim ? maxLim : value));
}