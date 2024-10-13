#pragma once

#include "SFML\Graphics.hpp"
#include "AttributeComponent.h"

class HitboxComponent {
private:
	sf::Sprite& hitboxSprite; // Sprite& - daca am un hitbox trebuie neaparat sa am un sprite
	sf::RectangleShape hitboxRectangle;
	sf::FloatRect nextPosition;
	float offsetX, offsetY;
public:
	HitboxComponent(sf::Sprite& hitboxSprite, 
		float offsetX, float offsetY, 
		float width, float height);
	virtual ~HitboxComponent();
	// Verific cazul de colision
	void setPosition(const sf::Vector2f& newPosition);
	const sf::Vector2f& getPosition() const;
	bool checkIntersection(const sf::FloatRect& currentRectangle);
	void updateHitbox();
	void renderHitbox(sf::RenderTarget& window);
	const sf::FloatRect& getGlobalBounds() const;
	const sf::FloatRect& getNextPosition(const sf::Vector2f& velocity);
	HitboxComponent& operator=(const HitboxComponent& other);
	friend std::ostream& operator<<(std::ostream& out, const HitboxComponent& hitboxComp);
};