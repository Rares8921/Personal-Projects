#include "TileMap.h"
#include <iostream>
#include <fstream>

void TileMap::clearTileMap() {
	if (!tileMap.empty()) {
		for (int i = 0; i < tileMap.size(); ++i) {
			for (int j = 0; j < tileMap[i].size(); ++j) {
				for (int k = 0; k < tileMap[i][j].size(); ++k) {
					for (int l = 0; l < tileMap[i][j][k].size(); l++) {
						delete tileMap[i][j][k][l];
						tileMap[i][j][k][l] = NULL;
					}
					tileMap[i][j][k].clear();
				}
				tileMap[i][j].clear();
			}
			tileMap[i].clear();
		}
		tileMap.clear();
	}
}

TileMap::TileMap(float gridDimension, float gridWidth, float gridHeight, std::string pathToTileTexture)
	: gridDimension(gridDimension), maxSizeGrid(gridWidth, gridHeight) {
	
	maxSizeWorld = maxSizeGrid * gridDimension;
	textureFile = pathToTileTexture;
	numberOfLayers = 1;
	fromX = 0; toX = 0; fromY = 0; toY = 0; layer = 0;

	tileMap.resize(maxSizeGrid.x, std::vector< std::vector< std::vector <Tile*> > >());
	for (int i = 0; i < maxSizeGrid.x; ++i) {
		// Creez componenta grid-ului ce contine layerele
		tileMap.push_back(std::vector< std::vector< std::vector <Tile*> > >());
		for (int j = 0; j < maxSizeGrid.y; ++j) {
			// Adaug grid-ul propriu zis
			tileMap[i].resize(maxSizeGrid.y, std::vector< std::vector<Tile*> >());
			tileMap[i].push_back(std::vector< std::vector<Tile*> >());
			for (int k = 0; k < numberOfLayers; ++k) {
				tileMap[i][j].resize(numberOfLayers, std::vector<Tile*>());
			}
		}
		if (!tileTextureSheet.loadFromFile(pathToTileTexture)) {
			std::cout << "Tilesheet1 nu a putut fi incarcat!";
		}
		
		collisionBox.setSize(sf::Vector2f(gridDimension, gridDimension));
		//collisionBox.setOutlineThickness(1.f);
		//collisionBox.setOutlineColor(sf::Color::Red);
	}
}

TileMap::TileMap(const std::string file_name) {
	fromX = 0; toX = 0;
	fromY = 0; toY = 0;
	layer = 0;

	loadFromFile(file_name);

	collisionBox.setSize(sf::Vector2f(gridDimension, gridDimension));
	collisionBox.setFillColor(sf::Color(255, 0, 0, 50));
	//collisionBox.setOutlineColor(sf::Color::Red);
	//collisionBox.setOutlineThickness(1.f);
}

TileMap::~TileMap() {
	clearTileMap();
}

void TileMap::addTile(const int type, const float x, const float y, const float z,
					sf::IntRect textureRect, const bool collision) {
	// Iau indicii si adaug tile-ul la pozitia respectiva in matrice doar daca tilemap-ul accepta asta
	if (x < maxSizeGrid.x && y < maxSizeGrid.y && x >= 0 && y >= 0 && z < numberOfLayers && z >= 0) {
		if (tileMap[x][y][z].empty()) {
			tileMap[x][y][z].push_back(new RegularTile(type, x, y, gridDimension, tileTextureSheet, 
				textureRect, collision));
		}
	}
}

void TileMap::addTile(const float x, const float y, const float z,
	sf::IntRect textureRect, const int enemyType, const int enemyAmount,
	const int enemyTimeToSpawn, const int enemyMaxTravelDistance) {
	if (x < maxSizeGrid.x && y < maxSizeGrid.y && x >= 0 && y >= 0 && z < numberOfLayers && z >= 0) {
		if (tileMap[x][y][z].empty()) {
			tileMap[x][y][z].push_back(new SpawnerTile(x, y, gridDimension, tileTextureSheet, textureRect, 
					enemyType, enemyAmount, enemyTimeToSpawn, enemyMaxTravelDistance));
		}
	}
}

