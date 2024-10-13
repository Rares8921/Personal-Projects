#pragma once

#include "GUI.h"
#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include "Filter.h"
#include "Transformer.h"
#include "Adjusters.h"
#include "DrawTool.h"
#include "TextTag.h"
#include "Originator.h"
#include "Caretaker.h"
#include "Memento.h"

class ImageEditor {
private:
	int maxFps = 120;
	int windowWidth = 1850;
	int windowHeight = 950;
	int widthRatio, heightRatio;
	int textFontSize, textSpacing;
	int textBorderRadius, textBorderSize;
	float imageX, imageY, textX, textY;
	Color backgroundColor;
	// Pentru fiecare map folosesc up casting
	std::unordered_map<std::string, std::unique_ptr<GUI::Button>> buttons;
	std::unordered_map<std::string, std::unique_ptr<Filter>> filters;
	std::unordered_map<std::string, std::unique_ptr<Transformer>> transforms;
	std::unordered_map<std::string, std::unique_ptr<Adjusters>> adjusters;
	std::unordered_map<std::string, bool> dropdownMenus, editSubmenus, drawToolsMap, textOptions;
	std::unordered_map<std::string, std::unique_ptr<DrawTool>> drawTools;
	std::unique_ptr<DrawTool> drawTool;
	Eraser* eraser;
	FillTool* fill;
	Font consolasFont, arialFont;
	float imageScale = 1;
	GuiWindowFileDialogState fileDialog;
	Texture2D imageTexture, appIconTexture;
	Image appIcon, currentImage, copyImage;
	std::unique_ptr<Image> absoluteCopy;
	std::string pathToImage;
	// Pentru cautarea extensiilor unei file, folosesc unordered_set pt ca are find in ~O(1)
	const std::unordered_set<std::string> acceptedExtensions 
		= {".png", ".jpg", ".jpeg", ".jfif", ".pjpeg", ".pjp", ".bmp", ".dds", ".psd", ".tga", ".hdr"};
	std::unordered_map<std::string, bool> filterFlags, transformsTags;
	std::unordered_map<std::string, float> sliderValues;
	Color* imagePixels, currentColor, currentTextColor, currentTextShadowColor, borderColor;
	Camera2D imageCamera, adjustSubmenuCamera;
	GUI::Menu testMenu;
	std::shared_ptr<std::string> imageName, imageTextString;
	std::shared_ptr<Rectangle> imageZone;
	RenderTexture2D imageCanvas, submenuCanvas;
	Rectangle adjustMenuBounds = { 1551.f, 90.f, 300.f, 1500.f }, drawMenuBounds = { 1551.f, 90.f, 300.f, 1500.f }, textMenuBounds = { 1551.f, 90.f, 300.f, 1500.f };
	Rectangle adjustMenuContentBounds = { 0, 0, 300.f, 3000.f }, drawMenuContentBounds = { 0, 0, 300.f, 1700.f }, textMenuContentBounds = { 0, 0, 300.f, 3050.f };
	Vector2 adjustMenuScrollPosition = { 0, 0 }, drawMenuScrollPosition = { 0, 0 }, textMenuScrollPosition = { 0, 0 };
	Rectangle adjustMenuView, drawMenuView, textMenuView;
	int imageCanvasX, imageCanvasY;
	// Pentru image view
	bool isDraggable;
	Vector2 lastMousePos, imageOffset;
	// Image text
	TextTag imageText;
	Originator ImageOriginator;
	Caretaker ImageHistory;
	int imageIndex, maxImageIndex;
	void initWindow();
	void initComponents();
	void centerWindow();
	void resetSubMenu(std::string subMenu);
	void resetDrawTools(std::string drawTool);
	void setAdjustSliderValues();
	void drawMenuBarButton(std::string buttonKey);
public:
	ImageEditor();
	virtual ~ImageEditor();
	void moveWindow();
	void DrawMenuBar();
	void DrawSideBar();
	void DrawArrangeSubMenu();
	void DrawCropSubMenu();
	void DrawAdjustSubMenu();
	void DrawFilterSubMenu();
	void DrawAiToolsSubMenu();
	void DrawDrawSubMenu();
	void DrawTextSubMenu();
	void applyImageFilter(int filterCode);
	void renderWindow();
	ImageEditor& operator=(const ImageEditor& imageEditor);
};

