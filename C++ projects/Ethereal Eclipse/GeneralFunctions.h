#pragma once

// CORE
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <array>


// SFML
#include "SFML\System.hpp"
#include "SFML\Window.hpp"
#include "SFML\Graphics.hpp"
#include "SFML\Audio.hpp"
#include "SFML\Network.hpp"

class GeneralFunctions {
private:
	float musicVolume;
	float gameVolume;
	bool vSync;
	float framerate;
	bool antiAliasing;
	std::array<int, 10> keyCodes;
	void readFromFiles();
public:
	GeneralFunctions();
	virtual ~GeneralFunctions();
	const bool& isSpriteClicked(sf::RenderWindow *currentWindow, sf::Sprite currentSprite, sf::Vector2f mousePosition) const;
	float updateDeltaTime(sf::Clock deltaTimeClock);
	const float& getMusicVolume() const;
	const float& getGameVolume() const;
	const bool& getVSync() const;
	const float& getFramerate() const;
	const bool& getAntiAliasing() const;
	const std::array<int, 10>& getKeyCodes() const;
	std::string getKeyStringFromCode(const sf::Keyboard::Key& k);
	GeneralFunctions& operator=(const GeneralFunctions& other);
	friend std::ostream& operator<<(std::ostream& out, const GeneralFunctions& generalFuncs);
};
