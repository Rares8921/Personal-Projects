#include "Inventory.h"

void Inventory::initInventory() {
	size = 0;
	for (int i = 0; i < capacity; ++i) {
		//items[i] = new Item(0, 0);
		items.push_back(nullptr);
	}
}

Inventory::Inventory(int capacity) : capacity(capacity) {
	initInventory();
}

Inventory::~Inventory() {
	while (items.size() > 0) {
		int index = items.size() - 1;
		delete items[index];
		items[index] = nullptr;
	}
}

//Accessors

const int& Inventory::getSize() const {
	return size;
}

const int& Inventory::getCapacity() const {
	return capacity;
}

void Inventory::clear() {
	while (items.size() > 0) {
		int index = items.size() - 1;
		delete items[index];
		items[index] = nullptr;
	}
	size = 0;
}

const bool& Inventory::isEmpty() const {
	return size == 0;
}

const bool Inventory::addItem(Item* item) {
	if (size < capacity) {
		items[size++] = item;
		return true;
	}
	return false;
}

const bool Inventory::removeItem(const int index) {
	if (size > 0) {
		if (index < 0 || index >= capacity) {
			return false;
		}
		delete items[index];
		items[index] = nullptr;
		--size;
		return true;
	}
	return false;
}

const bool Inventory::saveToFile(const std::string fileName) {
	return false;
}

const bool Inventory::loadFromFile(const std::string fileName) {
	return false;
}

Inventory& Inventory::operator=(const Inventory& other) {
	if (this != &other) {
		// Copierea membrilor clasici
		items = other.items;
		capacity = other.capacity;
		size = other.size;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Inventory& inventory) {
	out << "Capacity: " << inventory.capacity << "\n";
	out << "Size: " << inventory.size << "\n";
	return out;
}
