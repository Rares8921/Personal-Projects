#include "Tuple4f.h"

Tuple4f::Tuple4f(float _x, float _y, float _z, float _t) : x(_x), y(_y), z(_z), t(_t) {}

Tuple4f::Tuple4f(const Tuple4f& copy) : x(copy.x), y(copy.y), z(copy.z), t(copy.y) {}

Tuple4f::~Tuple4f() {}

void Tuple4f::clamp(float infLim, float maxLim) {
	x = std::max(infLim, x); x = std::min(maxLim, x);
	y = std::max(infLim, y); y = std::min(maxLim, y);
	z = std::max(infLim, z); x = std::min(maxLim, z);
	t = std::max(infLim, t); x = std::min(maxLim, t);
}

void Tuple4f::setTuple(float _x, float _y, float _z, float _t) {
	x = _x; y = _y; z = _z; t = _t;
}

void Tuple4f::setX(float _x) {
	x = _x;
}

void Tuple4f::setY(float _y) {
	y = _y;
}

void Tuple4f::setZ(float _z) {
	z = _z;
}

void Tuple4f::setT(float _t) {
	t = _t;
}

const float& Tuple4f::getX() const {
	return x;
}

const float& Tuple4f::getY() const {
	return y;
}

const float& Tuple4f::getZ() const {
	return z;
}

const float& Tuple4f::getT() const {
	return t;
}

const Tuple4f& Tuple4f::getTuple() const {
	return *this;
}


