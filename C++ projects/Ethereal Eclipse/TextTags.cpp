#include "TextTags.h"
#include <sstream>

void TextTags::initFont(std::string fontFile) {
	if (!textTagsFont.loadFromFile(fontFile))
		std::cout << "Font-ul de la " << fontFile << " nu a putut fi incarcat pentru TextTags!\n";
}

void TextTags::initMapTags() {
	mapTags[DEFAULT_TAG] = new TextTag(textTagsFont, "", 100.f, 100.f, 0.f, -1.f, sf::Color::White, 16, 100.f, true, 200.f, 200.f, 2);
	mapTags[NEGATIVE_TAG] = new TextTag(textTagsFont, "", 100.f, 100.f, 0.f, 1.f, sf::Color::Red, 16, 100.f, true, 200.f, 200.f, 1);
	mapTags[EXPERIENCE_TAG] = new TextTag(textTagsFont, "", 100.f, 100.f, 0.f, -1.f, sf::Color::Cyan, 24, 200.f, true, 200.f, 200.f, 2);
}

TextTags::TextTags(std::string fontFile) {
	initFont(fontFile);
	initMapTags();
}

TextTags::~TextTags() {
	for (auto* tag : textTags) {
		delete tag;
	}
	for (auto& tag : mapTags) {
		delete tag.second;
	}
}

void TextTags::addTextTag(const int tagType, const float x, const float y,
	const int i, const std::string prefix, const std::string postfix) {
	std::stringstream ss;
	ss << prefix << " " << i << " " << postfix;
	textTags.push_back(new TextTag(mapTags[tagType], x, y, ss.str()));
}

void TextTags::updateTextTags(const float& deltaTime) {	
	/*for (TextTag* textTag : textTags) {
		textTag->updateTextTag(deltaTime);
		if (textTag->isFinished()) {
			delete textTag;
			textTags.erase(textTag);
		}
	}*/
	for (int i = 0; i < (int)textTags.size(); ++i) {
		textTags[i]->updateTextTag(deltaTime);
		if (textTags[i]->isFinished()) {
			delete textTags[i];
			textTags.erase(textTags.begin() + i);
		}
	}
}

void TextTags::renderTextTags(sf::RenderTarget& currentWindow) {
	for (TextTag* textTag : textTags) {
		textTag->renderTextTag(currentWindow);
	}
}

TextTags& TextTags::operator=(const TextTags& other) {
	if (this != &other) {
		textTagsFont = other.textTagsFont;
		mapTags.clear();
		for (const auto& pair : other.mapTags) {
			mapTags[pair.first] = new TextTag(*pair.second);
		}
		textTags.clear();
		for (const auto& tag : other.textTags) {
			textTags.push_back(new TextTag(*tag));
		}
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const TextTags& textTags) {
	out << "TextTags Font: " << "\n";
	out << "MapTags:\n";
	for (const auto& pair : textTags.mapTags) {
		out << "  Key: " << pair.first << ", Value: " << *(pair.second) << "\n";
	}
	out << "TextTags:\n";
	for (const auto& tag : textTags.textTags) {
		out << "  " << *tag << "\n";
	}
	return out;
}
