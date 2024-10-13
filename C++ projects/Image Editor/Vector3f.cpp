#include "Vector3f.h"

Vector3f::Vector3f(int _x, int _y, int _z)
{
	setX(_x);
	setY(_y);
	setZ(_z);
}

Vector3f::Vector3f(const Vector3f& copy) {
	setX(copy.getX());
	setY(copy.getY());
	setZ(copy.getZ());
}

Vector3f::~Vector3f(){}
