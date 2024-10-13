#include "GUI.h"

const float GUI::p2pX(const float perc, const sf::VideoMode& videoMode) {
	//Convert la valoarea procentuala relativ la pixelii rezolutiei curente pe axa x
	return std::floor(videoMode.width * (perc / 100.f));
}

const float GUI::p2pY(const float perc, const sf::VideoMode& videoMode) {
	// Convert la valoarea procentuala relativ la pixelii rezolutiei curente pe axa y
	return std::floor(videoMode.height * (perc / 100.f));
}

GUI::Slider& GUI::Slider::operator=(const GUI::Slider& other) {
	if (this != &other) {
		// Copiați membrii clasici
		sliderKnob = other.sliderKnob;
		sliderAxis = other.sliderAxis;
		sliderFont = other.sliderFont;
		sliderText = other.sliderText;
		minValue = other.minValue;
		maxValue = other.maxValue;
		x = other.x;
		y = other.y;
		axisWidth = other.axisWidth;
		axisHeight = other.axisHeight;
		sliderWidth = other.sliderWidth;
		sliderHeight = other.sliderHeight;
		sliderValue = other.sliderValue;
		isFocused = other.isFocused;
	}
	return *this;
}


std::ostream& GUI::operator<<(std::ostream& out, const Slider& slider) {
	out << "Min Value: " << slider.minValue << "\n";
	out << "Max Value: " << slider.maxValue << "\n";
	return out;
}

GUI::Button& GUI::Button::operator=(const Button& other) {
	if (this != &other) {
		buttonState = other.buttonState;
		buttonID = other.buttonID;
		buttonKeyCode = other.buttonKeyCode;
		buttonShape = other.buttonShape;
		buttonFont = other.buttonFont;
		buttonText = other.buttonText;
		textIdleColor = other.textIdleColor;
		textHoverColor = other.textHoverColor;
		textActiveColor = other.textActiveColor;
		idleColor = other.idleColor;
		hoverColor = other.hoverColor;
		activeColor = other.activeColor;
		borderIdleColor = other.borderIdleColor;
		borderHoverColor = other.borderHoverColor;
		borderActiveColor = other.borderActiveColor;
		hasFocus = other.hasFocus;
		width = other.width;
		height = other.height;
		buttonString = other.buttonString;
	}
	return *this;
}

std::ostream& GUI::operator<<(std::ostream& out, const Button& button) {
	out << "Button State: " << button.buttonState << "\n";
	out << "Button ID: " << button.buttonID << "\n";
	return out;
}

GUI::ProgressBar& GUI::ProgressBar::operator=(const GUI::ProgressBar& other) {
	if (this != &other) {
		progressBarString = other.progressBarString;
		progressBarText = other.progressBarText;
		maxWidth = other.maxWidth;
		fullProgressShape = other.fullProgressShape;
		currentProgressShape = other.currentProgressShape;
	}
	return *this;
}

std::ostream& GUI::operator<<(std::ostream& out, const ProgressBar& progressBar) {
	out << "Progress Bar String: " << progressBar.progressBarString << "\n";
	return out;
}

GUI::Slider::Slider(int x, int y) : x(x), y(y) {
	isFocused = false;
	
	sliderValue = 0.f;
	minValue = maxValue = 0;
	axisWidth = 200;
	axisHeight = 10;
	sliderWidth = 20;
	sliderHeight = 30;
	
	if (!sliderFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
		std::cout << "Font-ul Iceland nu a putut fi incarcat in Slider!";
	}
	sliderText.setFont(sliderFont);
	sliderText.setFillColor(sf::Color::White);
	sliderText.setCharacterSize(20);

	sliderAxis.setPosition(x, y);
	sliderAxis.setOrigin(0, axisHeight / 2.f);
	sliderAxis.setSize(sf::Vector2f(axisWidth, axisHeight));
	sliderAxis.setFillColor(sf::Color(167, 167, 155));

	sliderKnob.setRadius((sliderWidth + sliderHeight) / 5.25f);
	sliderKnob.setPosition(x, y);
	sliderKnob.setOrigin(sliderWidth / 2.65f, sliderHeight / 2.65f);
	sliderKnob.setFillColor(sf::Color(205, 205, 205));
}

sf::Text GUI::Slider::returnText(int x, int y, const std::string text, int fontSize)
{
	sliderText.setPosition(x, y);
	sliderText.setString(text);
	sliderText.setCharacterSize(fontSize);
	return sliderText;
}

