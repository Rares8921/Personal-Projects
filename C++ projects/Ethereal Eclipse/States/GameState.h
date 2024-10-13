#pragma once

#include "State.h"
#include "../TextTags.h"
#include "../PauseMenu.h"
#include "../Player.h"
#include "../Tiles/TileMap.h"
#include "../PlayerGUI.h"

class GameState : public State, public GeneralFunctions {
private:
	// Cu RenderTexture generez un mipmap prin care dau speed-up la tot procesul de render si pentru LOD
	sf::RenderTexture renderTexture;
	sf::Sprite renderSprite;
	// Folosesc mapView pentru a seta camera player-ului
	sf::View mapView;
	PauseMenu* pauseMenu;
	Player* currentPlayer;
	PlayerGUI* playerStatus;
	TileMap* currentTileMap;
	sf::Shader currentShader;
	sf::Font currentFont;
	sf::Shader gameShader;

	std::vector<Enemy*> activeEnemies;
	EnemySystem* enemySystem;

	// Pentru momentele in care incerc sa accesez meniuri, sa nu se inchida si deschida la nesfarsitr=
	sf::Clock keyTimer;
	float keyTimeMax;
	
	sf::Vector2i viewGridPosition;

	TextTags* textTags;
	int gameLevel;
	
	sf::Sound musicSound, attackSound, playerDeathSound;

	void initDeferredRender();
	void initTextures();
	void initFonts();
	void initPlayer();
	void initPlayerStatus();
	void initShader();
	void initTileMap();
	void initView();
	void initKeyBinds();
	void initPauseMenu();
	void initKeyTime();
	void initEnemySystem();
	void initTextSystem();
	void initSounds();
public:
	GameState() = default;
	GameState(float gridSize,
		sf::RenderWindow* currentWindow, sf::VideoMode gameVideoMode,
		std::map<std::string, int>* supportedKeys, std::stack<State*>* stackOfStates, int level);
	virtual ~GameState();
	const bool getKeyTime();
	//void endState();
	//void updatePaused();
	const sf::View& getMapView() const;
	const sf::Vector2f& getPlayerCoords() const;
	void saveData();
	void updateInput(const float& deltaTime);
	void updatePlayerInput(const float& deltaTime);
	void updateState(const float& deltaTime);
	//void updateEntity(const float& deltaTime);
	void updatePlayerStatus(const float& deltaTime);
	void updatePauseMenuButtons();
	void updateView(const float& deltaTime);
	void updateTileMap(const float& deltaTime);
	void updatePlayer(const float& deltaTime);
	void updateCombatAndEnemies(const float& deltaTime);
	void updateCombat(Enemy* enemy, const int index, const float& deltaTime);
	void renderState(sf::RenderTarget* target);
	GameState& operator=(const GameState& other);
	friend std::ostream& operator<<(std::ostream& out, const GameState& gameState);
};

