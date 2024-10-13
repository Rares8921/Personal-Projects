#pragma once

#include "SFML\Graphics.hpp"
#include <iostream>

enum MovementStates {
	IDLE = 1,
	MOVING,
	MOVING_LEFT,
	MOVING_TOP_LEFT,
	MOVING_TOP,
	MOVING_TOP_RIGHT,
	MOVING_RIGHT,
	MOVING_BOTTOM_RIGHT,
	MOVING_BOTTOM,
	MOVING_BOTTOM_LEFT,
	ATTACKING,
};

class MovementComponent
{
private:
	sf::Sprite& currentSprite;
	float maxVelocity;
	sf::Vector2f velocity;
	float acceleration, deceleration;
	// Initializare

public:
	MovementComponent(sf::Sprite &currentSprite, float maxVelocity, float acceleration, float deceleration);
	virtual ~MovementComponent();

	const sf::Vector2f& getMaxVelocity() const;
	void setMaxVelocity(float newVelocity);
	const bool getState(MovementStates) const;
	void setVelocity(float newVelocityX, float newVelocityY);
	void setVelocityX(float newVelocityX);
	void setVelocityY(float newVelocityY);

	void move(const float x, const float y, const float& deltaTime);
	void update(const float& deltaTime);
	MovementComponent& operator=(const MovementComponent& other);
	friend std::ostream& operator<<(std::ostream& out, const MovementComponent& movementComp);
};