void GUI::Slider::userInteraction(sf::RenderWindow& window) {
	if (sliderKnob.getGlobalBounds().contains(sf::Mouse::getPosition(window).x, sf::Mouse::getPosition(window).y) || isFocused) {
		// Setez cursor-ul
		if (sf::Mouse::isButtonPressed(sf::Mouse::Button::Left)) {
			isFocused = true;
			sf::Cursor handCursor;
			if (!handCursor.loadFromSystem(sf::Cursor::Hand)) {
				std::cout << "Cursorul Hand nu a putut fi incarcat din sistem!";
			}
			window.setMouseCursor(handCursor);
			// slider.getPosition().x - pozitia mouse-ului pe slider in raport cu window
			// x - pozitia punctului ce contine valoarea minima in raport cu window

			sliderKnob.setPosition(sf::Mouse::getPosition(window).x, y);
			float pozitieSlider = (sliderKnob.getPosition().x - x); // pozitia in raport cu slider-ul
			float complementara = pozitieSlider / axisWidth; // complementara valorii
			float zeroBasedValue = complementara * (maxValue - minValue); // valoarea efectiva pentru cand minValue = 0
			sliderValue = minValue + zeroBasedValue; // Valoarea finala

			// Acum verific ca valoarea sa se fi pastrat in interval
			if (sliderValue < minValue) {
				sliderKnob.setPosition(x, y);
				sliderValue = 0;
			} else if (sliderValue > maxValue) {
				sliderValue = maxValue;
				sliderKnob.setPosition(x + axisWidth, y);
			}
		} else {
			isFocused = false;
			sf::Cursor arrowCursor;
			if (!arrowCursor.loadFromSystem(sf::Cursor::Arrow)) {
				std::cout << "Cursorul Arrow nu a putut fi incarcat din sistem!";
			}
			window.setMouseCursor(arrowCursor);
		}
	}
}

void GUI::Slider::create(int minimum, int maximum) {
	sliderValue = minimum;
	minValue = minimum;
	maxValue = maximum;
}

float GUI::Slider::getSliderValue() {
	return sliderValue;
}

int GUI::Slider::getX() {
	return x;
}

int GUI::Slider::getY() {
	return y;
}

int GUI::Slider::getMinValue() {
	return minValue;
}

void GUI::Slider::setSliderValue(float value) {
	// Verific daca value este valid
	if(value >= minValue && value <= maxValue) {
		sliderValue = value;
		// Lungimea totala a slider-ului
		float diff1 = maxValue - minValue;
		// Asemanator cu sumele partiale, gasesc unde este value pe segment
		float diff2 = value - minValue;
		// Plec la de la cea mai din stanga pozitie
		float xPosition = x + diff2 * (axisWidth / diff1);
		sliderKnob.setPosition(xPosition, y);
	}
}

void GUI::Slider::renderSlide(sf::RenderWindow& window) {
	userInteraction(window);

	// Pentru ca nu se creeaza nicio copie a font-ului, trebuie mereu incarcat aici
	if (!sliderFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
		std::cout << "Font-ul Iceland nu a putut fi incarcat in Slider!";
	}
	sliderText.setFont(sliderFont);
	// Textul valorii minime
	window.draw(returnText(x - 10, y + 5, std::to_string(minValue), 20));
	window.draw(sliderAxis);
	// Textul valorii maxime
	window.draw(returnText(x + axisWidth - 10, y + 5, std::to_string(maxValue), 20));
	window.draw(sliderKnob);
	// Textul valorii curente a slider-ului fix pe pozitia unde apare
	window.draw(returnText(sliderKnob.getPosition().x - sliderWidth, sliderKnob.getPosition().y - sliderHeight,
		std::to_string((int)sliderValue), 17.5));
}

GUI::ProgressBar::ProgressBar(float _x, float _y, float _width, float _height, sf::Color innerColor, int characterSize, sf::VideoMode& videoMode, sf::Font* progressBarFont) {
	
	float width = GUI::p2pX(_width, videoMode);
	float height = GUI::p2pY(_height, videoMode);
	float x = GUI::p2pX(_x, videoMode);
	float y = GUI::p2pY(_y, videoMode);

	maxWidth = width;
	fullProgressShape.setSize(sf::Vector2f(width, height));
	fullProgressShape.setFillColor(sf::Color(50, 50, 50, 200));
	fullProgressShape.setPosition(x, y);

	currentProgressShape.setSize(sf::Vector2f(width, height));
	currentProgressShape.setFillColor(innerColor);
	currentProgressShape.setPosition(fullProgressShape.getPosition());
	if (progressBarFont) {
		progressBarText.setFont(*progressBarFont);
		progressBarText.setCharacterSize((videoMode.width + videoMode.height) / characterSize);
		progressBarText.setPosition(
			currentProgressShape.getPosition().x + GUI::p2pX(0.53f, videoMode),
			currentProgressShape.getPosition().y + GUI::p2pY(0.5f, videoMode)
		);
	}
}

GUI::ProgressBar::~ProgressBar() {}

void GUI::ProgressBar::updateProgressBar(const int currentValue, const int maxValue) {
	currentProgressShape.setSize(sf::Vector2f(currentValue, currentProgressShape.getSize().y));
	progressBarString = std::to_string(currentValue) + "/" + std::to_string(maxValue);
	progressBarText.setString(progressBarString);
}

void GUI::ProgressBar::renderProgressBar(sf::RenderTarget& window) {
	window.draw(fullProgressShape);
	window.draw(currentProgressShape);
	window.draw(progressBarText);
}

