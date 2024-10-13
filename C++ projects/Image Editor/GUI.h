#pragma once

#include "Includes.h"
#include <string>
#include <vector>
#include <memory> // unique_ptr

const enum menuPosition {
	CENTER = 0,
	TOP_CENTER,
	BOTTOM_CENTER,
	LEFT,
	TOP_LEFT,
	BOTTOM_LEFT,
	RIGHT,
	TOP_RIGHT,
	BOTTOM_RIGHT,
	NONE,
};

const enum textPosition {
	TEXT_CENTER = 0,
	TEXT_LEFT,
	TEXT_RIGHT,
	TEXT_NONE
};

namespace GUI {

	class Button {
	protected:
		Rectangle buttonRect, shadowRect, borderRect;
		std::string buttonText;
		Font buttonFont;
		int fontSize, fontSpacing;
		double shadowOffset;
		bool isShadowVisible;
		Color shadowColor;
		Color textColor, backgroundColor, borderColor;
		Color hoverTextColor, hoverBackgroundColor, hoverBorderColor;
		Color activeTextColor, activeBackgroundColor, activeBorderColor;
		Color textColorToDraw, backgroundColorToDraw, borderColorToDraw;
		int borderWidth;
		bool isBorderVisible;
		float borderThickness;
		float x, y;
		int width, height;
		Vector2 textSize, textPosition;
		int buttonStatus;
	public:
		Button() = default;
		Button(std::string buttonText, float x, float y, int width, int height);
		virtual ~Button();
		void setShadowOffset(double shadowOffset);
		void setShadowColor(Color shadowColor);
		void setShadow(bool isShadowVisible);
		const bool& getShadow() const;
		void setText(std::string buttonText);
		const Vector2& getTextSize() const;
		void setTextPosition(Vector2 textPosition);
		const Vector2& getTextPosition() const;
		void setX(float x);
		const int& getX() const;
		void setY(float y);
		const int& getY() const;
		void setWidth(int width);
		const int& getWidth() const;
		void setHeight(int height);
		const int& getHeight() const;
		void setBackground(Color backgroundColor);
		void setHoverBackground(Color hoverBackgroundColor);
		void setActiveBackground(Color activeBackgroundColor);
		void setForeground(Color textColor);
		void setHoverForeground(Color hoverTextColor);
		void setActiveForeground(Color activeTextColor);
		void setBorder(bool isBorderVisible);
		void setBorderColor(Color borderColor);
		void setBorderHoverColor(Color hoverBorderColor);
		void setBorderActiveColor(Color activeBorderColor);
		void setBorderWidth(int borderWidth);
		void setFont(Font buttonFont);
		void setFontSize(int fontSize);
		void setFontSpacing(int fontSpacing);
		int updateButton();
		virtual int drawButton(); // Returneaza statusul 0 daca butonul a fost apasat
	};

	class RoundedButton : public Button {
	private:
		float radius;
	public:
		RoundedButton() = default;
		RoundedButton(std::string buttonText, float x, float y, int width, int height);
		void setRadius(float _radius);
		int drawButton() override;
	};

	class ColorPicker {
	private:

	public:
	};

	class Menu {
	private:
		float menuWidth, menuHeight;
		float x, y;
		unsigned menuPosition;
		// Menu Bar
		int bars;
		std::vector<Button> menuButtons;
		float barSpacing, height, width;
		// Texte
		std::vector<std::string> menuTexts;
		unsigned textsPosition;
		float textMargin;
		Font menuFont;
		int menuFontSize;
		bool isRaylibFont, isTextMod;
		void initMenu();
	public:
		Menu() = default;
		Menu(const Menu& _menu) = delete;
		virtual ~Menu();
		void setMenuWidth(float _menuWidth);
		void setMenuHeight(float _menuHeight);
		void setMenuX(float _x);
		void setMenuY(float _y);
		void setMenuTexts(std::vector<std::string> _menuTexts);
		void setMenuPosition(unsigned _menuPosition);
		void setBarSpacing(float _barSpacing);
		void setBarsColor(std::vector<Color> barsColors);
		void setBarsHoverColor(std::vector<Color> barsHoverColors);
		void setBarsActiveColor(std::vector<Color> barsActiveColors);
		void setBarsSize(float _width, float _height);
		void setShadow(bool _isShadowVisible);
		void setShadowColor(Color shadowColor);
		void addToShadowSize(float _width, float _height);
		void setBorder(bool _isBorderVisible);
		void setBordersColor(std::vector<Color> bordersColor);
		void setBordersHoverColor(std::vector<Color> bordersHoverColors);
		void setBordersActiveColor(std::vector<Color> bordersActiveColors);
		void setBordersThickness(float borderThickness);
		void setFont(Font _menuFont);
		void setFontSize(int _menuFontSize);
		void setTextBar(std::string text, int barIndex);
		void setTextPosition(unsigned _textPosition);
		void setTextsColor(std::vector<Color> textsColor);
		virtual void buildMenu();
		virtual void drawMenu();
	};
}

