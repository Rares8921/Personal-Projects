#include "Rat.h"

void Rat::initAnimations() {
	animationComponent->addAnimation("IDLE", 25.f, 0, 0, 3, 0, 60, 64, 0);
	animationComponent->addAnimation("WALK_DOWN", 11.f, 0, 1, 3, 1, 60, 64, 0);
	animationComponent->addAnimation("WALK_LEFT", 11.f, 0, 2, 3, 2, 60, 64, 0);
	animationComponent->addAnimation("WALK_RIGHT", 11.f, 0, 3, 3, 3, 60, 64, 0);
	animationComponent->addAnimation("WALK_UP", 11.f, 0, 4, 3, 4, 60, 64, 0);
	animationComponent->addAnimation("ATTACK", 5.f, 0, 2, 1, 2, 60, 64, 0);
}


void Rat::initGUI() {
	hpBar.setFillColor(sf::Color::Red);
	hpBar.setSize(sf::Vector2f(60.f, 10.f));
	hpBar.setPosition(entityShapeSprite.getPosition());
}

Rat::Rat(float x, float y, sf::Texture& textureSheet, SpawnerTile& enemySpawnerTile, Entity& player) 
	: Enemy(enemySpawnerTile) {

	initGUI();
	createHitboxComponent(entityShapeSprite, 28.f, 69.f, 30.f, 30.f);
	createMovementComponent(25.f, 800.f, 500.f);
	createAnimationComponent(textureSheet);
	createAttributeComponent(1);

	generateAttributes(attributeComponent->getCurrentLevel());

	setPosition(x, y);
	initAnimations();

	// AIFollow(enemy, player)
	ratFollow = new AIFollow(*this, player);
}

Rat::~Rat() {
	delete ratFollow;
}
void Rat::updateAnimation(const float& deltaTime) {
	if (movementComponent->getState(IDLE)) {
		animationComponent->playAnimation("IDLE", deltaTime);
	} else if (movementComponent->getState(MOVING_BOTTOM)) {
		animationComponent->playAnimation("BOTTOM_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_LEFT)) {
		animationComponent->playAnimation("LEFT_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_TOP)) {
		animationComponent->playAnimation("TOP_WALK", deltaTime);
	} else if (movementComponent->getState(MOVING_RIGHT)) {
		animationComponent->playAnimation("RIGHT_WALK", deltaTime);
	}
	if (getDamageTimer().getElapsedTime().asMilliseconds() <= getMaxDamageTimer()) {
		entityShapeSprite.setColor(sf::Color::Red);
	} else {
		entityShapeSprite.setColor(sf::Color::White);
	}
}

void Rat::updateEntity(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& mapView) {
	Enemy::updateEntity(deltaTime, mousePosView, mapView);
	movementComponent->update(deltaTime);
	AttributeComponent* ac = getAttributeComponent();
	hpBar.setSize(sf::Vector2f(60.f * (1.0f * ac->getCurrentHp() / ac->getMaxHp()), 10.f));
	hpBar.setPosition(entityShapeSprite.getPosition());
	updateAnimation(deltaTime);
	hitboxComponent->updateHitbox();
	ratFollow->update(deltaTime);
}



void Rat::renderEntity(sf::RenderTarget& currentWindow, sf::Shader* shader,
	const sf::Vector2f lightPosition, const bool showHitbox) {

	if (shader) {
		shader->setUniform("hasTexture", true);
		shader->setUniform("lightPosition", lightPosition);
		currentWindow.draw(entityShapeSprite, shader);
	} else {
		currentWindow.draw(entityShapeSprite);
	}

	currentWindow.draw(hpBar);

	if (showHitbox) {
		hitboxComponent->renderHitbox(currentWindow);
	}
}

Rat& Rat::operator=(const Rat& other) {
	if (this != &other) {
		this->hpBar = other.hpBar;
		if (other.ratFollow != nullptr) {
			this->ratFollow = new AIFollow(*other.ratFollow);
		}
		else {
			this->ratFollow = nullptr;
		}
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Rat& rat) {
	return out;
}