void TileMap::saveToFile(const std::string fileName) {
	// Stochez informatiile in urmatoarea forma:
	// gridSizeX gridSizeY
	// gridDimension
	// numberOfLayers
	// path filei cu textura
	// Acum am mai multe cazuri in functie de tipul de tile
	// Fiecare tile va incepe cu: i j k tipTile
	// Apoi se continua
	// RegularTile: linie coloana collision
	// SpawnerTile: linie coloana tip amount timetoSpawn maxTravelDistance
	// Toate informatiile despre tile-uri vor fi scrise in continuare pentru a nu mari prea mult size-ul filei
	std::ofstream fout(fileName);
	if (fout.is_open()) {
		fout << maxSizeGrid.x << " " << maxSizeGrid.y << "\n" << (int) gridDimension << "\n" << numberOfLayers << "\n"
			<< textureFile << "\n";
		for (int i = 0; i < maxSizeGrid.x; ++i) {
			for (int j = 0; j < maxSizeGrid.y; ++j) {
				for (int k = 0; k < numberOfLayers; ++k) {
					if (!tileMap[i][j][k].empty()) {
						for (int l = 0; l < tileMap[i][j][k].size(); ++l) {
							fout << i << " " << j << " " << k << " " << tileMap[i][j][k][l]->getTileAsString() << " ";
						}
					}
				}
			}
		}
	}
	fout.close();
}

void TileMap::loadFromFile(const std::string fileName) {
	std::ifstream fin(fileName);

	if (fin.is_open()) {

		fin >> maxSizeGrid.x >> maxSizeGrid.y >> gridDimension >> numberOfLayers >> textureFile;

		maxSizeWorld = maxSizeGrid * (1.0f * gridDimension);

		clearTileMap();

		tileMap.resize(maxSizeGrid.x, std::vector< std::vector< std::vector<Tile*> > >());
		for (int i = 0; i < maxSizeGrid.x; ++i) {
			for (int j = 0; j < maxSizeGrid.y; ++j) {
				tileMap[i].resize(maxSizeGrid.y, std::vector< std::vector<Tile*> >());
				for (int k = 0; k < numberOfLayers; ++k) {
					tileMap[i][j].resize(numberOfLayers, std::vector<Tile*>());
				}
			}
		}

		if (!tileTextureSheet.loadFromFile(textureFile))
			std::cout << "Tile-ul de la path-ul: " << textureFile << " nu a putut fi incarcat!\n";

		// Incarc toate tile-urile
		int x = 0, y = 0, z = 0;
		int trX = 0, trY = 0;
		bool collision = false;
		int type = 0;
		while (fin >> x >> y >> z >> type) {
			if (type == TILE_TYPE::SPAWNER) {
				//amount, time, maxTravelDistance
				int enemyType = 0;
				int	enemyAmount = 0;
				int	enemyTimeToSpawn = 0;
				int	enemyMaxTravelDistance = 0;

				fin >> trX >> trY >> enemyType >> enemyAmount >> enemyTimeToSpawn >> enemyMaxTravelDistance;

				tileMap[x][y][z].push_back(
					new SpawnerTile( x, y, gridDimension, tileTextureSheet, sf::IntRect(trX, trY, gridDimension, gridDimension),
						enemyType, enemyAmount, enemyTimeToSpawn, enemyMaxTravelDistance));
			} else  {
				fin >> trX >> trY >> collision;
				sf::IntRect temp = sf::IntRect(trX, trY, gridDimension, gridDimension);
				tileMap[x][y][z].push_back(
					new RegularTile(type, x, y, gridDimension, tileTextureSheet, temp, collision));
			}
		}
	}
	fin.close();
}

void TileMap::updateForCollision(Entity* currentEntity, const float& deltaTime) {
	if (currentEntity) {
		// Daca entitatea incearca sa iasa din harta prin partea stanga
		sf::Vector2f maxSize = getMaxSize();
		if (currentEntity->getPosition().x < 0.f) {
			currentEntity->setPosition(0.f, currentEntity->getPosition().y);
			currentEntity->setVelocityX(0.f);
			// Sau prin partea dreapta si trebuie tinut cont si de latimea entitatii
		}
		else if (currentEntity->getPosition().x + currentEntity->getGlobalBounds().width > maxSize.x) {
			currentEntity->setPosition(maxSize.x - currentEntity->getGlobalBounds().width, currentEntity->getPosition().y);
			currentEntity->setVelocityX(0.f);
		}

		// Daca entitatea incearca sa iasa din harta prin partea de sus
		if (currentEntity->getPosition().y < 0.f) {
			currentEntity->setPosition(currentEntity->getPosition().x, 0.f);
			currentEntity->setVelocityY(0.f);
			// Sau prin partea de jos si trebuie tinut cont si de inaltimea entitatii
		}
		else if (currentEntity->getPosition().y + currentEntity->getGlobalBounds().height > maxSize.y) {
			currentEntity->setPosition(currentEntity->getPosition().x, maxSize.y - currentEntity->getGlobalBounds().height);
			currentEntity->setVelocityY(0.f);
		}
	}

}

