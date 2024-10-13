#pragma once

#include "SFML\Graphics.hpp"

enum TILE_TYPE {
	DEFAULTTILE,
	DAMAGING,
	DECORATION,
	SPAWNER,
	SCENE_SWAPPER,
};
	

class Tile
{
private:
	sf::RectangleShape tileShape;
	bool hasCollision;
	int tileType;
public:
	Tile();
	Tile(int type, float x, float y, float gridSize, const sf::Texture& tileTexture, const sf::IntRect& textureRect, const bool collision);
	virtual ~Tile();
	const bool& getCollisionProperty() const;
	const int& getTileType() const;
	const sf::RectangleShape& getTileShape() const;
	const sf::IntRect& getTileRect() const;
	const sf::Vector2f& getTilePosition() const;
	const sf::FloatRect& getGlobalBounds() const;
	const bool& intersects(const sf::FloatRect currentBounds) const;
	virtual const std::string& getTileAsString() const = 0;
	virtual void updateTile();
	virtual void renderTile(sf::RenderTarget& window, sf::Shader* shader = NULL, const sf::Vector2f player_position = sf::Vector2f());
	Tile& operator=(const Tile& other);
	friend std::ostream& operator<<(std::ostream& out, const Tile& tile);
};

