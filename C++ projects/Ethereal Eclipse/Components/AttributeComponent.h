
#pragma once

#include <iostream>

class AttributeComponent {
private:
	// Mp - magic points
	int vitality, agility, strength;
	int intelligence, magicPower;
	int currentExp, maxExp;
	int currentHp, maxHp;
	int currentMp, maxMp;
	int currentLevel;
	bool dead;
	int physicalDefence, magicDefence;
	int minAttackDamage, maxAttackDamage;
	int minMagicDamage, maxMagicDamage;

public:
	AttributeComponent(const int level);
	virtual ~AttributeComponent();

	const bool& isDead();
	const int& getVitality();
	const int& getAgility();
	const int& getStrength();
	const int& getIntelligence();
	const int& getMagicPower();
	const int& getCurrentMp();
	const int& getMaxMp();
	const int& getCurrentLevel();
	const int& getCurrentExp();
	const int& getMaxExp();
	const int& getCurrentHp();
	const int& getMaxHp();
	const int& getPhysicalDefence();
	const int& getMagicDefence();
	const int& getMinMagicDamage();
	const int& getMaxMagicDamage();
	const int& getMinAttackDamage() const;
	const int& getMaxAttackDamage() const;

	void updateHp(const int hp);
	void updateMaxHp(const int hp);
	void updateMp(const int mp);
	void updateMaxMp(const int mp);
	void updateExp(const int exp);
	void updateAttackPower(const int ap);
	void updateMagicPower(const int mp);
	void updatePhysicalDefence(const int def);
	void updateMagicDefence(const int def);
	void updateStats(const bool reset);
	void updateLevel();
	void updateComponent();
	
	AttributeComponent& operator=(const AttributeComponent& other);
	friend std::ostream& operator<<(std::ostream& out, const AttributeComponent& attrComp);
};

