#include "PauseMenu.h"

PauseMenu::PauseMenu(sf::VideoMode& videoMode, sf::Font& font)
	: menuFont(font) {

	//Init background
	menuBackground.setSize(sf::Vector2f(videoMode.width, videoMode.height));
	menuBackground.setFillColor(sf::Color(20, 20, 20, 100));

	//Init container
	menuContainer.setSize(sf::Vector2f(videoMode.width / 4.f, videoMode.height - GUI::p2pY(9.3f, videoMode)));
	menuContainer.setFillColor(sf::Color(20, 20, 20, 200));
	menuContainer.setPosition(videoMode.width / 2.f - menuContainer.getSize().x / 2.f, 30.f );

	//Init text
	menuText.setFont(font);
	menuText.setFillColor(sf::Color(255, 255, 255, 200));
	menuText.setCharacterSize((videoMode.width + videoMode.height) / 60.f);
	menuText.setString("PAUSED");
	menuText.setPosition(
		menuContainer.getPosition().x + menuContainer.getSize().x / 2.f - menuText.getGlobalBounds().width / 2.f,
		menuContainer.getPosition().y + GUI::p2pY(4.f, videoMode)
	);
}

PauseMenu::~PauseMenu() {
	for (auto it : menuButtons) {
		delete it.second;
	}
}

std::map<std::string, GUI::Button*>& PauseMenu::getButtons() {
	return menuButtons;
}

//Functions
const bool PauseMenu::isButtonPressed(const std::string key) {
	if (menuButtons[key]) {
		return menuButtons[key]->isPressed();
	}
	return false;
}

void PauseMenu::addButton( const std::string key, const float y, const float width, const float height,
	const unsigned char_size, const std::string text) {
	float x = menuContainer.getPosition().x + menuContainer.getSize().x / 2.f - width / 2.f;

	menuButtons[key] = new GUI::Button( x, y, width, height,
		menuFont, text, char_size,
		sf::Color(70, 70, 70, 200), sf::Color(250, 250, 250, 250), sf::Color(20, 20, 20, 50),
		sf::Color(70, 70, 70, 0), sf::Color(150, 150, 150, 0), sf::Color(20, 20, 20, 0),
		sf::Color(70, 70, 70, 0), sf::Color(150, 150, 150, 0), sf::Color(20, 20, 20, 0)
	);
}

void PauseMenu::updatePauseMenu(const sf::Vector2i& mousePosition) {
	for (auto& i : menuButtons) {
		if (i.second) {
			i.second->updateButton(mousePosition);
		}
	}
}

void PauseMenu::renderPauseMenu(sf::RenderTarget& currentWindow) {
	currentWindow.draw(menuBackground);
	currentWindow.draw(menuContainer);

	for (auto& i : menuButtons)
	{
		if (i.second) {
			i.second->renderButton(currentWindow);
		}
	}

	currentWindow.draw(menuText);
}

PauseMenu& PauseMenu::operator=(const PauseMenu& other) {
	if (this != &other) {
		this->menuFont = other.menuFont;
		this->menuText = other.menuText;
		this->menuBackground = other.menuBackground;
		this->menuContainer = other.menuContainer;
		for (const auto& pair : other.menuButtons) {
			this->menuButtons[pair.first] = new GUI::Button(*pair.second);
		}
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const PauseMenu& pauseMenu) {
	return out;
}
