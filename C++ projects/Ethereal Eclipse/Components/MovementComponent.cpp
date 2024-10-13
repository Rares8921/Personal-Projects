#include "MovementComponent.h"

MovementComponent::MovementComponent(sf::Sprite& currentSprite, float maxVelocity, 
									float acceleration, float deceleration)
				: currentSprite(currentSprite), maxVelocity(maxVelocity), acceleration(acceleration), deceleration(deceleration) {
}

MovementComponent::~MovementComponent() {
}

const sf::Vector2f& MovementComponent::getMaxVelocity() const {
	return velocity;
}

void MovementComponent::setMaxVelocity(float newVelocity) {
	maxVelocity = newVelocity;
}

const bool MovementComponent::getState(MovementStates currentState) const
{
	// Verific daca entitatea nu mai este miscata in nicio directie
	switch (currentState) {
		case IDLE:
			return velocity.x == 0.f && velocity.y == 0.f;
		case MOVING:
			return velocity.x != 0.f || velocity.y != 0.f;
		case MOVING_LEFT:
			return velocity.x < 0.f;
		case MOVING_TOP_LEFT:
			return velocity.x < 0.f && velocity.y < 0.f;
		case MOVING_TOP:
			return velocity.y < 0.f;
		case MOVING_TOP_RIGHT:
			return velocity.x > 0.f && velocity.y < 0.f;
		case MOVING_RIGHT:
			return velocity.x > 0.f;
		case MOVING_BOTTOM_RIGHT:
			return velocity.x > 0.f && velocity.y > 0.f;
		case MOVING_BOTTOM:
			return velocity.y > 0.f;
		case MOVING_BOTTOM_LEFT:
			return velocity.x < 0.f && velocity.y > 0.f;
		default:
			break;
	}
	return false;
}

void MovementComponent::setVelocity(float newVelocityX, float newVelocityY) {
	velocity.x = newVelocityX;
	velocity.y = newVelocityY;
}

void MovementComponent::setVelocityX(float newVelocityX) {
	velocity.x = newVelocityX;
}

void MovementComponent::setVelocityY(float newVelocityY) {
	velocity.y = newVelocityY;
}

void MovementComponent::move(const float x, const float y, const float &deltaTime) {
	// Acelerarea unui sprite pana ajunge la velocitatea maxima
	velocity.x += acceleration * x;
	velocity.y += acceleration * y;
}

void MovementComponent::update(const float& deltaTime) {
	// Decelerare
	// + verific sa nu am o velocitate mai mare decat cea admisa
	if (velocity.x > 0.f) {
		// Verific pentru cazul in care jucatorul se mica inspre dreapta
		velocity.x = std::min(velocity.x, maxVelocity);
		// Tot pentru partea dreapta
		velocity.x -= deceleration;
		if (velocity.x < 0.f) {
			velocity.x = 0.f;
		}
	} else if (velocity.x < 0.f) {
		// Si aici pentru cand se misca inspre stanga
		velocity.x = std::max(velocity.x, -maxVelocity);
		// Si tot pentru partea stanga
		velocity.x += deceleration;
		if (velocity.x > 0.f) {
			velocity.x = 0.f;
		}
	}
	if (velocity.y > 0.f) {
		// Verific pentru cazul in care jucatorul se mica in jos
		velocity.y = std::min(velocity.y, maxVelocity);
		// Tot pentru mersul in jos
		velocity.y -= deceleration;
		if (velocity.y < 0.f) {
			velocity.y = 0.f;
		}
	} else if (velocity.y < 0.f) {
		// Si aici pentru cand se misca in sus
		velocity.y = std::max(velocity.y, -maxVelocity);
		// Si tot pentru mersul in sus
		velocity.y += deceleration;
		if (velocity.y > 0.f) {
			velocity.y = 0.f;
		}
	}
	// Ultimul move
	currentSprite.move(velocity * deltaTime);
}

MovementComponent& MovementComponent::operator=(const MovementComponent& other)
{
	if (this != &other) {
		currentSprite = other.currentSprite;
		maxVelocity = other.maxVelocity;
		velocity = other.velocity;
		acceleration = other.acceleration;
		deceleration = other.deceleration;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const MovementComponent& movementComp) {
	out << "Max Velocity: " << movementComp.maxVelocity << "\n";
	out << "Velocity: (" << movementComp.velocity.x << ", " << movementComp.velocity.y << ")\n";
	out << "Acceleration: " << movementComp.acceleration << "\n";
	out << "Deceleration: " << movementComp.deceleration << "\n";
	return out;
}
