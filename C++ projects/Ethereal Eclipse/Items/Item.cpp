#include "Item.h"

Item::Item(int level, int value) : level(level), value(value) {
	type = ITEM_TYPES::DEFAULT;
}

Item::~Item() {}

const int& Item::getType() const {
	return type;
}

const int& Item::getLevel() const {
	return level;
}

const int& Item::getValue() const {
	return value;
}
