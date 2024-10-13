#pragma once

#include "../Player.h"
#include "../Gui.h"


class Tab {
protected:
	sf::VideoMode& videoMode;
	sf::Font& tabFont;
	Player& playerEntity;
	bool hidden;
public:
	Tab(sf::VideoMode& videoMode, sf::Font& tabFont, Player& playerEntity, bool hidden);
	virtual ~Tab();

	//Accessor
	const bool& isHidden() const;
	const bool& isOpen() const;
	void toggleTab();

	//Functions
	void hideTab();
	void showTab();

	virtual void updateTab() = 0;
	virtual void renderTab(sf::RenderTarget& currentWindow) = 0;
	Tab& operator=(const Tab& other);
	friend std::ostream& operator<<(std::ostream& out, const Tab& tab);
};

