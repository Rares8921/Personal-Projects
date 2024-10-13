// Includ header-ul doar o data la o compilare
#pragma once

#include "GeneralFunctions.h"

class Game : public GeneralFunctions {
private:
	// Variabile smechere pt aplicatie
	sf::RenderWindow *applicationWindow; // Window-ul va fi folosit dinamic, de aceea e declarat ca pointer.
	sf::Texture backgroundTexture, gameTitleTexture, menuTexture, playButtonTexture, charactersButtonTexture, settingsButtonTexture, exitButtonTexture;
	sf::Sprite backgroundSprite, gameTitleSprite, menuSprite, playButtonSprite, charactersButtonSprite, settingButtonSprite, exitButtonSprite;
	sf::Event windowEvent;
	sf::Cursor handCursor;
	bool isHovered, isActive;
	// Initialiazare
	void initWindow();
	void initContent();
public:
	Game();
	virtual ~Game();
	void updateEvents();
	void updateWindow();
	void renderWindow() const;
	void runApplication();
	Game& operator=(const Game& other);
	friend std::ostream& operator<<(std::ostream& out, const Game& game);
};
