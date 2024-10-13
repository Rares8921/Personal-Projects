#include "CharacterTab.h"

void CharacterTab::initText()
{
	//Text
	infoText.setFont(tabFont);
	infoText.setCharacterSize((videoMode.width + videoMode.height) / 80.f);
	infoText.setFillColor(sf::Color(200, 200, 200, 240));
	infoText.setPosition(fullTab.getPosition().x + 15.f, fullTab.getPosition().y + 15.f);

	infoText.setString(playerEntity.toStringCharacterTab());
}

CharacterTab::CharacterTab(sf::VideoMode& videoMode, sf::Font& font, Player& player)
	: Tab(videoMode, font, player, false)
{
	//Background
	fullTab.setFillColor(sf::Color(26, 26, 26, 120));
	fullTab.setSize(sf::Vector2f((videoMode.width * (45.f / 140.f)), videoMode.height * (45.f / 145.f)));
	fullTab.setPosition(675.f - fullTab.getSize().x / 2.f, 0);
	initText();
}

CharacterTab::~CharacterTab() {}

void CharacterTab::updateTab() {
	if (!hidden) {
		// Actualizez informatiile despre player
		infoText.setString(playerEntity.toStringCharacterTab());
	}
}

void CharacterTab::renderTab(sf::RenderTarget& target) {
	if (!hidden) {
		target.draw(fullTab);
		target.draw(infoText);
	}
}

CharacterTab& CharacterTab::operator=(const CharacterTab& other) {
	if (this != &other) {
		fullTab = other.fullTab;
		infoText = other.infoText;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const CharacterTab& charTab) {
	out << "Full Tab: " << charTab.fullTab.getPosition().x << ", " << charTab.fullTab.getPosition().y << " - "
		<< charTab.fullTab.getSize().x << "x" << charTab.fullTab.getSize().y << "\n";
	out << "Info Text: " << charTab.infoText.getString().toAnsiString() << "\n";
	return out;
}
