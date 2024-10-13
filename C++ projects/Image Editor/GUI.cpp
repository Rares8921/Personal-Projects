#include "GUI.h"
#include <iostream>

GUI::Button::Button(std::string buttonText, float x, float y, int width, int height)
	: buttonText(buttonText), x(x), y(y), width(width), height(height) {

	buttonStatus = -1;
	buttonRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	borderRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	shadowRect = Rectangle{ x + 5, y + 5, 1.f * width, 1.f * height };
	shadowOffset = 0.05;
	shadowColor = DARKGRAY;
	isShadowVisible = false;

	buttonFont = GetFontDefault();
	fontSize = 16;
	fontSpacing = 3.f;
	textSize = MeasureTextEx(buttonFont, buttonText.c_str(), fontSize, fontSpacing);
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };

	textColorToDraw = textColor = hoverTextColor = activeTextColor = BLACK;
	backgroundColorToDraw = backgroundColor = hoverBackgroundColor = activeBackgroundColor = WHITE;
	borderColorToDraw = borderColor = hoverBorderColor = activeBorderColor = GRAY;
	borderWidth = 1;
	borderThickness = (width + height) / 150;
	isBorderVisible = true;
	
}

GUI::Button::~Button() {}

void GUI::Button::setShadowOffset(double _shadowOffset) {
	shadowOffset = _shadowOffset;
}

void GUI::Button::setShadowColor(Color _shadowColor) {
	shadowColor = _shadowColor;
}

void GUI::Button::setShadow(bool _isShadowVisible) {
	isShadowVisible = _isShadowVisible;
}

const bool& GUI::Button::getShadow() const {
	return isShadowVisible;
}

void GUI::Button::setText(std::string _buttonText) {
	buttonText = _buttonText;
}

const Vector2& GUI::Button::getTextSize() const {
	return textSize;
}

void GUI::Button::setTextPosition(Vector2 _textPosition) {
	textPosition = _textPosition;
}

const Vector2& GUI::Button::getTextPosition() const {
	return textPosition;
}

void GUI::Button::setX(float _x) {
	x = _x;
	buttonRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	borderRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	shadowRect = Rectangle{ x + 5, y + 5, 1.f * width, 1.f * height };
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };
}

const int& GUI::Button::getX() const {
	return x;
}

void GUI::Button::setY(float _y) {
	y = _y;
	buttonRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	borderRect = Rectangle{ x, y, 1.f * width, 1.f * height };
	shadowRect = Rectangle{ x + 5, y + 5, 1.f * width, 1.f * height };
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };
}

const int& GUI::Button::getY() const {
	return y;
}

void GUI::Button::setWidth(int _width) {
	width = _width;
}

const int& GUI::Button::getWidth() const {
	return width;
}

void GUI::Button::setHeight(int _height) {
	height = _height;
}

const int& GUI::Button::getHeight() const {
	return height;
}

void GUI::Button::setBackground(Color _backgroundColor) {
	backgroundColor = _backgroundColor;
}

void GUI::Button::setHoverBackground(Color _hoverBackgroundColor) {
	hoverBackgroundColor = _hoverBackgroundColor;
}

void GUI::Button::setActiveBackground(Color _activeBackgroundColor) {
	activeBackgroundColor = _activeBackgroundColor;
}

void GUI::Button::setForeground(Color _textColor) {
	textColor = _textColor;
}

void GUI::Button::setHoverForeground(Color _hoverTextColor) {
	hoverTextColor = _hoverTextColor;
}

void GUI::Button::setActiveForeground(Color _activeTextColor) {
	activeTextColor = _activeTextColor;
}

void GUI::Button::setBorder(bool _isBorderVisible) {
	isBorderVisible = _isBorderVisible;
}

void GUI::Button::setBorderColor(Color _borderColor) {
	borderColor = _borderColor;
}

