#include "EnemySystem.h"

EnemySystem::EnemySystem(std::map<std::string, sf::Texture>& enemyTextures, std::vector<Enemy*>& activeEnemies,
	Entity& playerEntity) : enemyTextures(enemyTextures), activeEnemies(activeEnemies), playerEntity(playerEntity) {
	
}

EnemySystem::~EnemySystem() {
	while (activeEnemies.size() > 0) {
		delete activeEnemies[activeEnemies.size() - 1];
		activeEnemies.erase(activeEnemies.end() - 1);
	}
}

void EnemySystem::createEnemy(const int type, const float x, const float y, SpawnerTile& enemySpawnerTile) {
    switch (type) {
        case Zone2Enemy1:
            activeEnemies.push_back(new Rat(x, y, enemyTextures["RAT1_SHEET"], enemySpawnerTile, playerEntity));
            enemySpawnerTile.increaseCounter();
            break;
        case Zone2Enemy2:
            activeEnemies.push_back(new Rat(x, y, enemyTextures["RAT2_SHEET"], enemySpawnerTile, playerEntity));
            enemySpawnerTile.increaseCounter();
            break;
        case Zone2Enemy3:
            activeEnemies.push_back(new Rat(x, y, enemyTextures["RAT3_SHEET"], enemySpawnerTile, playerEntity));
            enemySpawnerTile.increaseCounter();
            break;
        case Zone2Enemy4:
            activeEnemies.push_back(new Rat(x, y, enemyTextures["RAT4_SHEET"], enemySpawnerTile, playerEntity));
            enemySpawnerTile.increaseCounter();
            break;
        case Zone3Enemy1:
            break;
        case Zone3Enemy2:
            break;
        case Zone3Enemy3:
            break;
        case Zone3Enemy4:
            break;
        case Zone3MiniBoss:
            break;
        case Zone3Boss:
            break;
        case Zone4Enemy1:
            break;
        case Zone4Enemy2:
            break;
        case Zone4Enemy3:
            break;
        case Zone4Enemy4:
            break;
        case Zone4MiniBoss:
            break;
        case Zone4Boss:
            break;
        case Zone5Enemy1:
            break;
        case Zone5Enemy2:
            break;
        case Zone5Enemy3:
            break;
        case Zone5Enemy4:
            break;
        case Zone5MiniBoss:
            break;
        case Zone5Boss:
            break;
        case Zone6Enemy1:
            break;
        case Zone6Enemy2:
            break;
        case Zone6Enemy3:
            break;
        case Zone6Enemy4:
            break;
        case Zone6MiniBoss:
            break;
        case Zone6Boss:
            break;
        case Zone7Enemy1:
            break;
        case Zone7Enemy2:
            break;
        case Zone7Enemy3:
            break;
        case Zone7Enemy4:
            break;
        case Zone7MiniBoss:
            break;
        case Zone7Boss:
            break;
        default:
            std::cout << "Tipul specificat de enemy nu exista!\n";
            break;
    }
}

void EnemySystem::removeEnemy(const int index) {

	delete activeEnemies[index];
	activeEnemies.erase(activeEnemies.begin() + index);
}

EnemySystem& EnemySystem::operator=(const EnemySystem& other) {
    if (this != &other) {
        playerEntity = other.playerEntity;
        enemyTextures = other.enemyTextures;
        for (Enemy* enemy : activeEnemies) {
            delete enemy;
        }
        activeEnemies.clear();

        for (Enemy* enemy : other.activeEnemies) {
            activeEnemies.push_back(enemy);
        }
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const EnemySystem& enemySystem) {
    out << "Player Entity: " << &enemySystem.playerEntity << "\n";
    out << "Enemy Textures:\n";
    for (const auto& pair : enemySystem.enemyTextures) {
        out << "  " << pair.first << ": " << &pair.second << "\n";
    }
    out << "Active Enemies:\n";
    for (const auto& enemy : enemySystem.activeEnemies) {
        out << "  " << enemy << "\n";
    }
    return out;
}
