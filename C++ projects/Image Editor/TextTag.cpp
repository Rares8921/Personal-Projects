#include "TextTag.h"
#include <iostream>

TextTag::TextTag() {
	x = y = 150;
	spacing = 20.f;
	radius = 1;
	borderSize = 0;
	textFont = GetFontDefault();
	fontSize = 12;
	textColor = WHITE;
	borderColor = BLANK;
	borderType = BORDER_TYPE::RECTANGULAR;
	borderLine = BORDER_LINE::LINE;
	shadowColor = BLANK;
	isBolded = isItalic = isUnderlined = false;
	textString = "";
}

void TextTag::drawTextImage(Image& image, Vector2 position) {
    Font italicFont = LoadFont("italicConsolas.ttf");
    Font boldFont = LoadFont("boldConsolas.ttf");
    if (isBolded && isItalic) {
        for (int i = 0; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                ImageDrawTextEx(&image, italicFont, textString.c_str(), { position.x + i, position.y + j }, fontSize, spacing, textColor);
            }
        }
    } else if (isBolded) {
        for (int i = 0; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                ImageDrawTextEx(&image, textFont, textString.c_str(), { position.x + i, position.y + j }, fontSize, spacing, textColor);
            }
        }
    } else if (isItalic) {
        ImageDrawTextEx(&image, italicFont, textString.c_str(), position, fontSize, spacing, textColor);
    }
    else {
        ImageDrawTextEx(&image, textFont, textString.c_str(), position, fontSize, spacing, textColor);
    }
}

void TextTag::setX(int _x) {
	x = _x;
}

void TextTag::setY(int _y) {
	y = _y;
}

void TextTag::setRadius(int _radius) {
	radius = _radius;
}

void TextTag::setBorderSize(int _borderSize) {
	borderSize = _borderSize;
}

void TextTag::setSpacing(float _spacing) {
	spacing = _spacing;
}

void TextTag::setTextFont(Font _textFont) {
	textFont = _textFont;
}

void TextTag::setFontSize(int _fontSize) {
	fontSize = _fontSize;
}

void TextTag::setTextColor(Color _textColor) {
	textColor = _textColor;
}

void TextTag::setBorderColor(Color _borderColor) {
	borderColor = _borderColor;
}

void TextTag::setBorderType(BORDER_TYPE _borderType) {
	borderType = _borderType;
}

void TextTag::setBorderLine(BORDER_LINE _borderLine) {
	borderLine = _borderLine;
}

void TextTag::setShadowColor(Color _shadowColor) {
	shadowColor = _shadowColor;
}

void TextTag::setBolded(bool _isBolded) {
	isBolded = _isBolded;
}

void TextTag::setItalic(bool _isItalic) {
	isItalic = _isItalic;
}

void TextTag::setUnderlined(bool _isUnderlined) {
	isUnderlined = _isUnderlined;
}

void TextTag::setText(std::string _textString) {
	textString = _textString;
}

const std::string& TextTag::getText() const {
	return textString;
}

void TextTag::drawTextTag(Image& image) {
    Vector2 position = { x, y };

    // Draw shadow if any
    if (shadowColor.a != 0) {
        Vector2 shadowPos = { position.x + 2, position.y + 2 };
        drawTextImage(image, shadowPos);
    }

    // Draw border if any
    if (borderColor.a != 0 && borderSize > 0 && borderType != NONEXISTENT && borderLine != EMPTY) {
        if (borderType == BORDER_TYPE::RECTANGULAR) {
            Vector2 textSize = MeasureTextEx(textFont, textString.c_str(), fontSize, spacing);
            Rectangle borderRect = { position.x - borderSize, position.y - borderSize, textSize.x + 2 * borderSize, textSize.y + 2 * borderSize };
            switch (borderLine) {
            case BORDER_LINE::LINE:
                ImageDrawRectangleLines(&image, borderRect, borderSize, borderColor);
                break;
            case BORDER_LINE::DOTTED:
                ImageDrawRectangleLines(&image, borderRect, borderSize, borderColor);
                break;
            default:
                break;
            }
        }
        else if (borderType == BORDER_TYPE::ROUNDED) {
            // Assuming circular borders are centered around the text
            int textWidth = MeasureText(textString.c_str(), fontSize);
            int textHeight = fontSize; // Approximate height of the text
            int centerX = x + textWidth / 2;
            int centerY = y + textHeight / 2;
            switch (borderLine) {
            case BORDER_LINE::LINE:
                ImageDrawCircleLines(&image, centerX, centerY, radius, borderColor);
                break;
            case BORDER_LINE::DOTTED:
                ImageDrawCircleLines(&image, centerX, centerY, radius, borderColor);
                break;
            default:
                break;
            }
        }
    }
    // Apply text styles (bold, italic, underline)
    if (isUnderlined) {
        // Raylib does not support underline directly, so we draw a line under the text
        drawTextImage(image, position);
        Vector2 textSize = MeasureTextEx(textFont, textString.c_str(), fontSize, spacing);
        Vector2 underlineStart = { position.x, position.y + textSize.y + 1 };
        Vector2 underlineEnd = { position.x + textSize.x, position.y + textSize.y + 1 };
        ImageDrawLine(&image, underlineStart.x, underlineStart.y, underlineEnd.x, underlineEnd.y, textColor);
    }
    else {
        drawTextImage(image, position);
    }
}