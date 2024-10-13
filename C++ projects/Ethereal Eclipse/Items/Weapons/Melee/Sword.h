#pragma once

#include "MeleeWeapon.h"

class Sword : public MeleeWeapon {
public:
	Sword(int level, int damageMin, int damageMax, int range, int value, std::string pathToTexture);
	virtual ~Sword();

	virtual Sword* clone();

	virtual void update(const sf::Vector2f& mousePosView, const sf::Vector2f center);
	virtual void render(sf::RenderTarget& currentWindow, sf::Shader* shader = nullptr);
};

