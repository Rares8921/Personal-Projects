#include "Player.h"
#include <sstream>

void Player::initVariables() {
	initAttacking = false;
	isAttacking = false;
	playerWeapon = new Sword(1, 2, 5, 100, 20, "Textures/sword.png");
	playerWeapon->generate(1, 3);
	maxDamageTimer = 3000;
}

void Player::initAnimations() {
	animationComponent->addAnimation("IDLE", 5.f, 0, 0, 6, 0, 64, 64, 1);
	animationComponent->addAnimation("LEFT_WALK", 30.f, 0, 12, 6, 12, 64, 64, 1);
	animationComponent->addAnimation("TOP_LEFT_WALK", 20.f, 0, 14, 6, 14, 64, 64, 1);
	animationComponent->addAnimation("TOP_WALK", 30.f, 0, 17, 6, 17, 64, 64, 1);
	animationComponent->addAnimation("TOP_RIGHT_WALK", 20.f, 0, 15, 6, 15, 64, 64, 1);
	animationComponent->addAnimation("RIGHT_WALK", 30.f, 0, 13, 6, 13, 64, 64, 1);
	animationComponent->addAnimation("BOTTOM_RIGHT_WALK", 20.f, 0, 11, 6, 11, 64, 64, 1);
	animationComponent->addAnimation("BOTTOM_WALK", 30.f, 0, 16, 6, 16, 64, 64, 1);
	animationComponent->addAnimation("BOTTOM_LEFT_WALK", 20.f, 0, 12, 6, 12, 64, 64, 1);
}

void Player::initInventory() {
	playerInventory = new Inventory(100);
}

Player::Player(float x, float y, sf::Texture &textureSheet) {

	initVariables();
	setTexture(textureSheet);
	setPosition(x, y);

	createAttributeComponent(1);
	createMovementComponent(100.0f, 10.0f, 4.0f);
	createAnimationComponent(textureSheet);
	createHitboxComponent(entityShapeSprite, 22.5f, 11.5f, 37, 64);
	createSkillComponent();

	initAnimations();
	initInventory();
}

Player::~Player() {
	delete playerInventory;
	delete playerWeapon;
}

AttributeComponent* Player::getAttribute() const {
	return attributeComponent;
}

Weapon* Player::getWeapon() const {
	return playerWeapon;
}

const bool& Player::getInitAttacking() const {
	return initAttacking;
}

const bool Player::getDamageTimer() {
	if (damageTimer.getElapsedTime().asMilliseconds() >= maxDamageTimer) {
		damageTimer.restart();
		return true;
	}
	return false;
}

const int Player::getDamage() const {
	return rand() % (
		(attributeComponent->getMaxAttackDamage() + playerWeapon->getDamageMax())
		- (attributeComponent->getMinAttackDamage() + playerWeapon->getDamageMin()) + 1)
		+ (attributeComponent->getMinAttackDamage() + playerWeapon->getDamageMin());
}

void Player::setInitAttacking(const bool initAttack) {
	initAttacking = initAttack;
}

void Player::playDeathAnimation(const float& deltaTime) {
	animationComponent->playAnimation("DEATH", deltaTime);
}

