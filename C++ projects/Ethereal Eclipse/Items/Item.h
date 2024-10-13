#pragma once

enum ITEM_TYPES {
	DEFAULT = 0,
	MELEEWEAPON,
	MAGICWEAPON,
	HEALTHPOT1,
	HEALTHPOT2,
	HEALTHPOT3,
	HEALTHPOT4,
	MPPOT1,
	MPPOT2,
	MPPOT3,
	MPPOT4,
	MAGICPOT1,
	MAGICPOT2,
	MAGICPOT3,
	MAGICPOT4,
	STRENGHTPOT1,
	STRENGTHPOT2,
	STRENGTHPOT3,
	STRENGTHPOT4,
};

class Item {
protected:
	int type, level, value;
public:
	Item(int level, int value);
	virtual ~Item();

	const int& getType() const;
	const int& getLevel() const;
	const int& getValue() const;

	virtual Item* clone() = 0;
};
