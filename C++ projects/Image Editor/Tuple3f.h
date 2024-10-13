#pragma once

#include <iostream>

class Tuple3f {
private:
	float x, y, z;
public:
	Tuple3f() = default;
	Tuple3f(float _x, float _y, float _z);
	Tuple3f(const Tuple3f& copy);
	virtual ~Tuple3f();
	void clamp(float infLim, float maxLim);
	void setTuple(float _x, float _y, float _z);
	void setX(float _x);
	void setY(float _y);
	void setZ(float _z);
	const float& getX() const;
	const float& getY() const;
	const float& getZ() const;
	const Tuple3f& getTuple() const;
};