void GUI::Button::setBorderHoverColor(Color _hoverBorderColor) {
	hoverBorderColor = _hoverBorderColor;
}

void GUI::Button::setBorderActiveColor(Color _activeBorderColor) {
	activeBorderColor = _activeBorderColor;
}

void GUI::Button::setBorderWidth(int _borderWidth) {
	borderWidth = _borderWidth;
}

void GUI::Button::setFont(Font _buttonFont) {
	buttonFont = _buttonFont;
	textSize = MeasureTextEx(buttonFont, buttonText.c_str(), fontSize, fontSpacing);
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };
}

void GUI::Button::setFontSize(int _fontSize) {
	fontSize = _fontSize;
	textSize = MeasureTextEx(buttonFont, buttonText.c_str(), fontSize, fontSpacing);
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };
}

void GUI::Button::setFontSpacing(int _fontSpacing) {
	fontSpacing = _fontSpacing;
	textSize = MeasureTextEx(buttonFont, buttonText.c_str(), fontSize, fontSpacing);
	textPosition = { x + (width - textSize.x) / 2, y + (height - textSize.y) / 2 };
}

int GUI::Button::updateButton() {
	int result = -1;
	Vector2 mousePosition = GetMousePosition();
	if (CheckCollisionPointRec(mousePosition, buttonRect)) {
		SetMouseCursor(MOUSE_CURSOR_POINTING_HAND);
		if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
			textColorToDraw = activeTextColor;
			backgroundColorToDraw = activeBackgroundColor;
			borderColorToDraw = activeBorderColor;
			result = MOUSE_BUTTON_LEFT;
		} else {
			textColorToDraw = hoverTextColor;
			backgroundColorToDraw = hoverBackgroundColor;
			borderColorToDraw = hoverBorderColor;
		}
	} else {
		textColorToDraw = textColor;
		backgroundColorToDraw = backgroundColor;
		borderColorToDraw = borderColor;
		SetMouseCursor(MOUSE_CURSOR_DEFAULT);
	}
	return result;
}

int GUI::Button::drawButton() {
	int result = updateButton();
	if (isShadowVisible) {
		DrawRectangleRec(shadowRect, shadowColor);
	}
	DrawRectangleRec(buttonRect, backgroundColorToDraw);
	if (isBorderVisible) {
		DrawRectangleLinesEx(borderRect, borderThickness, borderColorToDraw);
	}
	DrawTextEx(buttonFont, buttonText.c_str(), textPosition, fontSize, fontSpacing, textColorToDraw);
	if (result == buttonStatus) {
		return -1;
	}
	return buttonStatus = result;
}

GUI::RoundedButton::RoundedButton(std::string buttonText, float x, float y, int width, int height) 
	: Button(buttonText, x, y, width, height) {
	radius = 0.5;
}

void GUI::RoundedButton::setRadius(float _radius) {
	radius = _radius;
}

int GUI::RoundedButton::drawButton() {
	int result = updateButton();
	if (getShadow()) {
		DrawRectangleRounded(shadowRect, radius, 4, shadowColor);
	}
	DrawRectangleRounded(buttonRect, radius, 1, backgroundColorToDraw);
	if (isBorderVisible) {
		DrawRectangleRoundedLines(borderRect, radius, 4, borderThickness, borderColorToDraw);
	}
	DrawTextEx(buttonFont, buttonText.c_str(), textPosition, fontSize, fontSpacing, textColorToDraw);
	if (result == buttonStatus) {
		return -1;
	}
	return buttonStatus = result;
}

void GUI::Menu::initMenu() {

}
/*
GUI::Menu::Menu() {
	for (int i = 0; i < 6; ++i) {
		menuTexts.push_back("Dummy");
	}
	menuPosition = CENTER;
	isTextMod = false;
	isRaylibFont = true;
	//menuFont = LoadFont("Consolas.ttf");
	menuFontSize = 35;
	//buildMenu();
	for (Button& button : menuButtons) {
		button.setBorderWidth(2.f);
	}
}
*/