void Player::updateAnimation(const float& deltaTime) {
	movementComponent->update(deltaTime);
	if (movementComponent->getState(IDLE)) {
		movementComponent->setMaxVelocity(100.f);
		animationComponent->playAnimation("IDLE", deltaTime);
	} else if(movementComponent->getState(MOVING_BOTTOM_LEFT)) {
		movementComponent->setMaxVelocity(75.f);
		animationComponent->playAnimation("BOTTOM_LEFT_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_TOP_LEFT)) {
		movementComponent->setMaxVelocity(75.f);
		animationComponent->playAnimation("TOP_LEFT_WALK", deltaTime);
	} else if(movementComponent->getState(MOVING_TOP_RIGHT)){
		movementComponent->setMaxVelocity(75.f);
		animationComponent->playAnimation("TOP_RIGHT_WALK", deltaTime);
	} else if(movementComponent->getState(MOVING_BOTTOM_RIGHT)){
		movementComponent->setMaxVelocity(75.f);
		animationComponent->playAnimation("BOTTOM_RIGHT_WALK", deltaTime);
	} else if(movementComponent->getState(MOVING_BOTTOM_LEFT)){
		movementComponent->setMaxVelocity(75.f);
		animationComponent->playAnimation("BOTTOM_LEFT_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_BOTTOM)) {
		movementComponent->setMaxVelocity(100.f);
		animationComponent->playAnimation("BOTTOM_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_LEFT)) {
		movementComponent->setMaxVelocity(100.f);
		animationComponent->playAnimation("LEFT_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_TOP)) {
		movementComponent->setMaxVelocity(100.f);
		animationComponent->playAnimation("TOP_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_RIGHT)) {
		movementComponent->setMaxVelocity(100.f);
		animationComponent->playAnimation("RIGHT_WALK", deltaTime);
	}
}

void Player::update(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& view)
{
	movementComponent->update(deltaTime);
	updateAnimation(deltaTime);
	hitboxComponent->updateHitbox();
	playerWeapon->update(mousePosView, sf::Vector2f(getSpriteCenter().x, getSpriteCenter().y + 5.f));
}

const std::string Player::toStringCharacterTab() const {
	std::stringstream ss;
	const Weapon* w = playerWeapon;

	if (attributeComponent) {
		ss << "Level: " << attributeComponent->getCurrentLevel() << "\n"
			<< "Exp: " << attributeComponent->getCurrentExp() << "\n"
			<< "Exp to next: " << attributeComponent->getMaxExp() << "\n"
			<< "Weapon level: " << w->getLevel() << "\n"
			<< "Weapon type: " << w->getType() << "\n"
			<< "Weapon value: " << w->getValue() << "\n"
			<< "Weapon range: " << w->getRange() << "\n"
			<< "Minimum damage: " << w->getDamageMin() + attributeComponent->getMinAttackDamage() << " (" << attributeComponent->getMinAttackDamage() << ")" << "\n"
			<< "Maximum damage: " << w->getDamageMax() + attributeComponent->getMaxAttackDamage() << " (" << attributeComponent->getMaxAttackDamage() << ")" << "\n";
	}
	return ss.str();
}

void Player::loseHp(const int hp) {
	attributeComponent->updateHp(-hp);
}

void Player::gainHp(const int hp) {
	attributeComponent->updateHp(hp);
}

void Player::loseExp(const int exp) {
	attributeComponent->updateExp(-exp);
}

void Player::gainExp(const int exp) {
	attributeComponent->updateExp(exp);
}

void Player::renderEntity(sf::RenderTarget& target, sf::Shader* shader, const sf::Vector2f lightPosition,
	const bool showHitbox) {
	if (shader) {
		shader->setUniform("hasTexture", true);
		shader->setUniform("lightPosition", lightPosition);
		target.draw(entityShapeSprite, shader);

		shader->setUniform("hasTexture", true);
		shader->setUniform("lightPosition", lightPosition);
		playerWeapon->render(target, shader);
	} else {
		target.draw(entityShapeSprite);
		playerWeapon->render(target);
	}
	if (showHitbox) {
		hitboxComponent->renderHitbox(target);
	}
}

Player& Player::operator=(const Player& other) {
	if (this != &other) {
		// Copierea membrilor clasici
		isAttacking = other.isAttacking;
		initAttacking = other.initAttacking;
		maxDamageTimer = other.maxDamageTimer;
		playerWeapon = other.playerWeapon;
		if (other.playerInventory != nullptr) {
			playerInventory = new Inventory(*other.playerInventory);
		} else {
			playerInventory = nullptr;
		}
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Player& player) {
	out << "Is Attacking: " << player.isAttacking << "\n";
	out << "Init Attacking: " << player.initAttacking << "\n";
	return out;
}
