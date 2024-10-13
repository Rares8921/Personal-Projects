#include "Tile.h"
#include <iostream>

Tile::Tile() {
	hasCollision = false;
	tileType = 0;
}

Tile::Tile(int type, float x, float y, float gridSize, const sf::Texture& tileTexture, const sf::IntRect& textureRect, const bool collision) {
	tileType = type;
	tileShape.setSize(sf::Vector2f(gridSize, gridSize));
	tileShape.setFillColor(sf::Color::Black);
	tileShape.setOutlineThickness(1.f);
	tileShape.setOutlineColor(sf::Color::Black);
	tileShape.setPosition(x * gridSize, y * gridSize);
	tileShape.setTexture(&tileTexture);
	tileShape.setTextureRect(textureRect);
	hasCollision = collision;
}

Tile::~Tile() {
}

const bool& Tile::getCollisionProperty() const {
	return hasCollision;
}

const int& Tile::getTileType() const {
	return tileType;
}

const sf::RectangleShape& Tile::getTileShape() const {
	return tileShape;
}

const sf::IntRect& Tile::getTileRect() const {
	return tileShape.getTextureRect();
}


const sf::Vector2f& Tile::getTilePosition() const {
	return tileShape.getPosition();
}

const sf::FloatRect& Tile::getGlobalBounds() const {
	return tileShape.getGlobalBounds();
}

const bool& Tile::intersects(const sf::FloatRect currentBounds) const {
	return tileShape.getGlobalBounds().intersects(currentBounds);
}

void Tile::updateTile()
{
}

void Tile::renderTile(sf::RenderTarget& window, sf::Shader* shader, const sf::Vector2f player_position) {
	if (shader) {
		shader->setUniform("hasTexture", true);
		shader->setUniform("lightPosition", player_position);
		window.draw(getTileShape(), shader);
	}
	else {
		tileShape.setPosition(tileShape.getPosition());
		window.draw(tileShape);
	}
}

Tile& Tile::operator=(const Tile& other) {
	if (this != &other) {
		tileShape = other.tileShape;
		hasCollision = other.hasCollision;
		tileType = other.tileType;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Tile& tile) {
	out << "Tile Shape: (" << tile.tileShape.getPosition().x << ", " << tile.tileShape.getPosition().y << "), "
		<< "Size(" << tile.tileShape.getSize().x << ", " << tile.tileShape.getSize().y << ")\n";
	out << "Has Collision: " << (tile.hasCollision ? "true" : "false") << "\n";
	out << "Tile Type: " << tile.tileType << "\n";
	return out;
}
