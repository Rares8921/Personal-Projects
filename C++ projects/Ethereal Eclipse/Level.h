#pragma once

#include "GeneralFunctions.h"
#include "States/GameState.h"

class Level : public GeneralFunctions, GameState {
private:
	sf::View* mapView;
	sf::RenderWindow* levelWindow;
	sf::Texture gameMapTexture, level1ButtonTexture;
	sf::Sprite gameMapSprite, level1ButtonSprite;
	sf::Event levelWindowEvent;
	// Folosesc o stiva pentru a mentine state-urile jocului
	// De exemplu: state-ul de fight
	std::stack<State*> stackState;
	// Pentru keybinds
	std::map<std::string, int> supportedKeys;
	// Am nevoie de delta time pentru a stii cat timp este necesar pentru a incarca un frame
	// Voi folosi delta time atunci cand va trebui o entitate sa se miste.
	sf::Clock deltaTimeClock;
	float deltaTime;
	sf::Vector2f appSize;
	sf::Vector2f mapSize;
	int level;
	std::string zoneTitle;
	// Initializare
	void initWindow();
	void initContent();
	void initStates();
	void initKeys();
public:
	Level(int level, std::string zoneTitle);
	virtual ~Level();
	// Update
	void updateEvents();
	void updateWindow();
	// Render
	void renderWindow();
	// Draw
	void runLevel();

	Level& operator=(const Level& other);
	friend std::ostream& operator<<(std::ostream& out, const Level& level);
};