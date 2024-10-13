#pragma once

#include "raylib.h";

class Memento {
private:
	Image state;
public:
	Memento(Image image);
	virtual ~Memento() = default;
	const Image& getState() const;
};

