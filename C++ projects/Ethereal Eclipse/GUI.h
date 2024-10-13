#pragma once

#include "GeneralFunctions.h"
#include "SFML\Graphics.hpp"
#include <iostream>

enum button_state {
	B_IDLE = 0,
	B_HOVER,
	B_ACTIVE
};

namespace GUI {

	// Functii pentru calcularea pozitiei unui obiect pe harta pentru render
	const float p2pX(const float perc, const sf::VideoMode& videoMode);
	const float p2pY(const float perc, const sf::VideoMode& videoMode);

	class Slider {
	private:
		sf::CircleShape sliderKnob;
		sf::RectangleShape sliderAxis;
		sf::Font sliderFont;
		sf::Text sliderText;
		int minValue, maxValue;
		int x, y;
		int axisWidth, axisHeight;
		int sliderWidth, sliderHeight; // pentru knob
		float sliderValue;
		bool isFocused;
	public:
		Slider() = default;
		Slider(int x, int y);
		virtual ~Slider() {}
		sf::Text returnText(int x, int y, const std::string text, int fontSize);
		void userInteraction(sf::RenderWindow& window);
		void create(int minimum, int maximum);
		float getSliderValue();
		int getX();
		int getY();
		int getMinValue();
		void setSliderValue(float value);
		void renderSlide(sf::RenderWindow& window);
		Slider& operator=(const Slider& other);
		friend std::ostream& operator<<(std::ostream& out, const Slider& slider);
	};

	class Button : public GeneralFunctions {
	private:
        int buttonState, buttonID;
        sf::Keyboard::Key buttonKeyCode;
		sf::RectangleShape buttonShape;
		sf::Font buttonFont;
		sf::Text buttonText;
		sf::Color textIdleColor, textHoverColor, textActiveColor;
		sf::Color idleColor, hoverColor, activeColor;
		sf::Color borderIdleColor, borderHoverColor, borderActiveColor;
		bool hasFocus;
		int width, height;
		std::string buttonString;
	public:
		Button(float x, float y, float width, float height,
			   sf::Font buttonFont, std::string text, int characterSize,
			   sf::Color textIdleColor, sf::Color textHoverColor, sf::Color textActiveColor,
			   sf::Color idleColor, sf::Color hoverColor, sf::Color activeColor, 
			   sf::Color borderIdleColor, sf::Color borderHoverColor, sf::Color borderActiveColor,
			   int keyCode = -1, int buttonID = 0);

		virtual ~Button();

		const bool& isPressed() const;
		const std::string& getText() const;
		void setText(const std::string newText);
		const int& getId() const;
        const sf::Keyboard::Key& getButtonKeyCode() const;
		void setId(const int newID);
		void updateButton(const sf::Vector2i mousePosition, sf::Keyboard::Key keyCode = sf::Keyboard::Key(-1));
		void renderButton(sf::RenderTarget& target, sf::Keyboard::Key keyCode = sf::Keyboard::Key(-1));
		
		Button& operator=(const Button& other);
		friend std::ostream& operator<<(std::ostream& out, const Button& button);
	};

	class ProgressBar {
	private:
		std::string progressBarString;
		sf::Text progressBarText;
		float maxWidth;
		sf::RectangleShape fullProgressShape, currentProgressShape;
	public:
		ProgressBar(float _x, float _y, float _width, float _height,
			sf::Color innerColor, int characterSize,
			sf::VideoMode &videoMode, sf::Font* progressBarFont = NULL);
		virtual ~ProgressBar();
		void updateProgressBar(const int currentValue, const int maxValue);
		void renderProgressBar(sf::RenderTarget& window);
		ProgressBar& operator=(const ProgressBar& other);
		friend std::ostream& operator<<(std::ostream& out, const ProgressBar& progressBar);
	};

}

