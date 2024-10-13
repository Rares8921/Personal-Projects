#pragma once

#include "GeneralFunctions.h"
#include "GUI.h"

class Controls : public GeneralFunctions {
private:
	sf::RenderWindow* controlsWindow; // Window-ul va fi folosit dinamic, de aceea e declarat ca pointer.
	sf::Texture backgroundTexture, menuTexture, titleTexture;
	sf::Sprite backgroundSprite, menuSprite, titleSprite;
	sf::Event windowEvent;
	sf::Cursor handCursor;
	sf::Text moveLeftText, moveRightText, moveUpText, moveDownText, closeText, pauseText, tabText, inventoryText, jumpText;
	sf::Font icelandFont;
	GUI::Button* moveLeftButton, *moveRightButton, *moveUpButton, *moveDownButton, *jumpButton, *closeButton, *pauseButton, *tabButton, *inventoryButton;
	sf::Keyboard::Key lastKeyPressed;
	std::map<std::string, int> supportedKeys;
	// Initialiazare
	void saveToFile();
	void initWindow();
	void initKeys();
	void initButtons();
	void initContent();	
	void updateEvents();
	void updateWindow();
	void renderWindow();
public:
	Controls();
	virtual ~Controls();
	void runControlsMenu();
	Controls& operator=(const Controls& other);
	friend std::ostream& operator<<(std::ostream& out, const Controls& controls);
};

