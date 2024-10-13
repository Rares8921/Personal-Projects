#pragma once

#include "Items/Item.h"
#include <vector>
#include <iostream> // std::string

class Inventory {
private:
	std::vector<Item*> items;
	int capacity, size;
	void initInventory();
public:
	Inventory(int capacity);
	virtual ~Inventory();
	const int& getSize() const;
	const int& getCapacity() const;
	const bool& isEmpty() const;
	void clear();
	const bool addItem(Item* item);
	const bool removeItem(const int index);

	const bool saveToFile(const std::string pathToFile);
	const bool loadFromFile(const std::string pathToFile);

	Inventory& operator=(const Inventory& other);
	friend std::ostream& operator<<(std::ostream& out, const Inventory& inventory);
};

