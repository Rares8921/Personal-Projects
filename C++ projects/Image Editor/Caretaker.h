#pragma once

#include "Memento.h"
#include <vector>

class Caretaker {
private:
	std::vector<Memento> history;
	unsigned currentIndex;
public:
	Caretaker();
	~Caretaker() = default;
	void addMemento(Memento& memento);
	Memento getMemento(int index);
};

