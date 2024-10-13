#pragma once

#include "GeneralFunctions.h"

class GameRun : public GeneralFunctions {
private:
	sf::RenderWindow *gameWindow;
	sf::Texture gameMapTexture, zoneTexture, innacessibleZoneTexture;
	sf::Sprite gameMapSprite, zoneOneSprite, zoneTwoSprite, zoneThreeSprite, zoneFourSprite, zoneFiveSprite, zoneSixSprite,
		zoneSevenSprite, zoneEightSprite;
	sf::Event gameWindowEvent;
	// Initializare
	void initWindow();
	void initContent();
public:
	GameRun();
	virtual ~GameRun();
	// Update
	void updateEvents();
	void updateWindow();
	// Render
	void renderWindow();
	// Draw
	void runGameMenu();
	GameRun& operator=(const GameRun& other);
	friend std::ostream& operator<<(std::ostream& out, const GameRun& gameRun);
};