void TileMap::updateTileCollision(Entity* currentEntity, const float& deltaTime) {
	// Simulez un tile culling

	sf::Vector2f maxSize = getMaxSizeGrid();
	layer = 0;

	fromX = currentEntity->getGridPosition((int) gridDimension).x - 1;
	if (fromX < 0.f) {
		fromX = 0.f;
	}
	else if (fromX > maxSize.x) {
		fromX = maxSize.x;
	}
	toX = currentEntity->getGridPosition((int)gridDimension).x + 3;
	if (toX < 0.f) {
		toX = 0.f;
	}
	else if (toX > maxSize.x) {
		toX = maxSize.x;
	}
	fromY = currentEntity->getGridPosition((int)gridDimension).y - 1;
	if (fromY < 0.f) {
		fromY = 0.f;
	}
	else if (fromY > maxSize.y) {
		fromY = maxSize.y;
	}
	toY = currentEntity->getGridPosition((int)gridDimension).y + 3;
	if (toY < 0.f) {
		toY = 0.f;
	}
	else if (toY > maxSize.y) {
		toY = maxSize.y;
	}

	for (int i = fromX; i < toX; ++i) {
		for (int j = fromY; j < toY; ++j) {
			for (int k = 0; k < tileMap[i][j][layer].size(); ++k) {
				sf::FloatRect playerBounds = currentEntity->getGlobalBounds();
				sf::FloatRect tileBounds = tileMap[i][j][layer][k]->getGlobalBounds();
				sf::FloatRect nextPositionBounds = currentEntity->getNextPositionGlobalBounds(deltaTime);
				sf::Vector2f prev = currentEntity->getPosition();

				tileMap[i][j][layer][k]->updateTile();
				if (tileMap[i][j][layer][k]->intersects(nextPositionBounds) &&
					tileMap[i][j][layer][k]->getCollisionProperty()) {
					// Collision in jos cand entitatea vine de sus
					if (playerBounds.top < tileBounds.top
						&& playerBounds.top + playerBounds.height < tileBounds.top + tileBounds.height
						&& playerBounds.left < tileBounds.left + tileBounds.width
						&& playerBounds.left + playerBounds.width > tileBounds.left
						)
					{
						currentEntity->setVelocity(0.f, 0.f);
						currentEntity->setPosition(prev.x, prev.y - 5);
					}

					// Collision in sus cand entitatea vine de jos
					else if (playerBounds.top > tileBounds.top
						&& playerBounds.top + playerBounds.height > tileBounds.top + tileBounds.height
						&& playerBounds.left < tileBounds.left + tileBounds.width
						&& playerBounds.left + playerBounds.width > tileBounds.left
						) {
						currentEntity->setVelocity(0.f, 0.f);
						currentEntity->setPosition(prev.x, prev.y + 5);
					}
					// Collision la stanga, cand o entitate vine din dreapta tile-ului
					// Verific daca este mai in "stanga" decat tile-ul curent si daca este la acelasi nivel
					// Adica sa nu fie nici mai jos si nici mai sus
					// Iar la stanga verific sa nu fie in stanga tile-ului intreg ci doar sa incerce sa se
					// intersecteze cu acesta	
					// La restul verificarilor o sa fie aceeasi logica
					else if (playerBounds.left < tileBounds.left &&
						playerBounds.left + playerBounds.width < tileBounds.left + tileBounds.width &&
						playerBounds.top < tileBounds.top + tileBounds.height &&
						playerBounds.top + playerBounds.height > tileBounds.top) {
						currentEntity->setVelocity(0.f, 0.f);
						currentEntity->setPosition(prev.x - 5, prev.y);
					} else if (playerBounds.left > tileBounds.left &&
						playerBounds.left + playerBounds.width > tileBounds.left + tileBounds.width &&
						playerBounds.top < tileBounds.top + tileBounds.height &&
						playerBounds.top + playerBounds.height > tileBounds.top) {
						currentEntity->setVelocity(0.f, 0.f);
						currentEntity->setPosition(prev.x + 5, prev.y);
						// La restul verificarilor o sa fie aceeasi logica
					}
				}
			}
		}
	}
}

