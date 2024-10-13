#include "Tab.h"


Tab::Tab(sf::VideoMode& videoMode, sf::Font& tabFont, Player& playerEntity, bool hidden) 
	: videoMode(videoMode), tabFont(tabFont), playerEntity(playerEntity), hidden(hidden) {
}

Tab::~Tab() {}

const bool& Tab::isHidden() const {
	return hidden;
}

const bool& Tab::isOpen() const {
	return !hidden;
}

void Tab::toggleTab() {
	hidden = !hidden;
}

void Tab::hideTab() {
	hidden = true;
}

void Tab::showTab() {
	hidden = false;
}

Tab& Tab::operator=(const Tab& other) {
	if (this != &other) {
		videoMode = other.videoMode;
		tabFont = other.tabFont;
		playerEntity = other.playerEntity;
		hidden = other.hidden;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const Tab& tab) {
	out << "Video Mode: " << tab.videoMode.width << "x" << tab.videoMode.height << "\n";
	out << "Hidden: " << (tab.hidden ? "true" : "false") << "\n";
	return out;
}
