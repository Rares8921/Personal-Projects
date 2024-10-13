#pragma once

#include "Entity.h"
#include "Inventory.h"
#include "Items/Weapons/Weapon.h"
#include "Items/Weapons/Melee/Sword.h"

class Player : public Entity {
private:
	// Variabile
	bool isAttacking, initAttacking;
	Weapon* playerWeapon;
	Inventory* playerInventory;

	sf::Clock damageTimer;
	int maxDamageTimer;

	// Initializez functiile
	void initVariables();
	void initAnimations();
	void initInventory();
public:
	Player(float x, float y, sf::Texture& playerTexture);
	virtual ~Player();
	// Functii
	AttributeComponent* getAttribute() const;
	Weapon* getWeapon() const;
	const int getDamage() const;
	const bool getDamageTimer();
	const bool& getInitAttacking() const;
	void setInitAttacking(const bool initAttack);
	void playDeathAnimation(const float& deltaTime);
	const std::string toStringCharacterTab() const;

	void loseHp(const int hp);
	void gainHp(const int hp);
	void loseExp(const int exp);
	void gainExp(const int exp);

	void updateAnimation(const float& deltaTime);
	void update(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& view);
	void renderEntity(sf::RenderTarget& target,
		sf::Shader* shader = NULL, const sf::Vector2f lightPosition = sf::Vector2f(),
		const bool showHitbox = false);
	Player& operator=(const Player& other);
	friend std::ostream& operator<<(std::ostream& out, const Player& player);
};

