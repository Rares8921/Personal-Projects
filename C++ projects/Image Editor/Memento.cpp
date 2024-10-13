#include "Memento.h"

Memento::Memento(Image image) {
	state = image;
}

const Image& Memento::getState() const {
	return state;	
}
