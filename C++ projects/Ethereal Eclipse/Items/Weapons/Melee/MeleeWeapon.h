#pragma once

#include "../Weapon.h"
#include <iostream> // std::cout

class MeleeWeapon : public Weapon {
public:
	MeleeWeapon(int level, int minDamage, int maxDamage, int range, int value,
		std::string pathToTexture);
	virtual ~MeleeWeapon();

	virtual MeleeWeapon* clone() = 0;
	virtual void generate(const int levelMin, const int levelMax);

	virtual void update(const sf::Vector2f& mouse_pos_view, const sf::Vector2f center) = 0;
	virtual void render(sf::RenderTarget& target, sf::Shader* shader) = 0;
};

