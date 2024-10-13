#include "Enemy.h"

void Enemy::initVariables() {
	expAmount = 10;
	damageTimerMax = 1000;
	despawnTimerMax = 1000;
}

Enemy::Enemy(SpawnerTile& enemySpawnerTile) : enemySpawnerTile(enemySpawnerTile) {
	initVariables();
}

Enemy::~Enemy() {

}

const sf::Clock& Enemy::getDamageTimer() const {
	return damageTimer;
}

const int& Enemy::getMaxDamageTimer() const {
	return damageTimerMax;
}

const int& Enemy::getExpAmount() const {
	return expAmount;
}

SpawnerTile& Enemy::getSpawnerTile() {
	return enemySpawnerTile;
}

const sf::Sprite& Enemy::getRatSprite() const {
	return entityShapeSprite;
}

const bool& Enemy::isDamageTimerDone() const {
	return damageTimer.getElapsedTime().asMilliseconds() >= damageTimerMax;
}

const bool& Enemy::isDespawnTimerDone() const {
	return despawnTimer.getElapsedTime().asMilliseconds() >= despawnTimerMax;
}

void Enemy::resetDamageTimer() {
	damageTimer.restart();
}

void Enemy::generateAttributes(const int level) {
	expAmount = level * (rand() % 5 + 1);
}

void Enemy::loseHP(const int hp) {
	if (attributeComponent) {
		attributeComponent->updateHp(-hp);
	}
}

const bool& Enemy::isDead() const {
	return (attributeComponent ? attributeComponent->isDead() : false);
}

const AttributeComponent* Enemy::getAttribute() const {
	if (attributeComponent) {
		return attributeComponent;
	}
	std::cout << "Enemy.cpp : AttributeComponent was not initialised!";
	return nullptr;
}

void Enemy::updateEntity(const float& deltaTime, sf::Vector2f& mousePosView, const sf::View& mapView) {
	float distance = sqrt(pow(getPosition().x - mapView.getCenter().x, 2));
	if (distance < 1500.f) {
		despawnTimer.restart();
	}
}

Enemy& Enemy::operator=(const Enemy& other) {
	if (this != &other) {
		enemySpawnerTile = other.enemySpawnerTile;
		expAmount = other.expAmount;
		damageTimer = other.damageTimer;
		despawnTimer = other.despawnTimer;
		damageTimerMax = other.damageTimerMax;
		despawnTimerMax = other.despawnTimerMax;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Enemy& enemy) {
	out << "Exp Amount: " << enemy.expAmount << "\n";
	out << "Damage Timer Max: " << enemy.damageTimerMax << "\n";
	out << "Despawn Timer Max: " << enemy.despawnTimerMax << "\n";
	return out;
}
