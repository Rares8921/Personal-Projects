#pragma once

#include "SFML\Graphics.hpp"
#include <iostream> // std::string
#include <vector>
#include <map>

enum TAGTYPES { 
	DEFAULT_TAG = 0,
	NEGATIVE_TAG, 
	EXPERIENCE_TAG
};


class TextTags {
private:
	class TextTag {
	private:
		sf::Text currentText;
		float directionX, directionY;
		float speed, acceleration;
		float time;
		sf::Vector2f velocity;
		int fade;
		bool isReversed;
	public:
		TextTag(sf::Font& textTagFont, std::string stringText, float x, float y, float directionX, float directionY,
			sf::Color textColor, int characterSize, float time, bool isReversed,
			float speed, float acceleration, int fade)
			: directionX(directionX), directionY(directionY), time(time), isReversed(isReversed),
			speed(speed), acceleration(acceleration), fade(fade) {

			currentText.setFont(textTagFont);
			currentText.setString(stringText);
			currentText.setFillColor(textColor);
			currentText.setCharacterSize(characterSize);
			currentText.setPosition(x, y);

			if (isReversed) {
				velocity = sf::Vector2f(directionX * speed, directionY * speed);
			}

		}

		// Pentru push-uri cu mai putin cod de scris
		TextTag(TextTag* textTag, float x, float y, std::string stringText) 
			: directionX(textTag->directionX), directionY(textTag->directionY), time(textTag->time), 
			isReversed(textTag->isReversed), speed(textTag->speed), acceleration(textTag->acceleration), 
			fade(textTag->fade), velocity(textTag->velocity) {

			currentText = textTag->currentText;
			currentText.setString(stringText);
			currentText.setPosition(x, y);

		}

		~TextTag() {}

		inline const bool isFinished() {
			return time <= 0.f;
		}

		void updateTextTag(const float &deltaTime) {
			if (time > 0.f) {
				time -= 100.f * deltaTime;
				// Daca exista acceleratie, verific daca pot creste velocity-ul obiectului
				if (acceleration > 0.f) {
					if (isReversed) {
						velocity.x = std::max(0.f, velocity.x - directionX * acceleration * deltaTime);
						velocity.y = std::max(0.f, velocity.y - directionY * acceleration * deltaTime);
					} else {
						velocity.x = std::min(directionX * speed, velocity.x + directionX * acceleration * deltaTime);
						velocity.y = std::min(directionY * speed, velocity.y + directionY * acceleration * deltaTime);
					}
					currentText.move(velocity * deltaTime);
				// Altfel obiectul se va muta cu viteza pe care o are deja
				} else {
					currentText.move(directionX * acceleration * deltaTime, directionY * acceleration * deltaTime);
				}
				// In functie de fade-ul pe care il am la momentul curent, reduc opacitatea textului
				if (fade > 0 && currentText.getFillColor().a >= fade) {
					currentText.setFillColor(sf::Color(
						currentText.getFillColor().r, currentText.getFillColor().g, currentText.getFillColor().b,
						currentText.getFillColor().a - fade));
				}
			}
		}

		void renderTextTag(sf::RenderTarget& currentWindow) {
			currentWindow.draw(currentText);
		}
		TextTag& operator=(const TextTag& other) {
			return *this;
		}
		friend std::ostream& operator<<(std::ostream& out, const TextTag& textTags) { return out;}
	};
	sf::Font textTagsFont;
	std::map<int, TextTag*> mapTags;
	std::vector<TextTag*> textTags;

	void initFont(std::string pathToFont);
	void initMapTags();
public:
	TextTags(std::string pathToFont);
	virtual ~TextTags();

	void addTextTag(const int tagType, const float x, const float y,
		const int i, const std::string prefix = "", const std::string postfix = "");

	void updateTextTags(const float &deltaTime);
	void renderTextTags(sf::RenderTarget& currentWindow);
	TextTags& operator=(const TextTags& other);
	friend std::ostream& operator<<(std::ostream& out, const TextTags& textTags);
};

