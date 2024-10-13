#pragma once

#include "Tuple3f.h"

class Vector3f : public Tuple3f {
private:
public:
	Vector3f() = default;
	Vector3f(int _x, int _y, int _z);
	Vector3f(const Vector3f& copy);
	virtual ~Vector3f();

};

