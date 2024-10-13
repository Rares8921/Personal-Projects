#include "PlayerGUI.h"

void PlayerGUI::initFont() {
	pgFont.loadFromFile("Fonts/Pixel/Pixel.ttf");
}

void PlayerGUI::initLevelBar()
{
	float width = pgVideoMode.width * (1.6f / 100.f);
	float height = pgVideoMode.height * (2.8f / 100.f);
	float x = pgVideoMode.width * (1.f / 100.f);
	float y = pgVideoMode.height * (1.9f / 100.f);

	levelBarFullShape.setSize(sf::Vector2f(width * 1.5, height * 1.5));
	levelBarFullShape.setFillColor(sf::Color(50, 50, 50, 200));
	levelBarFullShape.setPosition(x, y);

	levelBarText.setFont(pgFont);
	levelBarText.setCharacterSize((pgVideoMode.width + pgVideoMode.height) / 80.f);
	levelBarText.setPosition(
		levelBarFullShape.getPosition().x + pgVideoMode.width * (0.7f / 100.f),
		levelBarFullShape.getPosition().y + pgVideoMode.height * (0.15f / 100.f)
	);
}

void PlayerGUI::initEXPBar() {
	expBar = new GUI::ProgressBar(1.f, 6.9f, 10.4f, 5.9f, sf::Color::Color(141, 168, 152, 255), 80, pgVideoMode, &pgFont);
}

void PlayerGUI::initHPBar() {
	hpBar = new GUI::ProgressBar(1.f, 13.3f, 10.4f, 5.8f, sf::Color::Red, 76, pgVideoMode, &pgFont);
}

void PlayerGUI::initPlayerTab() {
	playerTab = new PlayerGUITab(pgVideoMode, pgFont, *pgPlayer);
}

PlayerGUI::PlayerGUI(Player* pgPlayer, sf::VideoMode& pgVideoMode)
	: pgVideoMode(pgVideoMode), pgPlayer(pgPlayer) {
	initFont();
	initLevelBar();
	initEXPBar();
	initHPBar();
	initPlayerTab();
}

PlayerGUI::~PlayerGUI()
{
	delete hpBar;
	delete expBar;
	delete playerTab;
}

const bool& PlayerGUI::tabOpen() const {
	return playerTab->isTabOpen();
}

void PlayerGUI::toggleCharacterTab()
{
	playerTab->toggleTab(PLAYER_TABS::CHARACTER_TAB);
}

//Functions
void PlayerGUI::updateLevelBar() {
	levelBarString = std::to_string(pgPlayer->getAttribute()->getCurrentLevel());
	levelBarText.setString(levelBarString);
}

void PlayerGUI::updateEXPBar() {
	expBar->updateProgressBar(pgPlayer->getAttribute()->getCurrentExp(), pgPlayer->getAttribute()->getMaxExp());
}

void PlayerGUI::updateHPBar() {
	std::cout << pgPlayer->getAttribute()->getCurrentHp() << "\n";
	hpBar->updateProgressBar(pgPlayer->getAttribute()->getCurrentHp(), pgPlayer->getAttribute()->getMaxHp());
}

void PlayerGUI::updatePlayerTab() {
	playerTab->updateTab();
}

void PlayerGUI::update(const float& deltaTime) {
	updateLevelBar();
	updateEXPBar();
	updateHPBar();
	updatePlayerTab();
}


void PlayerGUI::renderLevelBar(sf::RenderTarget& currentWindow) {
	currentWindow.draw(levelBarFullShape);
	currentWindow.draw(levelBarText);
}

void PlayerGUI::renderEXPBar(sf::RenderTarget& currentWindow) {
	expBar->renderProgressBar(currentWindow);
}

void PlayerGUI::renderHPBar(sf::RenderTarget& currentWindow) {
	hpBar->renderProgressBar(currentWindow);
}

void PlayerGUI::renderPlayerTab(sf::RenderTarget& currentWindow) {
	playerTab->renderTab(currentWindow);
}

void PlayerGUI::render(sf::RenderTarget& currentWindow) {
	renderLevelBar(currentWindow);
	renderEXPBar(currentWindow);
	renderHPBar(currentWindow);
	renderPlayerTab(currentWindow);
}

PlayerGUI& PlayerGUI::operator=(const PlayerGUI& other) {
	if (this != &other) {
		pgPlayer = other.pgPlayer;
		pgVideoMode = other.pgVideoMode;
		pgFont = other.pgFont;
		levelBarString = other.levelBarString;
		levelBarText = other.levelBarText;
		levelBarFullShape = other.levelBarFullShape;
		if (other.expBar != nullptr) {
			expBar = new GUI::ProgressBar(*other.expBar);
		} else {
			expBar = nullptr;
		}
		if (other.hpBar != nullptr) {
			hpBar = new GUI::ProgressBar(*other.hpBar);
		}  else {
			hpBar = nullptr;
		}
		if (other.playerTab != nullptr) {
			playerTab = new PlayerGUITab(*other.playerTab);
		} else {
			playerTab = nullptr;
		}
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const PlayerGUI& playerGUI) {
	return out;
}
