#include "Weapon.h"

void Weapon::initVariables() {
	range = 50;
	minDamage = 1;
	maxDamage = 3;
	attackTimer.restart();
	maxAttackTimer = 500;
}

Weapon::Weapon(int level, int value, std::string pathToTexture)
	: Item(level, value) {

	initVariables();
	if (!weaponTexture.loadFromFile(pathToTexture)) {
		std::cout << "Arma de la adresa: " << pathToTexture << " nu a putut fi incarcata in clasa Weapon!\n";
	}
	weaponSprite.setTexture(weaponTexture);
}

Weapon::Weapon(int level, int minDamage, int maxDamage, int range, int value, std::string pathToTexture)
	: Item(level, value), minDamage(minDamage), maxDamage(maxDamage), range(range) {
	initVariables();

	if (!weaponTexture.loadFromFile(pathToTexture)) {
		std::cout << "Arma de la adresa: " << pathToTexture << " nu a putut fi incarcata in clasa Weapon!\n";
	}
	weaponSprite.setTexture(weaponTexture);
}

Weapon::~Weapon(){ }

const int& Weapon::getDamageMin() const {
	return minDamage;
}

const int& Weapon::getDamageMax() const {
	return maxDamage;
}

const int Weapon::getDamage() const {
	return rand() % (maxDamage - minDamage + 1) + (minDamage);
}

const int& Weapon::getRange() const {
	return range;
}

const bool Weapon::getAttackTimer() {
	if (attackTimer.getElapsedTime().asMilliseconds() >= maxAttackTimer) {
		attackTimer.restart();
		return true;
	}
	return false;
}