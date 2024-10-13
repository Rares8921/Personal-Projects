#include "Sword.h"

Sword::Sword(int level, int damageMin, int damageMax, int range, int value,
	std::string pathToTexture) : MeleeWeapon(level, damageMin, damageMax, range, value, pathToTexture) {

	weaponSprite.setOrigin(weaponSprite.getGlobalBounds().width / 2.f, weaponSprite.getGlobalBounds().height);

}

Sword::~Sword() {}

Sword* Sword::clone() {
	return new Sword(*this);
}

void Sword::update(const sf::Vector2f& mousePosView, const sf::Vector2f center) {
	weaponSprite.setPosition(center);

	float dX = mousePosView.x - weaponSprite.getPosition().x;
	float dY = mousePosView.y - weaponSprite.getPosition().y;

	const float PI = 3.14159265f;
	// https://en.wikipedia.org/wiki/Atan2
	float deg = atan2(dY, dX) * 180.f / PI;

	if (attackTimer.getElapsedTime().asMilliseconds() < maxAttackTimer / 4) {
		float len = 1.f * std::sqrt(pow(dX, 2) + pow(dY, 2));
		sf::Vector2f normVec(dX / len, dY / len);
		weaponSprite.setPosition(center.x + normVec.x * 10.f, center.y + normVec.y * 10.f);
	} else {
		weaponSprite.setRotation(deg + 90.f);
	}
}

void Sword::render(sf::RenderTarget& currentWindow, sf::Shader* shader) {
	if (shader) {
		currentWindow.draw(weaponSprite, shader);
	} else {
		currentWindow.draw(weaponSprite);
	}
}

