#include "HitboxComponent.h"

HitboxComponent::HitboxComponent(sf::Sprite& hitboxSprite,
	float offsetX, float offsetY,
	float width, float height) : hitboxSprite(hitboxSprite), offsetX(offsetX), offsetY(offsetY) {
	
	nextPosition.left = 0.f; nextPosition.top = 0.f;
	nextPosition.width = width; nextPosition.height = height;
	hitboxRectangle.setPosition(hitboxSprite.getPosition() + sf::Vector2f(offsetX, offsetY));
	hitboxRectangle.setSize(sf::Vector2f(width, height));
	hitboxRectangle.setFillColor(sf::Color::Transparent);
	hitboxRectangle.setOutlineThickness(-1.f); // grosimea border-ului
	hitboxRectangle.setOutlineColor(sf::Color::Green);

}

HitboxComponent::~HitboxComponent() {
}

void HitboxComponent::setPosition(const sf::Vector2f& newPosition) {
	hitboxRectangle.setPosition(newPosition);
	hitboxSprite.setPosition(newPosition - sf::Vector2f(offsetX, offsetY));
}

const sf::Vector2f& HitboxComponent::getPosition() const
{
	return hitboxRectangle.getPosition();
}

bool HitboxComponent::checkIntersection(const sf::FloatRect& currentRectangle) {
	return hitboxRectangle.getGlobalBounds().intersects(currentRectangle);
}

void HitboxComponent::updateHitbox() {
	hitboxRectangle.setPosition(hitboxSprite.getPosition() + sf::Vector2f(offsetX, offsetY));
}

void HitboxComponent::renderHitbox(sf::RenderTarget& window) {
	window.draw(hitboxRectangle);
}

const sf::FloatRect& HitboxComponent::getGlobalBounds() const {
	return hitboxRectangle.getGlobalBounds();
}

const sf::FloatRect& HitboxComponent::getNextPosition(const sf::Vector2f& velocity) {
	nextPosition.left = hitboxRectangle.getPosition().x + velocity.x;
	nextPosition.top = hitboxRectangle.getPosition().y + velocity.y;

	return nextPosition;
}

HitboxComponent& HitboxComponent::operator=(const HitboxComponent& other) {
	if (this != &other) {
		hitboxSprite = other.hitboxSprite;
		hitboxRectangle = other.hitboxRectangle;
		nextPosition = other.nextPosition;
		offsetX = other.offsetX;
		offsetY = other.offsetY;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const HitboxComponent& hitboxComp) {
	out << "Offset X: " << hitboxComp.offsetX << "\n";
	out << "Offset Y: " << hitboxComp.offsetY << "\n";
	return out;
}
