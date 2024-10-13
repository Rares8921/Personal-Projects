#include "PlayerGUITab.h"

void PlayerGUITab::initTabs() {
	tabs.push_back(new CharacterTab(pGuiVideoMode, pGuifont, pGuiPlayer));
}

void PlayerGUITab::initKeyTime() {
	keyTimeMax = 0.3f;
	keyTimer.restart();
}

PlayerGUITab::PlayerGUITab(sf::VideoMode& pGuiVideoMode, sf::Font& pGuifont, Player& pGuiPlayer)
	: pGuiVideoMode(pGuiVideoMode), pGuifont(pGuifont), pGuiPlayer(pGuiPlayer) {
	initTabs();
	initKeyTime();
}

PlayerGUITab::~PlayerGUITab() {}

const bool& PlayerGUITab::getKeyTime() {
	if (keyTimer.getElapsedTime().asSeconds() >= keyTimeMax) {
		keyTimer.restart();
		return true;
	}
	return false;
}

const bool& PlayerGUITab::isTabOpen() {
	// Verific daca este deja deschis un tab
	bool open = false;
	for (Tab* tab : tabs) {
		if (tab->isOpen()) {
			open = true;
			break;
		}
	}
	return open;
}

void PlayerGUITab::toggleTab(const int tab_index) {
	if (tab_index >= 0 && tab_index < tabs.size()) {
		tabs[tab_index]->toggleTab();
	}
}

void PlayerGUITab::updateTab() {
	for (Tab *tab : tabs) {
		if (tab->isOpen()) {
			tab->updateTab();
		}
	}
}

void PlayerGUITab::renderTab(sf::RenderTarget& currentWindow) {
	for (Tab* tab : tabs) {
		if (tab->isOpen()) {
			tab->renderTab(currentWindow);
		}
	}
}

PlayerGUITab& PlayerGUITab::operator=(const PlayerGUITab& other) {
	if (this != &other) {
		tabs = other.tabs;
		pGuiVideoMode = other.pGuiVideoMode;
		pGuiPlayer = other.pGuiPlayer;
		keyTimer = other.keyTimer;
		keyTimeMax = other.keyTimeMax;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const PlayerGUITab& playerGUITab) {
	out << "Number of Tabs: " << playerGUITab.tabs.size() << "\n";
	out << "PGUI Video Mode: " << playerGUITab.pGuiVideoMode.width << "x" << playerGUITab.pGuiVideoMode.height << "\n";
	out << "Key Timer: " << playerGUITab.keyTimer.getElapsedTime().asSeconds() << "\n";
	out << "Key Time Max: " << playerGUITab.keyTimeMax << "\n";
	return out;
}
