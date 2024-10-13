#include "RegularTile.h"
#include <sstream>

RegularTile::RegularTile(int type, float x, float y, float gridSize, sf::Texture& tileTexture, sf::IntRect& textureRect, const bool collision)
	: Tile(type, x, y, gridSize, tileTexture, textureRect, collision)
{
}

RegularTile::~RegularTile() {
}

const std::string& RegularTile::getTileAsString() const {
	std::stringstream outputString;
	sf::IntRect currentRect = getTileRect();
	outputString << getTileType() << " " << currentRect.left << " " << currentRect.top << " " << getCollisionProperty();
	//std::cout << outputString.str();
	return outputString.str();
}

RegularTile& RegularTile::operator=(const RegularTile& other) {
	return *this;
}

std::ostream& operator<<(std::ostream& out, const RegularTile& regularTile) {
	return out;
}
