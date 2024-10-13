#pragma once

#include "Tiles/SpawnerTile.h"
#include "Enemy.h"
#include "Rat.h"

enum EnemyList {
	Zone2Enemy1 = 0,
	Zone2Enemy2,
	Zone2Enemy3,
	Zone2Enemy4,
	Zone3Enemy1,
	Zone3Enemy2,
	Zone3Enemy3,
	Zone3Enemy4,
	Zone3MiniBoss,
	Zone3Boss,
	Zone4Enemy1,
	Zone4Enemy2,
	Zone4Enemy3,
	Zone4Enemy4,
	Zone4MiniBoss,
	Zone4Boss,
	Zone5Enemy1,
	Zone5Enemy2,
	Zone5Enemy3,
	Zone5Enemy4,
	Zone5MiniBoss,
	Zone5Boss,
	Zone6Enemy1,
	Zone6Enemy2,
	Zone6Enemy3,
	Zone6Enemy4,
	Zone6MiniBoss,
	Zone6Boss,
	Zone7Enemy1,
	Zone7Enemy2,
	Zone7Enemy3,
	Zone7Enemy4,
	Zone7MiniBoss,
	Zone7Boss
};

class EnemySystem {
private:
	// Referentiere pentru a asigura declararea obiectelor
	Entity& playerEntity;
	std::map<std::string, sf::Texture>& enemyTextures;
	std::vector<Enemy*>& activeEnemies;
public:
	EnemySystem(std::map<std::string, sf::Texture>& enemyTextures, std::vector<Enemy*>& activeEnemies,
		Entity& playerEntity);
	virtual ~EnemySystem();
	void createEnemy(const int type, const float x, const float y, SpawnerTile& enemySpawnerTile);
	void removeEnemy(const int index);
	EnemySystem& operator=(const EnemySystem& other);
	friend std::ostream& operator<<(std::ostream& out, const EnemySystem& enemySystem);
};

