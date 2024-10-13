#pragma once

#include "SFML\Graphics.hpp"
#include "Components/MovementComponent.h"
#include "Components/AnimationComponent.h"
#include "Components/HitboxComponent.h"
#include "Components/AttributeComponent.h"
#include "Components/SkillComponent.h"
#include "Items/Weapons/Melee/Sword.h"

class Entity {
private:
	bool isAttacking;
	void initVariables();
protected:
	sf::RectangleShape entityShape;
	// Pentru a nu incarca de mai multe ori decat necesar textura
	sf::Sprite entityShapeSprite;
	HitboxComponent* hitboxComponent;
	MovementComponent* movementComponent;
	AnimationComponent* animationComponent;
	AttributeComponent* attributeComponent;
	SkillComponent* skillComponent;
	float movementSpeed;
	void setAttacking(const bool& value);
	const bool& getAttacking() const;
public:
	Entity();
	virtual ~Entity();

	void setTexture(sf::Texture& texture);
	void createAttributeComponent(const int level);
	void createMovementComponent(const float maxVelocity, const float acceleration, const float deceleration);
	void createAnimationComponent(sf::Texture& textureSheet);
	void createHitboxComponent(sf::Sprite& textureSheet, 
		float offsetX, float offsetY,
		float width, float height);
	void createSkillComponent();

	virtual MovementComponent* getMovementComponent();
	virtual AnimationComponent* getAnimationComponent();
	virtual AttributeComponent* getAttributeComponent();
	virtual SkillComponent* getSkillComponent();

	virtual void setVelocity(const float x, const float y);
	virtual void setVelocityX(const float x);
	virtual void setVelocityY(const float y);
	virtual void setPosition(const float x, const float y);
	virtual const sf::Vector2f& getPosition() const;
	virtual const sf::Vector2f& getCenter() const;
	virtual const sf::Vector2f getSpriteCenter() const;
	virtual const sf::Vector2i& getGridPosition(const int gridSize) const;
	virtual const sf::FloatRect& getGlobalBounds() const;
	virtual const sf::FloatRect& getNextPositionGlobalBounds(const float& deltaTime) const;
	virtual const float getSpriteDistance(const Entity& entity) const;

	virtual void moveEntity(const float &deltaTime, const float x, const float y);
	virtual void updateEntity(const float& deltaTime);
	virtual void renderEntity(sf::RenderTarget& target,
		sf::Shader* shader = NULL, const sf::Vector2f light_position = sf::Vector2f(),
		const bool show_hitbox = false) = 0;

	Entity& operator=(const Entity& other);
	friend std::ostream& operator<<(std::ostream& out, const Entity& entity);
};