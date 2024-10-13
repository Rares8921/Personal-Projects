#pragma once
#include "Tile.h"
class SpawnerTile : public Tile {
private:
	sf::Clock spawnerTimer;
	int type, amount, counter, timeToSpawn;
	float maxTravelDistance;
	bool isFirstSpawn; // Pentru boss fights
public:
	SpawnerTile(float gridX, float gridY, float gridDimension,
		const sf::Texture& spawnerTexture, const sf::IntRect& textureRect,
		int type, int amount, int timeToSpawn, float maxTravelDistance
		);
	virtual ~SpawnerTile();

	virtual const std::string& getTileAsString() const;
	const int& getSpawnerType() const;
	const int& getEnemyAmount() const;
	const int& getEnemyCounter() const;
	const float& getMaxTravelDistance() const;
	const bool& getSpawnerTimer();

	void increaseCounter();
	void decreaseCounter();
	SpawnerTile& operator=(const SpawnerTile& other);

	friend std::ostream& operator<<(std::ostream& out, const SpawnerTile& spawnerTile);


};

