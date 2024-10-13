#pragma once
#include "CharacterTab.h"

enum PLAYER_TABS {
	CHARACTER_TAB = 0,
	INVENTORY_TAB
};

class PlayerGUITab {
private:
	std::vector<Tab*> tabs;
	sf::VideoMode& pGuiVideoMode;
	sf::Font& pGuifont;
	Player& pGuiPlayer;

	sf::Clock keyTimer;
	float keyTimeMax;
	void initTabs();
	void initKeyTime();

public:
	PlayerGUITab(sf::VideoMode& vm, sf::Font& font, Player& player);
	virtual ~PlayerGUITab();

	const bool& getKeyTime();
	const bool& isTabOpen();

	void toggleTab(const int tab_index);

	void updateTab();
	void renderTab(sf::RenderTarget& currentWindow);

	PlayerGUITab& operator=(const PlayerGUITab& other);
	friend std::ostream& operator<<(std::ostream& out, const PlayerGUITab& playerGUITab);
};

