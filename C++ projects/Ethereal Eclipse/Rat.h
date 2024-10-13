#pragma once

#include "Enemy.h"

class Rat : public Enemy {
private:
	sf::RectangleShape hpBar;
	AIFollow* ratFollow;
	void initAnimations();
	void initGUI();
public:
	Rat(float x, float y, sf::Texture& textureSheet, SpawnerTile& enemySpawnerTile, Entity& player);
	virtual ~Rat();
	void updateAnimation(const float& deltaTime);
	void updateEntity(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& mapView);
	void renderEntity(sf::RenderTarget& currentWindow,
		sf::Shader* shader = NULL,
		const sf::Vector2f lightPosition = sf::Vector2f(), const bool showHitbox = false);
	Rat& operator=(const Rat& other);
	friend std::ostream& operator<<(std::ostream& out, const Rat& rat);
};

