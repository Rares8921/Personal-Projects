#include "MeleeWeapon.h"

MeleeWeapon::MeleeWeapon(int level, int minDamage, int maxDamage, int range, int value, 
	std::string pathToTexture) : Weapon(level, minDamage, maxDamage, range, value, pathToTexture) {
	type = ITEM_TYPES::MELEEWEAPON;
}

MeleeWeapon::~MeleeWeapon() {}

void MeleeWeapon::generate(const int levelMin, const int levelMax) {
	level = rand() % (levelMax - levelMin + 1) + levelMin;
	minDamage = level * (rand() % 2 + 1);
	maxDamage = level * (rand() % 2 + 1) + minDamage;
	range = level + rand() % 10 + 70;
	value = level + minDamage + maxDamage + range + (rand() % level * 10);
}
