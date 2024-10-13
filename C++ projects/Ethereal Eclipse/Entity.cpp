#include "Entity.h"
#include <iostream>

void Entity::initVariables() {
	movementComponent = NULL;
	animationComponent = NULL;
	hitboxComponent = NULL;
	movementSpeed = 100.0f;
	entityShape.setFillColor(sf::Color::White);
	entityShape.setSize(sf::Vector2f(50.0f, 50.0f));
}

void Entity::setAttacking(const bool& value) {
	isAttacking = value;
}

const bool& Entity::getAttacking() const {
	return isAttacking;
}

Entity::Entity() {
	initVariables();
}

Entity::~Entity() {
	delete attributeComponent;
	delete movementComponent;
	delete animationComponent;
	delete hitboxComponent;
	delete skillComponent;
}

void Entity::setTexture(sf::Texture& texture) {
	entityShapeSprite.setTexture(texture);
}

void Entity::createAttributeComponent(const int level) {
	attributeComponent = new AttributeComponent(level);
}

void Entity::createMovementComponent(const float maxVelocity, const float acceleration, const float deceleration) {
	movementComponent = new MovementComponent(entityShapeSprite, maxVelocity, acceleration, deceleration);
}

void Entity::createAnimationComponent(sf::Texture& textureSheet) {
	animationComponent = new AnimationComponent(entityShapeSprite, textureSheet);
}

void Entity::createHitboxComponent(sf::Sprite& textureSheet, float offsetX, float offsetY,
	float width, float height) {
	hitboxComponent = new HitboxComponent(entityShapeSprite, offsetX, offsetY, width, height);
}

void Entity::createSkillComponent() {
	skillComponent = new SkillComponent();
}

MovementComponent* Entity::getMovementComponent() {
	return movementComponent;
}

AnimationComponent* Entity::getAnimationComponent() {
	return animationComponent;
}

AttributeComponent* Entity::getAttributeComponent() {
	return attributeComponent;
}

SkillComponent* Entity::getSkillComponent() {
	return skillComponent;
}

void Entity::setVelocity(const float x, const float y) {
	movementComponent->setVelocity(x, y);
}

void Entity::setVelocityX(const float x) {
	movementComponent->setVelocityX(x);
}

void Entity::setVelocityY(const float y) {
	movementComponent->setVelocityY(y);
}

void Entity::setPosition(const float x, const float y) {
	if (hitboxComponent) {
		hitboxComponent->setPosition(sf::Vector2f(x, y));
		return;
	}
	entityShapeSprite.setPosition(x, y);
}

const sf::Vector2f& Entity::getPosition() const
{
	if (hitboxComponent) {
		return hitboxComponent->getPosition();
	}
	return entityShapeSprite.getPosition();
}

const sf::Vector2f& Entity::getCenter() const {
	sf::FloatRect bounds = entityShapeSprite.getGlobalBounds();
	if (hitboxComponent) {
		bounds = hitboxComponent->getGlobalBounds();
		return hitboxComponent->getPosition() + sf::Vector2f(bounds.width / 2.f, bounds.height / 2.f);
	}
	return entityShapeSprite.getPosition() + sf::Vector2f(bounds.width / 2.f, bounds.height / 2.f);
}

const sf::Vector2f Entity::getSpriteCenter() const {
	return entityShapeSprite.getPosition() + 
		sf::Vector2f(entityShapeSprite.getGlobalBounds().width / 2.f, entityShapeSprite.getGlobalBounds().height / 2.f);
}

const sf::Vector2i& Entity::getGridPosition(const int gridSize) const {
	if (hitboxComponent) {
		return sf::Vector2i(
			hitboxComponent->getPosition().x / gridSize,
			hitboxComponent->getPosition().y / gridSize
		);
	}
	return sf::Vector2i(
		entityShapeSprite.getPosition().x / gridSize,
		entityShapeSprite.getPosition().y / gridSize
	);
}

const sf::FloatRect& Entity::getGlobalBounds() const {
	if (hitboxComponent) {
		return hitboxComponent->getGlobalBounds();
	}
	return entityShapeSprite.getGlobalBounds();
}

const sf::FloatRect& Entity::getNextPositionGlobalBounds(const float& deltaTime) const {
	// Daca are un hitbox si se misca atunci se poate prezice urmatoarea miscarea
	if (hitboxComponent && movementComponent) {
		return hitboxComponent->getNextPosition(movementComponent->getMaxVelocity() * deltaTime);
	}
	return sf::FloatRect(-1, -1, -1, -1);
}

const float Entity::getSpriteDistance(const Entity& entity) const {
	return sqrt(pow(getSpriteCenter().x - entity.getSpriteCenter().x, 2) + pow(getSpriteCenter().y - entity.getSpriteCenter().y, 2));
}

void Entity::moveEntity(const float& deltaTime, const float x, const float y) {
	if (movementComponent) {
		// Practic setez velocity-ul
		movementComponent->move(x, y, deltaTime);
		// Iar aici folosesc acel velocity
		entityShapeSprite.move(movementComponent->getMaxVelocity() * deltaTime);
	}
}

void Entity::updateEntity(const float& deltaTime) {
	if (movementComponent) {
		movementComponent->update(deltaTime);
	}
}

Entity& Entity::operator=(const Entity& other) {
	if (this != &other) {
		entityShape = other.entityShape;
		entityShapeSprite = other.entityShapeSprite;
		movementSpeed = other.movementSpeed;
		if (hitboxComponent) {
			delete hitboxComponent;
		}
		hitboxComponent = new HitboxComponent(*other.hitboxComponent);
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Entity& entity) {
	out << "Movement Speed: " << entity.movementSpeed << "\n";
	return out;
}
