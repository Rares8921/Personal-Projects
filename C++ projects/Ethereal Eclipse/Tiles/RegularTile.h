#pragma once

#include "Tile.h"

class RegularTile : public Tile {
public:
	RegularTile(int type, float x, float y, float gridSize, sf::Texture& tileTexture, sf::IntRect& textureRect,
		bool collision = false);
	virtual ~RegularTile();
	virtual const std::string& getTileAsString() const;
	RegularTile& operator=(const RegularTile& other);

	friend std::ostream& operator<<(std::ostream& out, const RegularTile& regularTile);

};