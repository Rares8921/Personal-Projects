#pragma once

#include "raylib.h"
#include <string>

enum BORDER_TYPE {
	NONEXISTENT = 0,
	RECTANGULAR,
	ROUNDED
};

enum BORDER_LINE {
	EMPTY = 0,
	LINE,
	DOTTED,
};

class TextTag {
private:
	int x, y;
	int radius;
	int borderSize;
	float spacing;
	Font textFont; // Default: Arial
	int fontSize; // Default: 12px
	Color textColor; // Default: White
	Color borderColor; // Default: blank;
	BORDER_TYPE borderType; // Defaut: rectangular
	BORDER_LINE borderLine;//Default: line;
	Color shadowColor; // Default: blank
	bool isBolded, isItalic, isUnderlined; // Default: false;
	std::string textString; // Va fi si numele tagului
	void drawTextImage(Image& image, Vector2 position);
public:
	TextTag();
	virtual ~TextTag()=default;
	void setX(int _x);
	void setY(int _y);
	void setRadius(int _radius);
	void setBorderSize(int _borderSize);
	void setSpacing(float _spacing);
	void setTextFont(Font _textFont);
	void setFontSize(int _fontSize);
	void setTextColor(Color _textColor);
	void setBorderColor(Color _borderColor);
	void setBorderType(BORDER_TYPE _borderType);
	void setBorderLine(BORDER_LINE _borderLine);
	void setShadowColor(Color _shadowColor);
	void setBolded(bool _isBolded);
	void setItalic(bool _isItalic);
	void setUnderlined(bool _isUnderlined);
	void setText(std::string _textString);
	const std::string& getText() const;
	void drawTextTag(Image& currentImage);
};

