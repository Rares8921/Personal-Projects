#include "Tuple3f.h"

Tuple3f::Tuple3f(float _x, float _y, float _z) : x(_x), y(_y), z(_z) {}

Tuple3f::Tuple3f(const Tuple3f& copy) : x(copy.x), y(copy.y), z(copy.z) {}

Tuple3f::~Tuple3f() {}

void Tuple3f::clamp(float infLim, float maxLim) {
	x = std::max(infLim, x); x = std::min(maxLim, x);
	y = std::max(infLim, y); y = std::min(maxLim, y);
	z = std::max(infLim, z); x = std::min(maxLim, z);
}

void Tuple3f::setTuple(float _x, float _y, float _z) {
	x = _x; y = _y; z = _z;
}

void Tuple3f::setX(float _x) {
	x = _x;
}

void Tuple3f::setY(float _y) {
	y = _y;
}

void Tuple3f::setZ(float _z) {
	z = _z;
}

const float& Tuple3f::getX() const {
	return x;
}

const float& Tuple3f::getY() const {
	return y;
}

const float& Tuple3f::getZ() const {
	return z;
}

const Tuple3f& Tuple3f::getTuple() const {
	return *this;
}


