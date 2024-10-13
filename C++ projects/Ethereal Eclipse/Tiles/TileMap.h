#pragma once

#include "SFML\Graphics.hpp"
#include "RegularTile.h";
#include "SpawnerTile.h";
#include "../Entity.h"
#include "../EnemySystem.h"
#include <vector>
#include <stack>

class TileMap {
private:
	int gridDimension, numberOfLayers;
	sf::Vector2f maxSizeGrid, maxSizeWorld;
	std::string textureFile;
	// Structura: Linii, coloane, layere(momentan este doar 1 layer) si tipul tile-ului
	// Pentru a ajunge la coloana tile-ului trebuie sa fie parcurse inainte si layer-ul de care apartine
	// Si linia unde este pozitionata
	// Astfel pot crea un tilemap usor prin care restrictionez jucatorul sa mearga prin anumite zone
	std::vector<std::vector<std::vector<std::vector<Tile*>>>> tileMap;
	// Exemplu de folosire a deffered render de pe linkedin:
	// For example, it can handle multiple dynamic lights in the scene without a significant performance impact
	// as the lighting calculations are done in screen space.	
	std::stack<Tile*> defferedStack; 
	sf::Texture tileTextureSheet;
	sf::RectangleShape collisionBox;
	// Pentru culling
	int fromX, toX;
	int fromY, toY;
	int layer;
	void clearTileMap();
public:
	TileMap(float gridDimension, float gridWidth, float gridHeight, std::string pathToTileTexture);
	TileMap(const std::string file_name);
	virtual ~TileMap();
	void addTile(const int type, const float x, const float y, const float z, sf::IntRect textureRect, const bool collision);
	void addTile(const float x, const float y, const float z, sf::IntRect textureRect,
		const int enemyType, const int enemyAmount, const int enemyTimeToSpawn, const int enemyMaxTravelDistance);
	void saveToFile(const std::string fileName);
	void loadFromFile(const std::string fileName);
	void updateForCollision(Entity* currentEntity, const float& deltaTime);
	void updateTileCollision(Entity* currentEntity, const float& deltaTime);
	void updateTiles(Entity* currentEntity, const float& deltaTime, EnemySystem& enemySystem);
	void updateTileMap();
	void renderTileMap(sf::RenderTarget& currentWindow,
		const sf::Vector2i& gridPosition,
		sf::Shader* shader = NULL,
		const sf::Vector2f playerPosition = sf::Vector2f(),
		const bool collision = false);
	void renderDeferred(sf::RenderTarget& target, sf::Shader* shader, const sf::Vector2f playerPosition);
	const sf::Vector2f& getMaxSize() const;
	const sf::Vector2f& getMaxSizeGrid() const;
	TileMap& operator=(const TileMap& other);

	friend std::ostream& operator<<(std::ostream& out, const TileMap& tileMap);
};