void TileMap::updateTiles(Entity* currentEntity, const float& deltaTime, EnemySystem& enemySystem) {
	sf::Vector2f maxSize = getMaxSizeGrid();
	layer = 0;

	fromX = currentEntity->getGridPosition(gridDimension).x - 1;
	if (fromX < 0.f) {
		fromX = 0.f;
	}
	else if (fromX > maxSize.x) {
		fromX = maxSize.x;
	}
	toX = currentEntity->getGridPosition(gridDimension).x + 3;
	if (toX < 0.f) {
		toX = 0.f;
	}
	else if (toX > maxSize.x) {
		toX = maxSize.x;
	}
	fromY = currentEntity->getGridPosition(gridDimension).y - 1;
	if (fromY < 0.f) {
		fromY = 0.f;
	}
	else if (fromY > maxSize.y) {
		fromY = maxSize.y;
	}
	toY = currentEntity->getGridPosition(gridDimension).y + 3;
	if (toY < 0.f) {
		toY = 0.f;
	}
	else if (toY > maxSize.y) {
		toY = maxSize.y;
	}
	for (int i = fromX; i < toX; ++i) {
		for (int j = fromY; j < toY; ++j) {
			for (int k = 0; k < tileMap[i][j][layer].size(); ++k) {
				tileMap[i][j][layer][k]->updateTile();
				if (tileMap[i][j][layer][k]->getTileType() == TILE_TYPE::SPAWNER) {
					SpawnerTile* spawner = dynamic_cast<SpawnerTile*>(tileMap[i][j][layer][k]);
					if (spawner) {
						if(spawner->getSpawnerTimer() && spawner->getEnemyCounter() < spawner->getEnemyAmount()) {

							enemySystem.createEnemy(spawner->getSpawnerType(), i * gridDimension, j * gridDimension, *spawner);
						}
					}
				}
			}
		}
	}
}

void TileMap::updateTileMap() {
}

void TileMap::renderTileMap(sf::RenderTarget& currentWindow, const sf::Vector2i& gridPosition,
		sf::Shader* shader, const sf::Vector2f playerPosition, const bool collision) {

	// Tile culling
	sf::Vector2f maxSize = getMaxSizeGrid();
	layer = 0;

	fromX = gridPosition.x - 1;
	if (fromX < 0.f) {
		fromX = 0.f;
	} else if (fromX > maxSize.x) {
		fromX = maxSize.x;
	}
	toX = gridPosition.x + 3;
	if (toX < 0.f) {
		toX = 0.f;
	} else if (toX > maxSize.x) {
		toX = maxSize.x;
	}
	fromY = gridPosition.y - 1;
	if (fromY < 0.f) {
		fromY = 0.f;
	} else if (fromY > maxSize.y) {
		fromY = maxSize.y;
	}
	toY = gridPosition.y + 3;
	if (toY < 0.f) {
		toY = 0.f;
	} else if (toY > maxSize.y) {
		toY = maxSize.y;
	}
	for (int i = fromX; i < toX; ++i) {
		for (int j = fromY; j < toY; ++j) {
			for (int k = 0; k < tileMap[i][j][layer].size(); ++k) {
				if (tileMap[i][j][layer][k]->getCollisionProperty()) {
					tileMap[i][j][layer][k]->renderTile(currentWindow);
					collisionBox.setFillColor(sf::Color::Transparent);
					collisionBox.setOutlineColor(sf::Color::Transparent);
					collisionBox.setOutlineThickness(-1.f);
					collisionBox.setPosition(tileMap[i][j][layer][k]->getTilePosition());
					currentWindow.draw(collisionBox);
				}
			}
		}
	}
}

void TileMap::renderDeferred(sf::RenderTarget& target, sf::Shader* shader, const sf::Vector2f playerPosition) {
	while (!defferedStack.empty()) 	{
		if (shader) {
			defferedStack.top()->renderTile(target, shader, playerPosition);
		} else {
			defferedStack.top()->renderTile(target);
		}
		defferedStack.pop();
	}
}

const sf::Vector2f& TileMap::getMaxSize() const {
	return maxSizeWorld;
}

const sf::Vector2f& TileMap::getMaxSizeGrid() const {
	return maxSizeGrid;
}

TileMap& TileMap::operator=(const TileMap& other) {
	if (this != &other) {
		gridDimension = other.gridDimension;
		numberOfLayers = other.numberOfLayers;
		maxSizeGrid = other.maxSizeGrid;
		maxSizeWorld = other.maxSizeWorld;
		textureFile = other.textureFile;
		tileMap = other.tileMap;
		tileTextureSheet = other.tileTextureSheet;
		collisionBox = other.collisionBox;
		fromX = other.fromX;
		toX = other.toX;
		fromY = other.fromY;
		toY = other.toY;
		layer = other.layer;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const TileMap& tileMap) {
	out << "Grid Dimension: " << tileMap.gridDimension << "\n";
	out << "Number of Layers: " << tileMap.numberOfLayers << "\n";
	out << "Max Size Grid: (" << tileMap.maxSizeGrid.x << ", " << tileMap.maxSizeGrid.y << ")\n";
	out << "Max Size World: (" << tileMap.maxSizeWorld.x << ", " << tileMap.maxSizeWorld.y << ")\n";
	return out;
}
