#pragma once

#include "../Item.h"
#include "SFML\Graphics.hpp"
#include <iostream> // std::string

class Weapon : public Item {
private:
	void initVariables();
protected:
	sf::Texture weaponTexture;
	sf::Sprite weaponSprite;

	int minDamage, maxDamage, range;

	sf::Clock attackTimer;
	int maxAttackTimer;
public:
	Weapon(int level, int value, std::string pathToTexture);
	Weapon(int level, int minDamage, int maxDamage, int range, int value, std::string pathToTexture);
	virtual ~Weapon();

	const int& getDamageMin() const;
	const int& getDamageMax() const;
	const int getDamage() const;
	const int& getRange() const;
	const bool getAttackTimer();

	virtual Item* clone() = 0;
	virtual void generate(const int levelMin, const int levelMax) = 0;

	virtual void update(const sf::Vector2f& mouse_pos_view, const sf::Vector2f center) = 0;
	virtual void render(sf::RenderTarget& target, sf::Shader* shader = nullptr) = 0;
};