GUI::Menu::~Menu()
{
}

void GUI::Menu::drawMenu() {
	for (Button& button : menuButtons) {
		button.drawButton();
	}
}

void GUI::Menu::setMenuWidth(float _menuWidth) {
	menuWidth = _menuWidth;
}

void GUI::Menu::setMenuHeight(float _menuHeight) {
	menuHeight = _menuHeight;
}

void GUI::Menu::setMenuTexts(std::vector<std::string> _menuTexts) {
	menuTexts = _menuTexts;
}

void GUI::Menu::setMenuX(float _x) {
	x = _x;
}

void GUI::Menu::setMenuY(float _y) {
	y = _y;
}

void GUI::Menu::setMenuPosition(unsigned _menuPosition) {
	menuPosition = _menuPosition;
}

void GUI::Menu::setTextPosition(unsigned _textPosition) {
	textsPosition = _textPosition;
}

void GUI::Menu::setTextsColor(std::vector<Color> textsColor) {
	int i = 0;
	for(Button& button : menuButtons) {
		button.setForeground(textsColor[i++]);
	}
}

void GUI::Menu::setFont(Font _menuFont) {
	menuFont = _menuFont;
}

void GUI::Menu::setFontSize(int _menuFontSize) {
	menuFontSize = _menuFontSize;
}

void GUI::Menu::setTextBar(std::string text, int barIndex) {
	menuTexts[barIndex] = text;
}

void GUI::Menu::setBarSpacing(float _barSpacing) {
	barSpacing = _barSpacing;
}

void GUI::Menu::setBarsColor(std::vector<Color> barsColors) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBackground(barsColors[i++]);
	}
}

void GUI::Menu::setBarsHoverColor(std::vector<Color> barsHoverColors) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setHoverBackground(barsHoverColors[i++]);
	}
}

void GUI::Menu::setBarsActiveColor(std::vector<Color> barsActiveColors) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setActiveBackground(barsActiveColors[i++]);
	}
}

void GUI::Menu::setBarsSize(float _width, float _height) {
	width = _width;
	height = _height;
}

void GUI::Menu::setShadow(bool _isShadowVisible) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setShadow(_isShadowVisible);
	}
}

void GUI::Menu::setShadowColor(Color shadowColor) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setShadowColor(shadowColor);
	}
}

void GUI::Menu::addToShadowSize(float _width, float _height) {}

void GUI::Menu::setBorder(bool _isBorderVisible) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBorder(_isBorderVisible);
	}
}

void GUI::Menu::setBordersColor(std::vector<Color> bordersColor) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBorderColor(bordersColor[i++]);
	}
}

void GUI::Menu::setBordersHoverColor(std::vector<Color> bordersHoverColors) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBorderHoverColor(bordersHoverColors[i++]);
	}
}

void GUI::Menu::setBordersActiveColor(std::vector<Color> bordersActiveColors) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBorderActiveColor(bordersActiveColors[i++]);
	}
}

void GUI::Menu::setBordersThickness(float borderThickness) {
	int i = 0;
	for (Button& button : menuButtons) {
		button.setBorderWidth(borderThickness);
	}
}

