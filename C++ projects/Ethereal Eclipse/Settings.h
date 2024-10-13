#pragma once

#include "GeneralFunctions.h"
#include "GUI.h";

class Settings : public GeneralFunctions {
private:
	sf::RenderWindow* settingsWindow; // Window-ul va fi folosit dinamic, de aceea e declarat ca pointer.
	sf::Texture backgroundTexture, menuTexture, titleTexture;
	sf::Sprite backgroundSprite, menuSprite, titleSprite;
	sf::Event windowEvent;
	sf::Cursor handCursor;
	sf::Text musicVolumeText, gameVolumeText, vSyncText, frameRateText, antiAliasingText;
	sf::Font icelandFont;
	GUI::Slider musicVolumeSlider;
	GUI::Slider gameVolumeSlider;
	GUI::Slider frameRateSlider;
	GUI::Button* vSyncButton, *antiAliasingButton;
	// Initialiazare
	void initWindow();
	void initContent();
	void initButtons();
	void saveToFile();
public:
	Settings();
	virtual ~Settings();
	void updateEvents();
	void updateWindow();
	void renderWindow();
	void runSettingsMenu();
	Settings& operator=(const Settings& other);
	friend std::ostream& operator<<(std::ostream& out, const Settings& settings);
};

