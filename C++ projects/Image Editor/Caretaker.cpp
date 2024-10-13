#include "Caretaker.h"

Caretaker::Caretaker() {
	currentIndex = -1;
}

void Caretaker::addMemento(Memento& memento) {
	history.push_back(memento);
	++currentIndex;
}

Memento Caretaker::getMemento(int index) {
	if (index < 0 || index > currentIndex) {
		return Memento(GenImageColor(0, 0, WHITE));
	}
	return Memento(history[index]);
}