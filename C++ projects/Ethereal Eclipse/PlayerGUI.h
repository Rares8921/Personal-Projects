#pragma once

#include "GUI.h";
#include "Player.h";
#include "Tabs/PlayerGUITab.h"

class PlayerGUI {
private:
	Player* pgPlayer;

	sf::VideoMode& pgVideoMode;
	sf::Font pgFont;

	//Level bar
	std::string levelBarString;
	sf::Text levelBarText;
	sf::RectangleShape levelBarFullShape;

	//EXP Bar
	GUI::ProgressBar* expBar;

	//HP Bar
	GUI::ProgressBar* hpBar;

	//Player GUI Tabs
	PlayerGUITab* playerTab;

	void initFont();
	void initLevelBar();
	void initEXPBar();
	void initHPBar();
	void initPlayerTab();

public:
	PlayerGUI(Player* pgPlayer, sf::VideoMode& pgVideoMode);
	virtual ~PlayerGUI();

	//Accessor
	const bool& tabOpen() const;
	void toggleCharacterTab();

	//Functions
	void updateLevelBar();
	void updateEXPBar();
	void updateHPBar();
	void updatePlayerTab();

	void update(const float& deltaTime);

	void renderLevelBar(sf::RenderTarget& currentWindow);
	void renderEXPBar(sf::RenderTarget& currentWindow);
	void renderHPBar(sf::RenderTarget& currentWindow);
	void renderPlayerTab(sf::RenderTarget& currentWindow);

	void render(sf::RenderTarget& currentWindow);
	PlayerGUI& operator=(const PlayerGUI& other);
	friend std::ostream& operator<<(std::ostream& out, const PlayerGUI& playerGUI);
};

