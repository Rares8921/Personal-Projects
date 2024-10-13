#pragma once

#include "Memento.h"

class Originator {
private:
	Image currentState;
public:
	Originator() = default;
	Originator(Image& _currentState) : currentState(_currentState) {}
	~Originator() = default;
	const Image& getImage() const;
	void setImage(Image& newImage);
	Memento saveState() const;
	void restoreFromMemento(Memento& memento);
};
