#include "Originator.h"

const Image& Originator::getImage() const {
	return currentState;
}

void Originator::setImage(Image& newImage) {
	currentState = newImage;
}

Memento Originator::saveState() const {
	return Memento(currentState);
}

void Originator::restoreFromMemento(Memento& memento) {
	currentState = ImageCopy(memento.getState());
}
