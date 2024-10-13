#include "AttributeComponent.h"
#include <utility>
#include <cmath>
#include <fstream>

AttributeComponent::AttributeComponent(const int level) 
	: currentLevel(level), currentExp(0), vitality(1), strength(1), agility(1), intelligence(1),
	  currentHp(50), currentMp(20), dead(false), magicPower(1), maxHp(50), maxMp(20), physicalDefence(4),
	  magicDefence(2), minAttackDamage(1), maxAttackDamage(5), minMagicDamage(1), maxMagicDamage(5) {
	// currentLevel + 1

	std::ifstream fin("Config/levels.ini");
	if (fin.is_open()) {
		fin >> currentLevel;
		fin >> currentExp;
		fin >> vitality;
		fin >> strength;
		fin >> agility;
		fin >> intelligence;
		fin >> currentHp;
		fin >> currentMp;
		fin >> magicPower;
		fin >> maxHp;
		fin >> maxMp;
		fin >> physicalDefence;
		fin >> magicDefence;
		fin >> minAttackDamage;
		fin >> maxAttackDamage;
		fin >> minMagicDamage;
		fin >> maxMagicDamage;
	}
	fin.close();

	maxExp = (50 * std::pow(currentLevel, 3) - 150 * pow(currentLevel, 2) + (size_t)400 * currentLevel) / 3;
	updateLevel();
	updateStats(true);
}

AttributeComponent::~AttributeComponent() {
}

const bool& AttributeComponent::isDead() {
	return dead = currentHp <= 0;
}

const int& AttributeComponent::getVitality() {
	return vitality;
}

const int& AttributeComponent::getAgility() {
	return agility;
}

const int& AttributeComponent::getStrength() {
	return strength;
}

const int& AttributeComponent::getIntelligence() {
	return intelligence;
}

const int& AttributeComponent::getMagicPower() {
	return magicPower;
}

const int& AttributeComponent::getCurrentMp() {
	return currentMp;
}

const int& AttributeComponent::getMaxMp() {
	return maxMp;
}

const int& AttributeComponent::getCurrentLevel() {
	return currentLevel;
}

const int& AttributeComponent::getCurrentExp() {
	return currentExp;
}

const int& AttributeComponent::getMaxExp() {
	return maxExp;
}

const int& AttributeComponent::getCurrentHp() {
	return currentHp;
}

const int& AttributeComponent::getMaxHp() {
	return maxHp;
}

const int& AttributeComponent::getPhysicalDefence() {
	return physicalDefence;
}

const int& AttributeComponent::getMagicDefence() {
	return magicDefence;
}

const int& AttributeComponent::getMinMagicDamage() {
	return minMagicDamage;
}

const int& AttributeComponent::getMaxMagicDamage() {
	return maxMagicDamage;
}

const int& AttributeComponent::getMinAttackDamage() const {
	return minAttackDamage;
}

const int& AttributeComponent::getMaxAttackDamage() const {
	return maxAttackDamage;
}

void AttributeComponent::updateHp(const int hp) {
	if (hp < 0) {
		currentHp = (currentHp + hp < 0 ? 0 : currentHp + hp);
	} else {
		currentHp = (currentHp + hp > maxHp ? maxHp : currentHp + hp);
	}
}

void AttributeComponent::updateMaxHp(const int hp) {
	maxHp = hp;
}

void AttributeComponent::updateMp(const int mp) {
	if (mp < 0) {
		currentMp = (currentMp + mp < 0 ? 0 : currentMp + mp);
	} else {
		currentMp = (currentMp + mp > maxMp ? maxMp : currentMp + mp);
	}
}

void AttributeComponent::updateMaxMp(const int mp) {
	maxMp = mp;
}

void AttributeComponent::updateExp(const int exp) {
	if (exp < 0) {
		currentExp = std::max(currentExp - 1, 0);
	} else {
		currentExp += exp;
		updateLevel();
	}
}

void AttributeComponent::updateAttackPower(const int ap) {
	strength = ap;
}

void AttributeComponent::updateMagicPower(const int mp) {
	magicPower = mp;
}

void AttributeComponent::updatePhysicalDefence(const int def) {
	physicalDefence = def;
}

void AttributeComponent::updateMagicDefence(const int def) {
	magicDefence = def;
}

void AttributeComponent::updateStats(const bool reset) {
	maxHp = vitality * 6 + strength / 2;
	maxMp = intelligence * 6 + magicPower / 2;
	minAttackDamage = strength / 2;
	maxAttackDamage = strength;
	minMagicDamage = magicPower / 2;
	maxMagicDamage = magicPower;
	physicalDefence = agility / 2 + vitality / 5;
	magicDefence = intelligence / 2 + magicPower / 5;
	if (reset) {
		currentHp = maxHp;
	}
}

void AttributeComponent::updateLevel() {
	while (currentExp >= maxExp) {
		++currentLevel;
		currentExp -= maxExp;
		maxExp = (50 * std::pow(currentLevel, 3) - 150 * pow(currentLevel, 2) + (size_t) 400 * currentLevel) / 3;
	}
}

void AttributeComponent::updateComponent() {
	updateLevel();
}

AttributeComponent& AttributeComponent::operator=(const AttributeComponent& other) {
	if (this != &other) {
		vitality = other.vitality;
		agility = other.agility;
		strength = other.strength;
		intelligence = other.intelligence;
		magicPower = other.magicPower;
		currentExp = other.currentExp;
		maxExp = other.maxExp;
		currentHp = other.currentHp;
		maxHp = other.maxHp;
		currentMp = other.currentMp;
		maxMp = other.maxMp;
		currentLevel = other.currentLevel;
		dead = other.dead;
		physicalDefence = other.physicalDefence;
		magicDefence = other.magicDefence;
		minAttackDamage = other.minAttackDamage;
		maxAttackDamage = other.maxAttackDamage;
		minMagicDamage = other.minMagicDamage;
		maxMagicDamage = other.maxMagicDamage;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const AttributeComponent& attrComp) {
	out << "Vitality: " << attrComp.vitality << "\n";
	out << "Agility: " << attrComp.agility << "\n";
	out << "Strength: " << attrComp.strength << "\n";
	out << "Intelligence: " << attrComp.intelligence << "\n";
	out << "Magic Power: " << attrComp.magicPower << "\n";
	out << "Current Exp: " << attrComp.currentExp << "\n";
	out << "Max Exp: " << attrComp.maxExp << "\n";
	out << "Current HP: " << attrComp.currentHp << "\n";
	out << "Max HP: " << attrComp.maxHp << "\n";
	out << "Current MP: " << attrComp.currentMp << "\n";
	out << "Max MP: " << attrComp.maxMp << "\n";
	out << "Current Level: " << attrComp.currentLevel << "\n";
	out << "Dead: " << attrComp.dead << "\n";
	out << "Physical Defence: " << attrComp.physicalDefence << "\n";
	out << "Magic Defence: " << attrComp.magicDefence << "\n";
	out << "Min Attack Damage: " << attrComp.minAttackDamage << "\n";
	out << "Max Attack Damage: " << attrComp.maxAttackDamage << "\n";
	out << "Min Magic Damage: " << attrComp.minMagicDamage << "\n";
	out << "Max Magic Damage: " << attrComp.maxMagicDamage << "\n";
	return out;
}
