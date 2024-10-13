#pragma once

#include "Gui.h"

class PauseMenu {
private:
	sf::Font& menuFont;
	sf::Text menuText;

	sf::RectangleShape menuBackground;
	sf::RectangleShape menuContainer;

	std::map<std::string, GUI::Button*> menuButtons;

	//Private Functions

public:
	PauseMenu(sf::VideoMode& videoMode, sf::Font& font);
	virtual ~PauseMenu();

	//Accessor
	std::map<std::string, GUI::Button*>& getButtons();

	//Functions
	const bool isButtonPressed(const std::string key);
	void addButton(const std::string key, const float y, const float width, const float height,
		const unsigned char_size, const std::string text);
	void updatePauseMenu(const sf::Vector2i& mousePosition);
	void renderPauseMenu(sf::RenderTarget& currentWindow);
	PauseMenu& operator=(const PauseMenu& other);
	friend std::ostream& operator<<(std::ostream& out, const PauseMenu& pauseMenu);
};

