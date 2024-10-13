#pragma once

#include "Entity.h"
#include "Tiles/SpawnerTile.h"
#include "AIFollow.h"

class Enemy : public Entity {
private:
	SpawnerTile& enemySpawnerTile;
	int expAmount;
	sf::Clock damageTimer, despawnTimer;
	int damageTimerMax, despawnTimerMax;
	void initVariables();
	// void initAnimations();
public:
	Enemy(SpawnerTile& enemySpawnerTile);
	virtual ~Enemy();
	
	const sf::Clock& getDamageTimer() const;
	const int& getMaxDamageTimer() const;
	const int& getExpAmount() const;
	SpawnerTile& getSpawnerTile();
	const sf::Sprite& getRatSprite() const;
	const bool& isDamageTimerDone() const;
	const bool& isDespawnTimerDone() const;
	void resetDamageTimer();
	virtual void generateAttributes(const int level);
	virtual void loseHP(const int hp);

	virtual const bool& isDead() const;

	virtual const AttributeComponent* getAttribute() const;
	
	virtual void updateEntity(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& mapView) = 0;
	virtual void renderEntity(sf::RenderTarget& currentWindow,
		sf::Shader* shader = NULL,
		const sf::Vector2f light_position = sf::Vector2f(), const bool show_hitbox = false) = 0;
	Enemy& operator=(const Enemy& other);
	friend std::ostream& operator<<(std::ostream& out, const Enemy& enemy);
};