void GUI::Menu::buildMenu() {
	// Numarul de meniuri va fi determinat pe baza numarul de texte existene
	bars = menuTexts.size();
	if (!isTextMod) {
		menuButtons.clear();
		// Incarc pe font-ul default
		if (isRaylibFont) {
			Button menuButton; 
			menuButton.setFont(GetFontDefault());
			for (int i = 0; i < bars; ++i) {
				menuButtons.push_back(menuButton);
			}
			for (int i = 0; i < bars; ++i) {
				menuButtons[i].setText(menuTexts[i]);
			}
		// Sau incarc un font deja instalat
		} else {
			const char* buttonText = "Empty";
			Button menuButton;
			menuButton.setText(buttonText);
			menuButton.setFont(GetFontDefault());
			menuButton.setFontSize(menuFontSize);
			for (int i = 0; i < bars; ++i) {
				menuButtons.push_back(menuButton);
			}
			for (int i = 0; i < bars; ++i) {
				menuButtons[i].setText(menuTexts[i]);
			}
		}
	}
	// Pentru fiecare meniu calculez width-ul height-ul si size-ul
	for (Button& button : menuButtons) {
		width = (button.getWidth() > width ? button.getWidth() + 50 : width);
	}
	menuWidth = width;
	menuHeight = bars * menuButtons[0].getHeight() + barSpacing * (bars - 1);
	//-- sets bars size
	for (Button& button : menuButtons) {
		button.setWidth(width);
	}
	int screenWidth = GetScreenWidth(), screenHeight = GetScreenHeight();
	float screenMargin = 50.0f;
	try {
		// Calculez pozitia butonului in functie de tipul specificat
		switch (menuPosition) {
		case CENTER:
			x = (screenWidth / 2.f) - (menuWidth / 2.f);
			y = (screenHeight / 2.f) - (menuHeight / 2.f);
			break;
		case TOP_CENTER:
			x = (screenWidth / 2.f) - (menuWidth / 2.f);
			y = screenMargin;
			break;
		case BOTTOM_CENTER:
			x = (screenWidth / 2.f) - (menuWidth / 2.f);
			y = (1.f * screenHeight - menuHeight - screenMargin);
			break;
		case LEFT:
			x = 1.f * screenMargin;
			y = (screenHeight / 2.f) - (menuHeight / 2.f);
			break;
		case  TOP_LEFT:
			x = 1.f * screenMargin;
			y = 1.f * screenMargin;
			break;
		case BOTTOM_LEFT:
			x = 1.f * screenMargin;
			y = (1.f * screenHeight - menuHeight - screenMargin);
			break;
		case RIGHT:
			x = (screenWidth - menuWidth - 2.f * screenMargin);
			y = (screenHeight / 2.f) - (menuHeight / 2.f);
			break;
		case TOP_RIGHT:
			x = (screenWidth - menuWidth - 2.f * screenMargin);
			y = 1.f * screenMargin;
			break;
		case BOTTOM_RIGHT:
			x = (screenWidth - menuWidth - 2.f * screenMargin);
			y = (1.f * screenHeight - menuHeight - screenMargin);
			break;
		case NONE:
			// The menu was repositioned using setMenuPosition()
			break;
		default:
			std::cout << "Meniul nu a primit o pozitie valida!\n";
			break;
		}
	} catch (char const* ERROR) {
		std::cout << "Meniul nu a putut fi incarcat:\n";
		throw ERROR;
	}

	// Setez pozitia meniurilor
	float tempBarY = y;
	for (Button& button : menuButtons) {
		button.setX(x);
		button.setY(tempBarY);
		tempBarY += button.getHeight() + barSpacing;
	}

	// In final setez si pozitiile textelor
	for (Button& button : menuButtons) {
		try {
			switch (textsPosition) {
			case TEXT_CENTER:
				// The std::shared_ptr<Button> class centers the texts by default
				break;
			case TEXT_LEFT:
				button.setTextPosition({ button.getX() + textMargin, button.getTextPosition().y });
				break;
			case TEXT_RIGHT:
				button.setTextPosition({button.getX() - textMargin + (button.getWidth() - button.getTextSize().x), button.getTextPosition().y});
				break;
			case TEXT_NONE:
				// The texts were repositioned using setTextPos()
				break;
			default:
				std::cout << "Textul meniului nu a primit o pozitie valida!\n";
				break;
			}
		} catch (char const* ERROR) {
			std::cout << "Textul meniului nu a putut fi incarcat:\n";
			throw ERROR;
		}
	}
}
