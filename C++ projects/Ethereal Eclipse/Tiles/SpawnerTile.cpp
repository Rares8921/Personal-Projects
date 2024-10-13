#include "SpawnerTile.h"
#include <sstream>

SpawnerTile::SpawnerTile(float gridX, float gridY, float gridDimension, const sf::Texture& spawnerTexture, const sf::IntRect& textureRect, int type, int amount, int timeToSpawn, float maxTravelDistance)
	: Tile(TILE_TYPE::SPAWNER, gridX, gridY, gridDimension, spawnerTexture, textureRect, false),
	  type(type), amount(amount), counter(0),
	timeToSpawn(timeToSpawn), maxTravelDistance(maxTravelDistance), isFirstSpawn(true) {
	spawnerTimer.restart();
}

SpawnerTile::~SpawnerTile() {

}

const std::string& SpawnerTile::getTileAsString() const {
	std::stringstream outputString;
	sf::IntRect currentRect = getTileRect();
	outputString << getTileType() << " " << currentRect.left << " " << currentRect.top << " "
		<< type << " " << amount << " " << timeToSpawn << " " << maxTravelDistance;
	return outputString.str();
}

const int& SpawnerTile::getSpawnerType() const {
	return type;
}

const int& SpawnerTile::getEnemyAmount() const {
	return amount;
}

const int& SpawnerTile::getEnemyCounter() const {
	return counter;
}

const float& SpawnerTile::getMaxTravelDistance() const {
	return maxTravelDistance;
}

const bool& SpawnerTile::getSpawnerTimer() {
	if (spawnerTimer.getElapsedTime().asSeconds() >= timeToSpawn || isFirstSpawn) {
		spawnerTimer.restart();
		isFirstSpawn = false;
		return true;
	}
	return false;
}

void SpawnerTile::increaseCounter() {
	counter = std::min(counter + 1, amount);
}

void SpawnerTile::decreaseCounter() {
	counter = std::max(counter - 1, 0);
}

SpawnerTile& SpawnerTile::operator=(const SpawnerTile& other) {
	if (this != &other) {
		spawnerTimer = other.spawnerTimer;
		type = other.type;
		amount = other.amount;
		counter = other.counter;
		timeToSpawn = other.timeToSpawn;
		maxTravelDistance = other.maxTravelDistance;
		isFirstSpawn = other.isFirstSpawn;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const SpawnerTile& spawnerTile) {
	out << "Spawner Tile details:\n";
	out << "Type: " << spawnerTile.type << "\n";
	out << "Amount: " << spawnerTile.amount << "\n";
	out << "Counter: " << spawnerTile.counter << "\n";
	out << "Time to Spawn: " << spawnerTile.timeToSpawn << "\n";
	out << "Max Travel Distance: " << spawnerTile.maxTravelDistance << "\n";
	out << "Is First Spawn: " << (spawnerTile.isFirstSpawn ? "true" : "false") << "\n";
	return out;
}
