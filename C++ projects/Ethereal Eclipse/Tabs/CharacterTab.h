#pragma once

#include "Tab.h"

class CharacterTab : public Tab {
private:
	sf::RectangleShape fullTab;
	sf::Text infoText;

	void initText();

public:
	CharacterTab(sf::VideoMode& videoMode, sf::Font& font, Player& player);
	~CharacterTab();

	void updateTab();
	void renderTab(sf::RenderTarget& currentWindow);
	CharacterTab& operator=(const CharacterTab& other);
	friend std::ostream& operator<<(std::ostream& out, const CharacterTab& charTab);
};