GUI::Button::Button(float x, float y, float width, float height,
	sf::Font buttonFont, std::string text, int characterSize
	, sf::Color textIdleColor, sf::Color textHoverColor, sf::Color textActiveColor,
	sf::Color idleColor, sf::Color hoverColor, sf::Color activeColor,
	sf::Color borderIdleColor, sf::Color borderHoverColor, sf::Color borderActiveColor, int keyCode, int buttonID)
	: buttonFont(buttonFont), 
	  textIdleColor(textIdleColor), textHoverColor(textHoverColor), textActiveColor(textActiveColor),
	  idleColor(idleColor), hoverColor(hoverColor), activeColor(activeColor), 
	  borderIdleColor(borderIdleColor), borderHoverColor(borderHoverColor), borderActiveColor(borderActiveColor), 
	buttonID(buttonID), width(width), height(height), buttonString(text) {
	
	hasFocus = false;
	buttonState = B_IDLE;

	buttonKeyCode = sf::Keyboard::Key(keyCode);
	buttonShape.setPosition(sf::Vector2f(x, y));
	buttonShape.setSize(sf::Vector2f(width, height));
	buttonShape.setFillColor(idleColor);
	buttonShape.setOutlineThickness(1.f);
	buttonShape.setOutlineColor(borderIdleColor);

	buttonText.setFont(buttonFont);
	buttonText.setString(text);
	buttonText.setCharacterSize(characterSize);
	buttonText.setPosition(
		buttonShape.getPosition().x + (buttonShape.getGlobalBounds().width / 2.f) - buttonText.getGlobalBounds().width / 1.9f,
		buttonShape.getPosition().y
	);
}

GUI::Button::~Button() { }

const bool& GUI::Button::isPressed() const {
	if (buttonState) {
		return buttonState == B_ACTIVE;
	}

	return false;
}

const std::string& GUI::Button::getText() const {
	return buttonString;
}

void GUI::Button::setText(const std::string newText) {
	buttonString = newText;
	buttonText.setString(newText);
	buttonText.setPosition(
		buttonShape.getPosition().x + (buttonShape.getGlobalBounds().width / 2.f) - buttonText.getGlobalBounds().width / 1.9f,
		buttonShape.getPosition().y
	);
}

const int& GUI::Button::getId() const {
	return buttonID;
}

const sf::Keyboard::Key& GUI::Button::getButtonKeyCode() const {
	return buttonKeyCode;
}

void GUI::Button::setId(const int newID) {
	buttonID = newID;
}

void GUI::Button::updateButton(sf::Vector2i mousePosition, sf::Keyboard::Key keyCode) {
	buttonState = B_IDLE;
	if (hasFocus) {
		buttonState = B_ACTIVE;
	} else if (buttonShape.getGlobalBounds().contains(static_cast<sf::Vector2f>(mousePosition))) {
		buttonState = B_HOVER;
		if (sf::Mouse::isButtonPressed(sf::Mouse::Left)) {
			buttonState = B_ACTIVE;
		}
	}
	switch (buttonState) {
		case B_IDLE:
			buttonShape.setFillColor(idleColor);
			buttonText.setFillColor(textIdleColor);
			buttonText.setStyle(sf::Text::Regular);
			buttonShape.setOutlineColor(borderIdleColor);
			break;
		case B_HOVER:
			buttonShape.setFillColor(hoverColor);
			buttonText.setFillColor(textHoverColor);
			buttonText.setStyle(sf::Text::Regular);
			buttonShape.setOutlineColor(borderHoverColor);
			break;
		case B_ACTIVE:
			if (!hasFocus) {
				hasFocus = true;
			}
			buttonShape.setFillColor(activeColor);
			buttonText.setFillColor(textActiveColor);
			buttonShape.setOutlineColor(borderActiveColor);
			buttonText.setStyle(sf::Text::Underlined);
			if (sf::Keyboard::isKeyPressed(sf::Keyboard::Enter) ||
				(sf::Mouse::isButtonPressed(sf::Mouse::Left) && !buttonShape.getGlobalBounds().contains(static_cast<sf::Vector2f>(mousePosition)))) {
				hasFocus = false;
			} else {
				if (keyCode != -1) {
					setText(getKeyStringFromCode(keyCode));
					buttonKeyCode = keyCode;
				}
				if (buttonText.getString() == "False") {
					setText("True");
					hasFocus = false;
				} else if(buttonText.getString() == "True") {
					setText("False");
					hasFocus = false;
				}
			}
			break;
		default:
			break;
	}
}

void GUI::Button::renderButton(sf::RenderTarget& window, sf::Keyboard::Key keyCode) {

	if (!buttonFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
		std::cout << "Font-ul Iceland nu a putut fi incarcat in meniul Controls!";
	}
	buttonText.setFont(buttonFont);
	/*sf::Vector2i pozitieInAplicatie = sf::Mouse::getPosition(window);
	if (keyCode != -1) {
		updateButton(pozitieInAplicatie, keyCode);
	} else {
		updateButton(pozitieInAplicatie);
	}*/
	window.draw(buttonShape);
	window.draw(buttonText);
}
