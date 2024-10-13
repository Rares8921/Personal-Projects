#pragma once

#include <iostream>

class Tuple4f {
private:
	float x, y, z, t;
public:
	Tuple4f() = default;
	Tuple4f(float _x, float _y, float _z, float _t);
	Tuple4f(const Tuple4f& copy);
	virtual ~Tuple4f();
	void clamp(float infLim, float maxLim);
	void setTuple(float _x, float _y, float _z, float _t);
	void setX(float _x);
	void setY(float _y);
	void setZ(float _z);
	void setT(float _t);
	const float& getX() const;
	const float& getY() const;
	const float& getZ() const;
	const float& getT() const;
	const Tuple4f& getTuple() const;
};

