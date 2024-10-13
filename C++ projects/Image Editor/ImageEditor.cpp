#include "ImageEditor.h"
#define RAYGUI_IMPLEMENTATION
#include "raygui.h"
#undef RAYGUI_IMPLEMENTATION 
#define GUI_WINDOW_FILE_DIALOG_IMPLEMENTATION
#include "gui_window_file_dialog.h"
#include "GrayscaleFilter.h"
#include "InvertedFilter.h"
#include "ShineFilter.h"
#include "ChromeFilter.h"
#include "FlareFilter.h"
#include "DiffOfGaussiansFilter.h"
#include "FadeFilter.h"
#include "ErodeFilter.h"
#include "SparkleFilter.h"
#include "FlipFilter.h"
#include "RotateFilter.h"
#include "GrainAdjust.h"
#include "OpacityAdjust.h"
#include "VibranceAdjust.h"
#include "SaturationAdjust.h"
#include "TemperatureAdjust.h"
#include "TintAdjust.h"
#include "HueAdjust.h"
#include "BrigthnessAdjust.h"
#include "ExposureAdjust.h"
#include "ContrastAdjust.h"
#include "BlackAdjust.h"
#include "AspectRatio.h"
#include "WhiteAdjust.h"
#include "HighlightsAdjust.h"
#include "ShadowsAdjust.h"
#include "SharpenAdjust.h"
#include "ClarityAdjust.h"
#include "SmoothnessAdjust.h"
#include "BlurAdjust.h"
#include "VignetteAdjust.h"
#include "GlamourAdjust.h"
#include "BloomAdjust.h"
#include "DehazeAdjust.h"
#include "Brush.h"
#include "OCR.h"
#include "BackgroundRemover.h"
#include "FacialRecognition.h"
#include "AutomaticColorization.h"
#include "ConvexHull.h"
#include "QuadTree.h"

#include <opencv2/objdetect.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>

void ImageEditor::initWindow() {
    InitWindow(windowWidth, windowHeight, "Image Editor");
    SetWindowState(FLAG_WINDOW_UNDECORATED);
    SetTargetFPS(maxFps);
}

void ImageEditor::initComponents() {

    // Texture-ul pentru imaginea care va fi incarcata
    imageTexture = { 0 };
    imageCamera = { 0 };
    imageCamera.zoom = 1;
    adjustSubmenuCamera = { 0 };
    adjustSubmenuCamera.zoom = 1;
    widthRatio = heightRatio = 1;
    textFontSize = 12;
    textSpacing = 20;
    textBorderRadius = 1;
    textBorderSize = 2;
    textX = 0, textY = 0;


    ImageHistory = Caretaker();
    ImageOriginator = Originator();
    imageCanvas = LoadRenderTexture(1415, 785);
    submenuCanvas = LoadRenderTexture(300, 3000);

    currentTextColor = currentColor = currentTextShadowColor = borderColor = BLACK;

    // Initializare font si text
    consolasFont = LoadFont("Consolas.ttf");
    arialFont = LoadFont("Arial.ttf");
    imageName = std::make_shared<std::string>("Empty");
    imageTextString = std::make_shared<std::string>("Empty");

    appIcon = LoadImage("appIcon.png");
    SetWindowIcon(appIcon);
    appIconTexture = LoadTextureFromImage(appIcon);

    // Background
    backgroundColor = Color({ 31, 31, 31, 255 });
    eraser = Eraser::getInstance();
    fill = FillTool::getInstance();
        
    // File dialog
    fileDialog = InitGuiWindowFileDialog(GetWorkingDirectory());

    // Pentru image view
    isDraggable = false;
    lastMousePos = imageOffset = { 0, 0 };

    drawTool = std::make_unique<DrawTool>();
    imageIndex = -1;

    // Menu bar
    buttons["FILE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("File", 0, 40, 70, 50)));
    dropdownMenus["FILE"] = false;

    buttons["EDIT"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Edit", 75, 40, 70, 50)));
    dropdownMenus["EDIT"] = false;

    buttons["VIEW"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("View", 150, 40, 70, 50)));
    dropdownMenus["VIEW"] = false;

    buttons["IMAGE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Image", 225, 40, 70, 50)));
    dropdownMenus["IMAGE"] = false;

    buttons["HELP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Help", 300, 40, 70, 50)));
    dropdownMenus["HELP"] = false;

    // Side bar
    buttons["ARRANGE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Arrange", 18, 110, 100, 50)));
    sliderValues["OPACITY"] = 100.f;
    sliderValues["DEGREES"] = 0.f;
    editSubmenus["ARRANGE"] = false;

    buttons["SN_CROP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Crop", 12, 160, 75, 50)));
    editSubmenus["SN_CROP"] = false;

    buttons["ADJUST"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Adjust", 12, 210, 95, 50)));
    editSubmenus["ADJUST"] = false;
    setAdjustSliderValues();

    buttons["SN_FILTER"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Filter", 7, 260, 95, 50)));
    editSubmenus["SN_FILTER"] = false;

    buttons["AI_TOOLS"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("AI Tools", 10, 310, 120, 50)));
    buttons["AI_TOOLS"]->setBorder(false);
    editSubmenus["AI_TOOLS"] = false;

    buttons["DRAW"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Draw", 16, 360, 70, 50)));
    editSubmenus["DRAW"] = false;

    buttons["TEXT"] = std::move(std::unique_ptr<GUI::Button>(new GUI::Button("Text", 14, 410, 70, 50)));
    editSubmenus["TEXT"] = false;

    // Setez look and feel-ul pentru fiecare dintre butoanele meniului
    for (auto& it : buttons) {
        it.second->setBackground(backgroundColor);
        it.second->setForeground(WHITE);
        it.second->setFont(arialFont);
        it.second->setFontSize(22);
    }

    // Butoane
    std::unique_ptr<GUI::Button> closeButton(new GUI::Button("X", windowWidth - 50, 0, 50, 50));
    buttons["CLOSE"] = std::move(closeButton);
    buttons["CLOSE"]->setShadowOffset(0.03);
    buttons["CLOSE"]->setFontSize(25);
    buttons["CLOSE"]->setFont(consolasFont);
    buttons["CLOSE"]->setBackground(BLACK);
    buttons["CLOSE"]->setForeground(WHITE);

    std::unique_ptr<GUI::Button> minimizeButton(new GUI::Button("--", windowWidth - 100, 0, 50, 50));
    buttons["MINIMIZE"] = std::move(minimizeButton);
    buttons["MINIMIZE"]->setShadowOffset(0.03);
    buttons["MINIMIZE"]->setFontSize(45);
    buttons["MINIMIZE"]->setFont(consolasFont);
    buttons["MINIMIZE"]->setBackground(BLACK);
    buttons["MINIMIZE"]->setForeground(WHITE);

    // Butoanele pentru filtre
    buttons["GRAYSCALE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Grayscaling", 1635, 200, 150, 50)));
    buttons["INVERTED"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Inverting", 1635, 255, 120, 50)));
    buttons["SHINE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Shining", 1640, 310, 120, 50)));
    buttons["CHROME"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Chrome", 1642, 365, 120, 50)));
    buttons["FADE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Fade", 1645, 420, 120, 50)));
    buttons["DOG"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("DiffOfGaussians", 1600, 475, 220, 50)));
    buttons["ERODE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Erode", 1645, 535, 120, 50)));
    buttons["FLARE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Flare", 1645, 595, 120, 50)));
    buttons["SPARKLE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Sparkle", 1640, 655, 120, 50)));

    // Butoanele din sectiunea crop
    buttons["FLIP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Horizontal flip", 1585, 550, 170, 50)));
    buttons["VERTICAL_FLIP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Vertical flip", 1585, 605, 150, 50)));
    buttons["LEFT_ROTATE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Left rotate", 1580, 425, 150, 50)));
    buttons["RIGHT_ROTATE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Right rotate", 1585, 480, 150, 50)));

    // Butoanele din sectiunea ai tools
    buttons["OCR"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Perform OCR", 1635, 240, 150, 50)));
    buttons["BKREMOVE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Background remove", 1580, 295, 250, 50)));
    buttons["FRECOGNITION"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Facial recognition", 1580, 350, 250, 50)));
    buttons["COLORIZATION"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Automatic Colorization", 1565, 405, 270, 50)));
    buttons["CONVEXHULL"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Convex hull", 1630, 460, 150, 50)));

    // Butoanele pentru draw
    buttons["BRUSH"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Brush", 1585, 290, 70, 50)));
    buttons["ERASER"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Eraser", 1585, 345, 80, 50)));
    buttons["PEN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Pen", 1585, 400, 60, 50)));
    sliderValues["PEN"] = 5.f;

    buttons["PENSIZEUP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1740, 280, 25, 30)));
    buttons["PENSIZEDOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1770, 280, 25, 30)));

    buttons["FILL"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Fill", 1585, 455, 60, 50)));
    buttons["PIXEL"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Pixel", 1585, 510, 65, 50)));

    // Butoanele pentru sectiunea text
    buttons["BOLD"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("B", 1645, 210, 25, 50)));
    textOptions["BOLD"] = false;

    buttons["ITALIC"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("I", 1685, 210, 25, 50)));
    textOptions["ITALIC"] = false;

    buttons["UNDERLINED"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("U", 1725, 210, 25, 50)));
    textOptions["UNDERLINED"] = false;

    buttons["FONTSIZEUP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1740, 280, 25, 30)));
    buttons["FONTSIZEDOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1770, 280, 25, 30)));

    buttons["SPACINGUP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1740, 280, 25, 30)));
    buttons["SPACINGDOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1770, 280, 25, 30)));

    buttons["RECTANGULAR"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Rectangular", 1725, 210, 135, 50)));
    textOptions["RECTANGULAR"] = false;

    buttons["ROUNDED"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Rounded", 1725, 210, 125, 50)));
    textOptions["ROUNDED"] = false;

    buttons["LINE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Line", 1725, 210, 75, 50)));
    textOptions["LINE"] = false;

    buttons["DOTTED"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Dotted", 1725, 210, 85, 50)));
    textOptions["DOTTED"] = false;

    buttons["TBRADIUSUP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1725, 210, 25, 30)));
    textOptions["TBRADIUSUP"] = false;

    buttons["TBRADIUSDOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1725, 210, 25, 30)));
    textOptions["TBRADIUSDOWN"] = false;

    buttons["TBSIZEUP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1725, 210, 25, 30)));
    textOptions["TBSIZEUP"] = false;

    buttons["TBSIZEDOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1725, 210, 25, 30)));
    textOptions["TBSIZEDOWN"] = false;

    buttons["PLACETEXT"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Place", 1725, 210, 70, 40)));
    textOptions["PLACETEXT"] = false;

    // Transformers
    buttons["CROP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("C", 1000, 100, 50, 50)));
    transformsTags["CROP"] = false;
    buttons["TRANSFORM_RATIO"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Transform", 1580, 305, 150, 50)));

    // Tot la transformers, butoanele de ratio up si ratio down pentru width si height
    buttons["WIDTH_RATIO_UP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1790, 220, 20, 20)));
    buttons["WIDTH_RATIO_UP"]->setFontSize(50);
    static_cast<GUI::RoundedButton*>(buttons["WIDTH_RATIO_UP"].get())->setRadius(0.25);

    buttons["WIDTH_RATIO_DOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1811, 220, 20, 20)));
    buttons["WIDTH_RATIO_DOWN"]->setFontSize(50);
    static_cast<GUI::RoundedButton*>(buttons["WIDTH_RATIO_DOWN"].get())->setRadius(0.25);

    buttons["HEIGHT_RATIO_UP"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("^", 1790, 260, 20, 20)));
    buttons["HEIGHT_RATIO_UP"]->setFontSize(50);
    static_cast<GUI::RoundedButton*>(buttons["HEIGHT_RATIO_UP"].get())->setRadius(0.25);

    buttons["HEIGHT_RATIO_DOWN"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("v", 1811, 260, 20, 20)));
    buttons["HEIGHT_RATIO_DOWN"]->setFontSize(50);
    static_cast<GUI::RoundedButton*>(buttons["HEIGHT_RATIO_DOWN"].get())->setRadius(0.25);

    // Functionality buttons
    buttons["SAVE"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Save image", 800, 885, 175, 50)));
    buttons["RESET"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Reset image", 1000, 885, 175, 50)));
    buttons["UNDO"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Undo", 1180, 885, 155, 50)));
    buttons["REDO"] = std::move(std::unique_ptr<GUI::Button>(new GUI::RoundedButton("Redo", 1360, 885, 155, 50)));

    // Adjusters   
    adjusters["OPACITY"] = std::move(std::unique_ptr<OpacityAdjust>(new OpacityAdjust()));
    adjusters["VIBRANCE"] = std::move(std::unique_ptr<VibranceAdjust>(new VibranceAdjust()));
    adjusters["SATURATION"] = std::move(std::unique_ptr<SaturationAdjust>(new SaturationAdjust()));
    adjusters["BRIGTHNESS"] = std::move(std::unique_ptr<BrigthnessAdjust>(new BrigthnessAdjust()));
    adjusters["TEMPERATURE"] = std::move(std::unique_ptr<TemperatureAdjust>(new TemperatureAdjust()));
    adjusters["TINT"] = std::move(std::unique_ptr<TintAdjust>(new TintAdjust()));
    adjusters["HUE"] = std::move(std::unique_ptr<HueAdjust>(new HueAdjust()));
    adjusters["EXPOSURE"] = std::move(std::unique_ptr<ExposureAdjust>(new ExposureAdjust()));
    adjusters["CONTRAST"] = std::move(std::unique_ptr<ContrastAdjust>(new ContrastAdjust()));
    adjusters["BLACK"] = std::move(std::unique_ptr<BlackAdjust>(new BlackAdjust()));
    adjusters["WHITE"] = std::move(std::unique_ptr<WhiteAdjust>(new WhiteAdjust()));
    adjusters["HIGHLIGHTS"] = std::move(std::unique_ptr<HighlightsAdjust>(new HighlightsAdjust()));
    adjusters["SHADOWS"] = std::move(std::unique_ptr<ShadowsAdjust>(new ShadowsAdjust()));
    adjusters["SHARPEN"] = std::move(std::unique_ptr<SharpenAdjust>(new SharpenAdjust()));
    adjusters["CLARITY"] = std::move(std::unique_ptr<ClarityAdjust>(new ClarityAdjust()));
    adjusters["SMOOTHNESS"] = std::move(std::unique_ptr<SmoothnessAdjust>(new SmoothnessAdjust()));
    adjusters["BLUR"] = std::move(std::unique_ptr<BlurAdjust>(new BlurAdjust()));
    adjusters["GRAIN"] = std::move(std::unique_ptr<GrainAdjust>(new GrainAdjust()));
    adjusters["VIGNETTE"] = std::move(std::unique_ptr<VignetteAdjust>(new VignetteAdjust()));
    adjusters["GLAMOUR"] = std::move(std::unique_ptr<GlamourAdjust>(new GlamourAdjust()));
    adjusters["BLOOM"] = std::move(std::unique_ptr<BloomAdjust>(new BloomAdjust()));
    adjusters["DEHAZE"] = std::move(std::unique_ptr<DehazeAdjust>(new DehazeAdjust()));

    // Filtre
    filters["GRAYSCALE"] = std::move(std::unique_ptr<GrayscaleFilter>(new GrayscaleFilter()));
    filterFlags["GRAYSCALE"] = false;

    filters["INVERTED"] = std::move(std::unique_ptr<InvertedFilter>(new InvertedFilter()));
    filterFlags["INVERTED"] = false;

    filters["SHINE"] = std::move(std::unique_ptr<ShineFilter>(new ShineFilter()));
    filterFlags["SHINE"] = false;

    filters["CHROME"] = std::move(std::unique_ptr<ChromeFilter>(new ChromeFilter()));
    filterFlags["CHROME"] = false;

    filters["FADE"] = std::move(std::unique_ptr<FadeFilter>(new FadeFilter()));
    filterFlags["FADE"] = false;

    filters["DOG"] = std::move(std::unique_ptr<DiffOfGaussiansFilter>(new DiffOfGaussiansFilter()));
    filterFlags["DOG"] = false;

    filters["ERODE"] = std::move(std::unique_ptr<ErodeFilter>(new ErodeFilter()));
    filterFlags["ERODE"] = false;

    filters["FLARE"] = std::move(std::unique_ptr<FlareFilter>(new FlareFilter()));
    filterFlags["FLARE"] = false;

    filters["SPARKLE"] = std::move(std::unique_ptr<SparkleFilter>(new SparkleFilter()));
    filterFlags["SPARKLE"] = false;

    filters["FLIP"] = std::move(std::unique_ptr<FlipFilter>(new FlipFilter()));
    filterFlags["FLIP"] = false;

    filters["VERTICAL_FLIP"] = std::move(std::unique_ptr<FlipFilter>(new FlipFilter()));
    filterFlags["VERTICAL_FLIP"] = false;

    filters["LEFT_ROTATE"] = std::move(std::unique_ptr<RotateFilter>(new RotateFilter()));
    filterFlags["LEFT_ROTATE"] = false;

    filters["RIGHT_ROTATE"] = std::move(std::unique_ptr<RotateFilter>(new RotateFilter()));
    filterFlags["RIGHT_ROTATE"] = false;

    for (auto& it : buttons) {
        it.second->setBackground(backgroundColor);
        it.second->setForeground(WHITE);
        it.second->setFont(arialFont);
        it.second->setFontSize(22);
        it.second->setBorder(true);
        it.second->setBorderColor({ 0, 0, 0, 0 });
        it.second->setHoverBackground({ 61, 61, 61, 255 });
        it.second->setHoverForeground(WHITE);
        it.second->setBorderHoverColor(WHITE);
        it.second->setActiveBackground({ 46, 46, 46, 255 });
        it.second->setActiveForeground({ 178, 178, 178, 255 });
        it.second->setBorderActiveColor(DARKGRAY);
    }
    buttons["WIDTH_RATIO_UP"]->setBorderColor(WHITE);
    buttons["WIDTH_RATIO_DOWN"]->setBorderColor(WHITE);
    buttons["HEIGHT_RATIO_UP"]->setBorderColor(WHITE);
    buttons["HEIGHT_RATIO_DOWN"]->setBorderColor(WHITE);
    buttons["FONTSIZEUP"]->setBorderColor(WHITE);
    buttons["FONTSIZEDOWN"]->setBorderColor(WHITE);
    buttons["SPACINGUP"]->setBorderColor(WHITE);
    buttons["SPACINGDOWN"]->setBorderColor(WHITE);
    buttons["TBRADIUSUP"]->setBorderColor(WHITE);
    buttons["TBRADIUSDOWN"]->setBorderColor(WHITE);
    buttons["TBSIZEUP"]->setBorderColor(WHITE);
    buttons["TBSIZEDOWN"]->setBorderColor(WHITE);
    
    // Draw tools
    drawTools["DRAWTOOL"] = std::move(std::unique_ptr<DrawTool>(new DrawTool()));
    drawToolsMap["BRUSH"] = false;
    drawToolsMap["ERASER"] = false;
    drawToolsMap["PEN"] = false;
    drawToolsMap["FILL"] = false;
}

void ImageEditor::centerWindow() {
    int currentMonitor = GetCurrentMonitor();
    int width = GetMonitorWidth(currentMonitor), height = GetMonitorHeight(currentMonitor);
    SetWindowPosition(width / 2 - windowWidth / 2, height / 2 - windowHeight / 2);
}

void ImageEditor::resetSubMenu(std::string subMenu) {
    bool temp = editSubmenus[subMenu];
    // Resetez oricare meniu este afisat
    for (std::unordered_map<std::string, bool>::iterator it = editSubmenus.begin();
        it != editSubmenus.end(); ++it) {
        it->second = false;
    }
    editSubmenus[subMenu] = !temp;
}

void ImageEditor::resetDrawTools(std::string drawTool) {
    bool temp = !drawToolsMap[drawTool];
    for (auto it : drawToolsMap) {
        drawToolsMap[it.first] = false;
    }
    drawToolsMap[drawTool] = temp;
}

void ImageEditor::setAdjustSliderValues() {
    sliderValues["VIBRANCE"] = 0.f;
    sliderValues["SATURATION"] = 0.f;
    sliderValues["TEMPERATURE"] = 0.f;
    sliderValues["TINT"] = 0.f;
    sliderValues["HUE"] = 0.f;
    sliderValues["BRIGTHNESS"] = 100.f;
    sliderValues["EXPOSURE"] = 0.f;
    sliderValues["CONTRAST"] = 0.f;
    sliderValues["BLACK"] = 0.f;
    sliderValues["WHITE"] = 0.f;
    sliderValues["HIGHLIGHTS"] = 0.f;
    sliderValues["SHADOWS"] = 0.f;
    sliderValues["SHARPEN"] = 0.f;
    sliderValues["CLARITY"] = 0.f;
    sliderValues["SMOOTHNESS"] = 0.f;
    sliderValues["BLUR"] = 0.f;
    sliderValues["GRAIN"] = 0.f;
    sliderValues["VIGNETTE"] = 0.f;
    sliderValues["GLAMOUR"] = 0.f;
    sliderValues["BLOOM"] = 0.f;
    sliderValues["DEHAZE"] = 0.f;
}

void ImageEditor::drawMenuBarButton(std::string buttonKey) {
    if (buttons[buttonKey]->drawButton() == 0) {
        bool state = dropdownMenus[buttonKey];
        for (const auto& it : dropdownMenus) {
            dropdownMenus[it.first] = false;
        }
        dropdownMenus[buttonKey] = !state;
    }
}

ImageEditor::ImageEditor() {
    initWindow();
    centerWindow();
    initComponents();
}

ImageEditor::~ImageEditor() {
    
}

void ImageEditor::moveWindow() {

}

void ImageEditor::DrawMenuBar() {
    drawMenuBarButton("FILE");
    drawMenuBarButton("EDIT");
    drawMenuBarButton("VIEW");
    drawMenuBarButton("IMAGE");
    drawMenuBarButton("HELP");
}

void ImageEditor::DrawSideBar() {
    if (buttons["ARRANGE"]->drawButton() == 0) {
        resetSubMenu("ARRANGE");
    } else if (buttons["SN_CROP"]->drawButton() == 0) {
        resetSubMenu("SN_CROP");
    } else if (buttons["ADJUST"]->drawButton() == 0) {
        resetSubMenu("ADJUST");
    } else if (buttons["SN_FILTER"]->drawButton() == 0) {
        resetSubMenu("SN_FILTER");
    } else if (buttons["AI_TOOLS"]->drawButton() == 0) {
        resetSubMenu("AI_TOOLS");
    } else if (buttons["DRAW"]->drawButton() == 0) {
        resetSubMenu("DRAW");
    } else if(buttons["TEXT"]->drawButton() == 0) {
        resetSubMenu("TEXT");
    }
}

void ImageEditor::DrawArrangeSubMenu() {
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ 1565, 120, 275, 600 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Image name", { 1585, 180 }, 20, 0.3f, RAYWHITE);
    // TextField
    //GuiTextInputBox({ 1580, 220, 250, 30 }, "A", "B", "S", const_cast<char*>(imageName.get()->c_str()), 25, test);
    GuiTextBox({ 1580, 220, 250, 30 }, const_cast<char*>(imageName.get()->c_str()), 50, true);
    // Opacity
    DrawTextEx(consolasFont, "Opacity", { 1585, 275 }, 20, 0.3f, RAYWHITE);
    // Slider-ul pentru opacity;   
    float temp = sliderValues["OPACITY"];
    sliderValues["OPACITY"] = GuiSliderPro({ 1585, 300, 150, 15 }, "0", "100", &sliderValues["OPACITY"], 0.f, 100.f, 50);
    if (temp != sliderValues["OPACITY"]) {
        adjusters["OPACITY"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["OPACITY"]);
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    }
    // Width si text field
    DrawTextEx(consolasFont, "Width", { 1585, 350 }, 20, 0.3f, RAYWHITE);
    DrawRectangleRoundedLines({ 1660, 345, 50, 30 }, 0.15, 5, 1, RAYWHITE);
    DrawRectangleRounded({ 1660, 345, 50, 30 }, 0.15, 5, Color({ 50, 50, 50, 255 }));
    DrawTextEx(consolasFont, std::to_string(currentImage.width).c_str(), { 1665, 350}, 20, 0.3f, RAYWHITE);
    // Height si text field
    DrawTextEx(consolasFont, "Height", { 1585, 390 }, 20, 0.3f, RAYWHITE);
    DrawRectangleRoundedLines({ 1660, 385, 50, 30 }, 0.15, 5, 1, RAYWHITE);
    DrawRectangleRounded({ 1660, 385, 50, 30 }, 0.15, 5, Color({ 50, 50, 50, 255 }));
    DrawTextEx(consolasFont, std::to_string(currentImage.height).c_str(), { 1665, 390 }, 20, 0.3f, RAYWHITE);
    if (buttons["FLIP"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["FLIP"] = !filterFlags["FLIP"];
    }
    if (buttons["VERTICAL_FLIP"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["VERTICAL_FLIP"] = !filterFlags["VERTICAL_FLIP"];
    }
    if (filterFlags["FLIP"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::FLIP);
        filterFlags["FLIP"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["VERTICAL_FLIP"]) {
        applyImageFilter(FILTER_TYPES::VERTICAL_FLIP);
        filterFlags["VERTICAL_FLIP"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    }
}

void ImageEditor::DrawCropSubMenu() {
    // De facut sa se afiseze si acel dreptunghi prin care dai crop
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ 1565, 120, 275, 600 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Crop", { 1670, 165 }, 25, 0.3f, RAYWHITE);
    // Width aspect ratio si text field
    DrawTextEx(consolasFont, "Width ratio", { 1585, 220 }, 20, 0.3f, RAYWHITE);
    DrawRectangleRoundedLines({ 1730, 215, 50, 30 }, 0.15, 5, 1, RAYWHITE);
    DrawRectangleRounded({ 1730, 215, 50, 30 }, 0.15, 5, Color({ 50, 50, 50, 255 }));
    DrawTextEx(consolasFont, std::to_string(widthRatio).c_str(), { 1750, 220 }, 20, 0.3f, RAYWHITE);
    // Butoanele de up si down pentru width ratio
    if (buttons["WIDTH_RATIO_UP"]->drawButton() == 0) {
        widthRatio = std::min(widthRatio + 1, 25);
    } if (buttons["WIDTH_RATIO_DOWN"]->drawButton() == 0) {
        widthRatio = std::max(widthRatio - 1, 1);
    }
    if (buttons["HEIGHT_RATIO_UP"]->drawButton() == 0) {
        heightRatio = std::min(heightRatio + 1, 25);
    } if (buttons["HEIGHT_RATIO_DOWN"]->drawButton() == 0) {
        heightRatio = std::max(heightRatio - 1, 1);
    }
    // Height aspect ratio si text field
    DrawTextEx(consolasFont, "Height ratio", { 1585, 260 }, 20, 0.3f, RAYWHITE);
    DrawRectangleRoundedLines({ 1730, 255, 50, 30 }, 0.15, 5, 1, RAYWHITE);
    DrawRectangleRounded({ 1730, 255, 50, 30 }, 0.15, 5, Color({ 50, 50, 50, 255 }));
    DrawTextEx(consolasFont, std::to_string(heightRatio).c_str(), { 1750, 260 }, 20, 0.3f, RAYWHITE);
    if (buttons["TRANSFORM_RATIO"]->drawButton() == 0 && pathToImage.size() > 0) {
        AspectRatio aR;
        currentImage = aR.changeImageRatio(currentImage, std::to_string(widthRatio) + ":" + std::to_string(heightRatio));
        imagePixels = LoadImageColors(currentImage);
        UpdateTexture(imageTexture, imagePixels);
        ImageOriginator.setImage(currentImage);
        Memento tempMemento = ImageOriginator.saveState();
        ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
    }
    // Text functii
    DrawTextEx(consolasFont, "Rotate", { 1585, 375 }, 22, 0.3f, RAYWHITE);
    if (buttons["LEFT_ROTATE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["LEFT_ROTATE"] = !filterFlags["LEFT_ROTATE"];
    }
    else if (buttons["RIGHT_ROTATE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["RIGHT_ROTATE"] = !filterFlags["RIGHT_ROTATE"];
    }
   if (filterFlags["LEFT_ROTATE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::LEFT_ROTATE);
        filterFlags["LEFT_ROTATE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
       //QuadTree<Color> tree(0, 0, currentImage.width, currentImage.height, currentImage);
       //tree.rotateTrigWise();
       //tree.loadToImage(currentImage);
       //imagePixels = LoadImageColors(currentImage);
       //UpdateTexture(imageTexture, imagePixels);
       //filterFlags["LEFT_ROTATE"] = false;
    } else if (filterFlags["RIGHT_ROTATE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::RIGHT_ROTATE);
        filterFlags["RIGHT_ROTATE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    }
}

void ImageEditor::DrawAdjustSubMenu() {
    GuiScrollPanel(adjustMenuBounds, NULL, adjustMenuContentBounds, &adjustMenuScrollPosition, &adjustMenuView);
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ adjustMenuBounds.x + 10, adjustMenuBounds.y + adjustMenuScrollPosition.y + 15, 270, 2350 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Adjust", { adjustMenuBounds.x + 110, adjustMenuBounds.y + adjustMenuScrollPosition.y + 50 }, 25, 0.3f, RAYWHITE);
    // Submeniul color
    DrawRectangleRoundedLines({ adjustMenuBounds.x + 35, adjustMenuBounds.y + adjustMenuScrollPosition.y + 100, 235, 500 }, 0.15, 5, 1, DARKGRAY);
    // Titlu submeniu - Color
        DrawTextEx(consolasFont, "Color", { adjustMenuBounds.x + 50, adjustMenuBounds.y + adjustMenuScrollPosition.y + 115 }, 25, 0.3f, RAYWHITE);
        // Optiuni
        // Vibrance
        DrawTextEx(consolasFont, "Vibrance", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 155 }, 20, 0.3f, RAYWHITE);
        float temp = sliderValues["VIBRANCE"];
        sliderValues["VIBRANCE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 185, 150, 15 }, "-100", "100", &sliderValues["VIBRANCE"], -100.f, 100.f, 10);
        if (temp != sliderValues["VIBRANCE"]) {
            adjusters["VIBRANCE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["VIBRANCE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Saturation
        temp = sliderValues["SATURATION"];
        DrawTextEx(consolasFont, "Saturation", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 235 }, 20, 0.3f, RAYWHITE);
        sliderValues["SATURATION"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 265, 150, 15 }, "-100", "100", &sliderValues["SATURATION"], -100.f, 100.f, 10);
        if (temp != sliderValues["SATURATION"]) {
            adjusters["SATURATION"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["SATURATION"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Temperature
        temp = sliderValues["TEMPERATURE"];
        DrawTextEx(consolasFont, "Temperature", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 315 }, 20, 0.3f, RAYWHITE);
        sliderValues["TEMPERATURE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 345, 150, 15 }, "-100", "100", &sliderValues["TEMPERATURE"], -100.f, 100.f, 10);
        if (temp != sliderValues["TEMPERATURE"]) {
            adjusters["TEMPERATURE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["TEMPERATURE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Tint
        temp = sliderValues["TINT"];
        DrawTextEx(consolasFont, "Tint", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 395 }, 20, 0.3f, RAYWHITE);
        sliderValues["TINT"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 425, 150, 15 }, "-100", "100", &sliderValues["TINT"], -100.f, 100.f, 10);
        if (temp != sliderValues["TINT"]) {
            
            adjusters["TINT"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["TINT"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Hue
        temp = sliderValues["HUE"];
        DrawTextEx(consolasFont, "Hue", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 475 }, 20, 0.3f, RAYWHITE);
        sliderValues["HUE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 505, 150, 15 }, "-100", "100", &sliderValues["HUE"], -100.f, 100.f, 10);
        if (temp != sliderValues["HUE"]) {
            
            adjusters["HUE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["HUE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
    
    // Submeniul light
    DrawRectangleRoundedLines({ adjustMenuBounds.x + 35, adjustMenuBounds.y + adjustMenuScrollPosition.y + 620, 235, 750 }, 0.15, 5, 1, DARKGRAY);
    // Titlu submeniu - Light
        DrawTextEx(consolasFont, "Light", { adjustMenuBounds.x + 50, adjustMenuBounds.y + adjustMenuScrollPosition.y + 635 }, 25, 0.3f, RAYWHITE);
        // Optiuni
        // Brigthness
        temp = sliderValues["BRIGTHNESS"];
        DrawTextEx(consolasFont, "Brigthness", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 685 }, 20, 0.3f, RAYWHITE);
        sliderValues["BRIGTHNESS"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 715, 150, 15 }, "0", "100", &sliderValues["BRIGTHNESS"], 0.f, 100.f, 10);
        if (temp != sliderValues["BRIGTHNESS"]) {
            
            adjusters["BRIGTHNESS"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["BRIGTHNESS"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Exposure
        temp = sliderValues["EXPOSURE"];
        DrawTextEx(consolasFont, "Exposure", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 765 }, 20, 0.3f, RAYWHITE);
        sliderValues["EXPOSURE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 795, 150, 15 }, "-100", "100", &sliderValues["EXPOSURE"], -100.f, 100.f, 10);
        if (temp != sliderValues["EXPOSURE"]) {
            
            adjusters["EXPOSURE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["EXPOSURE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Contrast
        temp = sliderValues["CONTRAST"];
        DrawTextEx(consolasFont, "Contrast", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 845 }, 20, 0.3f, RAYWHITE);
        sliderValues["CONTRAST"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 895, 150, 15 }, "-100", "100", &sliderValues["CONTRAST"], -100.f, 100.f, 10);
        if (temp != sliderValues["CONTRAST"]) {
            
            adjusters["CONTRAST"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["CONTRAST"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Black
        temp = sliderValues["BLACK"];
        DrawTextEx(consolasFont, "Black", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 945 }, 20, 0.3f, RAYWHITE);
        sliderValues["BLACK"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 995, 150, 15 }, "-100", "100", &sliderValues["BLACK"], -100.f, 100.f, 10);
        if (temp != sliderValues["BLACK"]) {
            
            adjusters["BLACK"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["BLACK"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // White
        temp = sliderValues["WHITE"];
        DrawTextEx(consolasFont, "White", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1045 }, 20, 0.3f, RAYWHITE);
        sliderValues["WHITE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1095, 150, 15 }, "-100", "100", &sliderValues["WHITE"], -100.f, 100.f, 10);
        if (temp != sliderValues["WHITE"]) {
            
            adjusters["WHITE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["WHITE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Highligths
        temp = sliderValues["HIGHLIGHTS"];
        DrawTextEx(consolasFont, "Highlights", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1145 }, 20, 0.3f, RAYWHITE);
        sliderValues["HIGHLIGHTS"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1195, 150, 15 }, "-100", "100", &sliderValues["HIGHLIGHTS"], -100.f, 100.f, 10);
        if (temp != sliderValues["HIGHLIGHTS"]) {
            
            adjusters["HIGHLIGHTS"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["HIGHLIGHTS"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Shadows
        temp = sliderValues["SHADOWS"];
        DrawTextEx(consolasFont, "Shadows", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1245 }, 20, 0.3f, RAYWHITE);
        sliderValues["SHADOWS"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1295, 150, 15 }, "-100", "100", &sliderValues["SHADOWS"], -100.f, 100.f, 10);
        if (temp != sliderValues["SHADOWS"]) {
            
            adjusters["SHADOWS"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["SHADOWS"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
    // Submeniul details
    DrawRectangleRoundedLines({ adjustMenuBounds.x + 35, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1410, 235, 500 }, 0.15, 5, 1, DARKGRAY);
    // Titlu submeniu - Details
        DrawTextEx(consolasFont, "Details", { adjustMenuBounds.x + 50, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1425 }, 25, 0.3f, RAYWHITE);
        // Sharpen
        temp = sliderValues["SHARPEN"];
        DrawTextEx(consolasFont, "Sharpen", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1475 }, 20, 0.3f, RAYWHITE);
        sliderValues["SHARPEN"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1505, 150, 15 }, "0", "100", &sliderValues["SHARPEN"], 0.f, 100.f, 10);
        if (temp != sliderValues["SHARPEN"]) {
            
            adjusters["SHARPEN"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["SHARPEN"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Clarity
        temp = sliderValues["CLARITY"];
        DrawTextEx(consolasFont, "Clarity", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1555 }, 20, 0.3f, RAYWHITE);
        sliderValues["CLARITY"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1585, 150, 15 }, "-100", "100", &sliderValues["CLARITY"], -100.f, 100.f, 10);
        if (temp != sliderValues["CLARITY"]) {
            
            adjusters["CLARITY"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["CLARITY"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Smooth
        temp = sliderValues["SMOOTHNESS"];
        DrawTextEx(consolasFont, "Smoothness", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1635 }, 20, 0.3f, RAYWHITE);
        sliderValues["SMOOTHNESS"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1655, 150, 15 }, "0", "100", &sliderValues["SMOOTHNESS"], 0.f, 100.f, 10);
        if (temp != sliderValues["SMOOTHNESS"]) {
            
            adjusters["SMOOTHNESS"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["SMOOTHNESS"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Blur
        temp = sliderValues["BLUR"];
        DrawTextEx(consolasFont, "Blur", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1715 }, 20, 0.3f, RAYWHITE);
        sliderValues["BLUR"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1745, 150, 15 }, "0", "100", &sliderValues["BLUR"], 0.f, 100.f, 10);
        if (temp != sliderValues["BLUR"]) {
            
            adjusters["BLUR"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["BLUR"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Grain
        temp = sliderValues["GRAIN"];
        DrawTextEx(consolasFont, "Grain", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1795 }, 20, 0.3f, RAYWHITE);
        sliderValues["GRAIN"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1825, 150, 15 }, "0", "100", &sliderValues["GRAIN"], 0.f, 100.f, 10);
        if (temp != sliderValues["GRAIN"]) {
            
            adjusters["GRAIN"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["GRAIN"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Submeniul scene
        DrawRectangleRoundedLines({ adjustMenuBounds.x + 35, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1930, 235, 400 }, 0.15, 5, 1, DARKGRAY);
        // Titlu submeniu - Scene
        DrawTextEx(consolasFont, "Scene", { adjustMenuBounds.x + 50, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1945 }, 25, 0.3f, RAYWHITE);
        // Vignette
        temp = sliderValues["VIGNETTE"];
        DrawTextEx(consolasFont, "Vignette", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 1995 }, 20, 0.3f, RAYWHITE);
        sliderValues["VIGNETTE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2025, 150, 15 }, "0", "100", &sliderValues["VIGNETTE"], 0.f, 100.f, 10);
        if (temp != sliderValues["VIGNETTE"]) {
            
            adjusters["VIGNETTE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["VIGNETTE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Glamour
        temp = sliderValues["GLAMOUR"];
        DrawTextEx(consolasFont, "Glamour", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2075 }, 20, 0.3f, RAYWHITE);
        sliderValues["GLAMOUR"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2105, 150, 15 }, "0", "100", &sliderValues["GLAMOUR"], 0.f, 100.f, 10);
        if (temp != sliderValues["GLAMOUR"]) {
            
            adjusters["GLAMOUR"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["GLAMOUR"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Bloom
        temp = sliderValues["BLOOM"];
        DrawTextEx(consolasFont, "Bloom", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2155 }, 20, 0.3f, RAYWHITE);
        sliderValues["BLOOM"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2185, 150, 15 }, "0", "100", &sliderValues["BLOOM"], 0.f, 100.f, 10);
        if (temp != sliderValues["BLOOM"]) {
            
            adjusters["BLOOM"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["BLOOM"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        // Dehaze
        temp = sliderValues["DEHAZE"];
        DrawTextEx(consolasFont, "Dehaze", { adjustMenuBounds.x + 70, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2235 }, 20, 0.3f, RAYWHITE);
        sliderValues["DEHAZE"] = GuiSliderPro({ adjustMenuBounds.x + 80, adjustMenuBounds.y + adjustMenuScrollPosition.y + 2265, 150, 15 }, "0", "100", &sliderValues["DEHAZE"], 0.f, 100.f, 10);
        if (temp != sliderValues["DEHAZE"]) {
            
            adjusters["DEHAZE"]->adjustImage(imagePixels, currentImage.height, currentImage.width, (int)sliderValues["DEHAZE"]);
            UpdateTexture(imageTexture, imagePixels);
            currentImage = LoadImageFromTexture(imageTexture);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
}

void ImageEditor::DrawFilterSubMenu() {
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ 1565, 120, 275, 600 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Filters", { 1660, 165 }, 25, 0.3f, RAYWHITE);
    //GuiScrollPanel(menuBounds, "Test", contentRec, filterSubMenuScrollPosition, NULL);
    if (buttons["GRAYSCALE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["GRAYSCALE"] = !filterFlags["GRAYSCALE"];
    } else if (buttons["INVERTED"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["INVERTED"] = !filterFlags["INVERTED"];
    } else if (buttons["SHINE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["SHINE"] = !filterFlags["SHINE"];
    } else if (buttons["CHROME"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["CHROME"] = !filterFlags["CHROME"];
    } else if (buttons["FADE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["FADE"] = !filterFlags["FADE"];
    } else if (buttons["DOG"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["DOG"] = !filterFlags["DOG"];
    } else if (buttons["ERODE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["ERODE"] = !filterFlags["ERODE"];
    } else if (buttons["FLARE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["FLARE"] = !filterFlags["FLARE"];
    } else if (buttons["SPARKLE"]->drawButton() == 0 && pathToImage.size() > 0) {
        filterFlags["SPARKLE"] = !filterFlags["SPARKLE"];
    }
    if (filterFlags["GRAYSCALE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::GRAYSCALE);
        filterFlags["GRAYSCALE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["INVERTED"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::INVERTED);
        filterFlags["INVERTED"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["SHINE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::SHINE);
        filterFlags["SHINE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["CHROME"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::CHROME);
        filterFlags["CHROME"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["FADE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::FADE);
        filterFlags["FADE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["DOG"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::DIFFOFGAUSSIANS);
        filterFlags["DOG"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["ERODE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::ERODE);
        filterFlags["ERODE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["FLARE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::FLARE);
        filterFlags["FLARE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    } else if (filterFlags["SPARKLE"]) {
        copyImage = ImageCopy(currentImage);
        imagePixels = LoadImageColors(currentImage);
        applyImageFilter(FILTER_TYPES::SPARKLE);
        filterFlags["SPARKLE"] = false;
        UpdateTexture(imageTexture, imagePixels);
        currentImage = LoadImageFromTexture(imageTexture);
    }
}

void ImageEditor::DrawAiToolsSubMenu() {
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ 1565, 120, 275, 600 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "AI Tools", { 1660, 165 }, 25, 0.3f, RAYWHITE);
    if(buttons["OCR"]->drawButton() == 0) {
        OCR ocr;
        ocr.setImagePath("test.png");
        ocr.performOCR();
    } else if (buttons["BKREMOVE"]->drawButton() == 0) {
        BackgroundRemover bkRemover;
        bkRemover.setSourcePath("shoe.jpg");
        bkRemover.removeBackground();
    } else if (buttons["FRECOGNITION"]->drawButton() == 0) {
        FacialRecognition facRecognition;
        facRecognition.setSourcePath("facRegTest.jpg");
        facRecognition.facialRecognition<Rect>();
    } else if(buttons["COLORIZATION"]->drawButton() == 0) {
        AutomaticColorization autoColorization;
        autoColorization.setSourcePath("facRegTest.jpg");
        autoColorization.colorize();
    } else if (buttons["CONVEXHULL"]->drawButton() == 0) {
        ConvexHull convexHull;
        convexHull.setSourcePath("obiecte.png");
        convexHull.computeConvexHull();
    }
}

void ImageEditor::DrawDrawSubMenu() {
    GuiScrollPanel(drawMenuBounds, NULL, drawMenuContentBounds, &drawMenuScrollPosition, &drawMenuView);
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ drawMenuBounds.x + 15, drawMenuBounds.y + drawMenuScrollPosition.y + 30, 265, 1200 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Drawing", { drawMenuBounds.x + 110, drawMenuBounds.y + drawMenuScrollPosition.y + 75 }, 25, 0.3f, RAYWHITE);
    // Submeniul Tools
        DrawRectangleRoundedLines({ drawMenuBounds.x + 35, drawMenuBounds.y + drawMenuScrollPosition.y + 125, 235, 500 }, 0.15, 5, 1, DARKGRAY);
        DrawTextEx(consolasFont, "Tools", { drawMenuBounds.x + 120, drawMenuBounds.y + drawMenuScrollPosition.y + 155}, 25, 0.3f, RAYWHITE);
        buttons["BRUSH"]->setX(drawMenuBounds.x + 120);  buttons["BRUSH"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 210);
        if (buttons["BRUSH"]->drawButton() == 0) {
            resetDrawTools("BRUSH");
        }
        buttons["ERASER"]->setX(drawMenuBounds.x + 115);  buttons["ERASER"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 265); 
        if (buttons["ERASER"]->drawButton() == 0) {
            resetDrawTools("ERASER");
        }
        buttons["PEN"]->setX(drawMenuBounds.x + 125);  buttons["PEN"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 320);
        if (buttons["PEN"]->drawButton() == 0) {
            resetDrawTools("PEN");
        }
        buttons["FILL"]->setX(drawMenuBounds.x + 125);  buttons["FILL"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 375);
        if(buttons["FILL"]->drawButton() == 0) {
            resetDrawTools("FILL");
        }
        buttons["PEN"]->setX(drawMenuBounds.x + 125);  buttons["PEN"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 430);
        if (buttons["PEN"]->drawButton() == 0) {
            resetDrawTools("PEN");
        }
        DrawTextEx(consolasFont, "Size:", { drawMenuBounds.x + 60, drawMenuBounds.y + drawMenuScrollPosition.y + 510}, 20, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, std::to_string((int) sliderValues["PEN"]).c_str(), {drawMenuBounds.x + 130, drawMenuBounds.y + drawMenuScrollPosition.y + 510}, 20, 0.3f, RAYWHITE);
        buttons["PENSIZEUP"]->setX(drawMenuBounds.x + 160);
        buttons["PENSIZEUP"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 500);
        if (buttons["PENSIZEUP"]->drawButton() == 0) {
            sliderValues["PEN"] = std::min(sliderValues["PEN"] + 1, 60.f);
        }
        buttons["PENSIZEDOWN"]->setX(drawMenuBounds.x + 185);
        buttons["PENSIZEDOWN"]->setY(drawMenuBounds.y + drawMenuScrollPosition.y + 500);
        if (buttons["PENSIZEDOWN"]->drawButton() == 0) {
            sliderValues["PEN"] = std::max(sliderValues["PEN"] - 1, 5.f);
        }
    // Submeniul 
        DrawRectangleRoundedLines({ drawMenuBounds.x + 35, drawMenuBounds.y + drawMenuScrollPosition.y + 660, 235, 400 }, 0.15, 5, 1, DARKGRAY);
        DrawTextEx(consolasFont, "Color", { drawMenuBounds.x + 110, drawMenuBounds.y + drawMenuScrollPosition.y + 690}, 25, 0.3f, RAYWHITE);
        Color tempColor = currentColor;
        GuiColorPicker({ drawMenuBounds.x + 50, drawMenuBounds.y + drawMenuScrollPosition.y + 740, 150, 150 }, NULL, &currentColor);
        DrawTextEx(consolasFont, "R: ", {drawMenuBounds.x + 50, drawMenuBounds.y + drawMenuScrollPosition.y + 920}, 22, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, "G: ", {drawMenuBounds.x + 50, drawMenuBounds.y + drawMenuScrollPosition.y + 950}, 22, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, "B: ", {drawMenuBounds.x + 50, drawMenuBounds.y + drawMenuScrollPosition.y + 980}, 22, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, std::to_string(currentColor.r).c_str(), {drawMenuBounds.x + 80, drawMenuBounds.y + drawMenuScrollPosition.y + 920}, 22, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, std::to_string(currentColor.g).c_str(), { drawMenuBounds.x + 80, drawMenuBounds.y + drawMenuScrollPosition.y + 950 }, 22, 0.3f, RAYWHITE);
        DrawTextEx(consolasFont, std::to_string(currentColor.b).c_str(), { drawMenuBounds.x + 80, drawMenuBounds.y + drawMenuScrollPosition.y + 980 }, 22, 0.3f, RAYWHITE);
    Vector2 mousePos = GetMousePosition();
    if(IsMouseButtonDown(MOUSE_BUTTON_LEFT) && 
        imagePixels && CheckCollisionPointRec(mousePos, { imageX + 140, imageY + 90, currentImage.width * 1.f, currentImage.height * 1.f})) {
        drawTool->setBrushColor(currentColor);
        eraser->setEraserSize(sliderValues["PEN"]);
        drawTool->setBrushSize(sliderValues["PEN"]);
        fill->setFillColor(currentColor);
        if (drawToolsMap["BRUSH"]) {
            int temp = drawTool->getBrushSize();
            drawTool->setBrushSize(ceil(temp * 2.5));
            drawTools["DRAWTOOL"]->drawPixel(*drawTool.get(), currentImage, mousePos.x - imageX - 140, mousePos.y - imageY - 90);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
            drawTool->setBrushSize(temp);
        } else if(drawToolsMap["PEN"]) {
            drawTools["DRAWTOOL"]->drawPixel(*drawTool.get(), currentImage, mousePos.x - imageX - 140, mousePos.y - imageY - 90);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        } else if (drawToolsMap["FILL"]) {
            drawTools["DRAWTOOL"]->drawPixel(fill, currentImage, mousePos.x - imageX - 140, mousePos.y - imageY - 90);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        } else if (drawToolsMap["ERASER"]) {
            drawTools["DRAWTOOL"]->drawPixel(eraser, currentImage, mousePos.x - imageX - 140, mousePos.y - imageY - 90);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        } else if (drawToolsMap["PIXEL"]) {
            int temp = drawTool->getBrushSize();
            drawTool->setBrushSize(ceil(temp * 0.25));
            drawTools["PIXEL"]->drawPixel(eraser, currentImage, mousePos.x - imageX - 140, mousePos.y - imageY - 90);
            drawTool->setBrushSize(temp);
            ImageOriginator.setImage(currentImage);
            Memento tempMemento = ImageOriginator.saveState();
            ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
        }
        imageTexture = LoadTextureFromImage(currentImage);
    }
}

void ImageEditor::DrawTextSubMenu() {
    // Verific daca click-ul a fost apasat inauntrul canvas-ului, respectiv pe poza
    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT) &&
        CheckCollisionPointRec(GetMousePosition(), { imageX + 140, imageY + 90, currentImage.width * 1.f, currentImage.height * 1.f })) {
        textX = GetMousePosition().x - imageX - 140;
        textY = GetMousePosition().y - imageY - 90;
    }
    GuiScrollPanel(textMenuBounds, NULL, textMenuContentBounds, &textMenuScrollPosition, &textMenuView);
    // Dreptunghiul fundalului
    DrawRectangleRoundedLines({ textMenuBounds.x + 15, textMenuBounds.y + textMenuScrollPosition.y + 30, 265, 2370 }, 0.15, 5, 1, DARKGRAY);
    // Titlul
    DrawTextEx(consolasFont, "Text", { textMenuBounds.x + 115, textMenuBounds.y + textMenuScrollPosition.y + 75 }, 25, 0.3f, RAYWHITE);
    buttons["BOLD"]->setX(textMenuBounds.x + 90);
    buttons["BOLD"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 125);
    if (buttons["BOLD"]->drawButton() == 0) {
        textOptions["BOLD"] = !textOptions["BOLD"];
    }
    buttons["ITALIC"]->setX(textMenuBounds.x + 130);
    buttons["ITALIC"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 125);
    if (buttons["ITALIC"]->drawButton() == 0) {
        textOptions["ITALIC"] = !textOptions["ITALIC"];
    }
    buttons["UNDERLINED"]->setX(textMenuBounds.x + 170);
    buttons["UNDERLINED"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 125);
    if (buttons["UNDERLINED"]->drawButton() == 0) {
        textOptions["UNDERLINED"] = !textOptions["UNDERLINED"];
    }
    DrawTextEx(consolasFont, "Font size: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 200 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textFontSize).c_str(), { textMenuBounds.x + 155, textMenuBounds.y + textMenuScrollPosition.y + 200 }, 20, 0.3f, RAYWHITE);
    buttons["FONTSIZEUP"]->setX(textMenuBounds.x + 190);
    buttons["FONTSIZEUP"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 190);
    if (buttons["FONTSIZEUP"]->drawButton() == 0) {
        textFontSize = std::min(textFontSize + 1, 85);
    }
    buttons["FONTSIZEDOWN"]->setX(textMenuBounds.x + 220);
    buttons["FONTSIZEDOWN"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 190);
    if (buttons["FONTSIZEDOWN"]->drawButton() == 0) {
        textFontSize = std::max(textFontSize - 1, 5);
    }
    DrawLine(textMenuBounds.x + 13, textMenuBounds.y + textMenuScrollPosition.y + 235, textMenuBounds.x + textMenuBounds.width - 20, textMenuBounds.y + textMenuScrollPosition.y + 235, DARKGRAY);
    DrawTextEx(consolasFont, "Current font: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 250 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Consolas", { textMenuBounds.x + 185, textMenuBounds.y + textMenuScrollPosition.y + 250 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Bold: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 290 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["BOLD"] ? "Yes" : "No"), { textMenuBounds.x + 100, textMenuBounds.y + textMenuScrollPosition.y + 290 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Italic: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 330 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["ITALIC"] ? "Yes" : "No"), { textMenuBounds.x + 120, textMenuBounds.y + textMenuScrollPosition.y + 330 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Underlined: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 370 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["UNDERLINED"] ? "Yes" : "No"), { textMenuBounds.x + 160, textMenuBounds.y + textMenuScrollPosition.y + 370 }, 20, 0.3f, RAYWHITE);
    DrawLine(textMenuBounds.x + 13, textMenuBounds.y + textMenuScrollPosition.y + 405, textMenuBounds.x + textMenuBounds.width - 20, textMenuBounds.y + textMenuScrollPosition.y + 405, DARKGRAY);
    DrawTextEx(consolasFont, "Current text:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 415 }, 20, 0.3f, RAYWHITE);
    GuiTextBox({ textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 455, 230, 30 }, const_cast<char*>(imageTextString.get()->c_str()), 50, true);

    DrawTextEx(consolasFont, "Text color:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 495 }, 20, 0.3f, RAYWHITE);
    GuiColorPicker({ textMenuBounds.x + 50, textMenuBounds.y + textMenuScrollPosition.y + 540, 150, 150 }, NULL, &currentTextColor);
    DrawTextEx(consolasFont, "R: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 720 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "G: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 750 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "B: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 780 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextColor.r).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 720 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextColor.g).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 750 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextColor.b).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 780 }, 22, 0.3f, RAYWHITE);

    DrawTextEx(consolasFont, "Text shadow color:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 820 }, 20, 0.3f, RAYWHITE);
    GuiColorPicker({ textMenuBounds.x + 50, textMenuBounds.y + textMenuScrollPosition.y + 870, 150, 150 }, NULL, &currentTextShadowColor);
    DrawTextEx(consolasFont, "R: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1050 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "G: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1080 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "B: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1110 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextShadowColor.r).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1050 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextShadowColor.g).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1080 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(currentTextShadowColor.b).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1110 }, 22, 0.3f, RAYWHITE);

    DrawTextEx(consolasFont, "Text border color:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1160 }, 20, 0.3f, RAYWHITE);
    GuiColorPicker({ textMenuBounds.x + 50, textMenuBounds.y + textMenuScrollPosition.y + 1210, 150, 150 }, NULL, &borderColor);
    DrawTextEx(consolasFont, "R: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1390 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "G: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1420 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "B: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1450 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(borderColor.r).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1390 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(borderColor.g).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1420 }, 22, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(borderColor.b).c_str(), { textMenuBounds.x + 65, textMenuBounds.y + textMenuScrollPosition.y + 1450 }, 22, 0.3f, RAYWHITE);

    DrawLine(textMenuBounds.x + 13, textMenuBounds.y + textMenuScrollPosition.y + 1500, textMenuBounds.x + textMenuBounds.width - 20, textMenuBounds.y + textMenuScrollPosition.y + 1500, DARKGRAY);
    DrawTextEx(consolasFont, "Text spacing:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1540 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textSpacing).c_str(), { textMenuBounds.x + 185, textMenuBounds.y + textMenuScrollPosition.y + 1540 }, 20, 0.3f, RAYWHITE);
    buttons["SPACINGUP"]->setX(textMenuBounds.x + 220);
    buttons["SPACINGUP"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 1530);
    if (buttons["SPACINGUP"]->drawButton() == 0) {
        textSpacing = std::min(textSpacing + 1, 85);
    }
    buttons["SPACINGDOWN"]->setX(textMenuBounds.x + 250);
    buttons["SPACINGDOWN"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 1530);
    if (buttons["SPACINGDOWN"]->drawButton() == 0) {
        textSpacing = std::max(textSpacing - 1, 5);
    }

    DrawTextEx(consolasFont, "Border type:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1580 }, 20, 0.3f, RAYWHITE);
    buttons["RECTANGULAR"]->setX(textMenuBounds.x + 70);
    buttons["RECTANGULAR"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 1620);
    if (buttons["RECTANGULAR"]->drawButton() == 0) {
        textOptions["RECTANGULAR"] = !textOptions["RECTANGULAR"];
        textOptions["ROUNDED"] = false;
        textBorderRadius = 1;
    }
    buttons["ROUNDED"]->setX(textMenuBounds.x + 75);
    buttons["ROUNDED"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 1680);
    if (buttons["ROUNDED"]->drawButton() == 0) {
        textOptions["ROUNDED"] = !textOptions["ROUNDED"];
        textOptions["RECTANGULAR"] = false;
    }
    DrawTextEx(consolasFont, "Rectangular: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1750 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["RECTANGULAR"] ? "Yes" : "No"), {textMenuBounds.x + 170, textMenuBounds.y + textMenuScrollPosition.y + 1750}, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Rounded: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1800 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["ROUNDED"] ? "Yes" : "No"), { textMenuBounds.x + 132, textMenuBounds.y + textMenuScrollPosition.y + 1800 }, 20, 0.3f, RAYWHITE);

    DrawLine(textMenuBounds.x + 13, textMenuBounds.y + textMenuScrollPosition.y + 1850, textMenuBounds.x + textMenuBounds.width - 20, textMenuBounds.y + textMenuScrollPosition.y + 1850, DARKGRAY);

    DrawTextEx(consolasFont, "Border style:", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 1900 }, 20, 0.3f, RAYWHITE);
    buttons["LINE"]->setX(textMenuBounds.x + 110);
    buttons["LINE"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 1940);
    if (buttons["LINE"]->drawButton() == 0) {
        textOptions["LINE"] = !textOptions["LINE"];
        textOptions["DOTTED"] = false;
    }
    buttons["DOTTED"]->setX(textMenuBounds.x + 105);
    buttons["DOTTED"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2000);
    if (buttons["DOTTED"]->drawButton() == 0) {
        textOptions["DOTTED"] = !textOptions["DOTTED"];
        textOptions["LINE"] = false;
    }
    DrawTextEx(consolasFont, "Line: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2070 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["LINE"] ? "Yes" : "No"), { textMenuBounds.x + 105, textMenuBounds.y + textMenuScrollPosition.y + 2070 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Dotted: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2120 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, (textOptions["DOTTED"] ? "Yes" : "No"), { textMenuBounds.x + 120, textMenuBounds.y + textMenuScrollPosition.y + 2120 }, 20, 0.3f, RAYWHITE);

    DrawTextEx(consolasFont, "Radius: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2170 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textBorderRadius).c_str(), {textMenuBounds.x + 125, textMenuBounds.y + textMenuScrollPosition.y + 2170}, 20, 0.3f, RAYWHITE);
    buttons["TBRADIUSUP"]->setX(textMenuBounds.x + 160);
    buttons["TBRADIUSUP"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2165);
    if (buttons["TBRADIUSUP"]->drawButton() == 0) {
        textBorderRadius = std::min(textBorderRadius + 1, 50);
    }
    buttons["TBRADIUSDOWN"]->setX(textMenuBounds.x + 190);
    buttons["TBRADIUSDOWN"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2165);
    if (buttons["TBRADIUSDOWN"]->drawButton() == 0) {
        textBorderRadius = std::max(textBorderRadius - 1, 1);
    }

    DrawTextEx(consolasFont, "B. Size: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2205 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textBorderSize).c_str(), { textMenuBounds.x + 145, textMenuBounds.y + textMenuScrollPosition.y + 2205 }, 20, 0.3f, RAYWHITE);
    buttons["TBSIZEUP"]->setX(textMenuBounds.x + 180);
    buttons["TBSIZEUP"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2200);
    if (buttons["TBSIZEUP"]->drawButton() == 0) {
        textBorderSize = std::min(textBorderSize + 1, 50);
    }
    buttons["TBSIZEDOWN"]->setX(textMenuBounds.x + 210);
    buttons["TBSIZEDOWN"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2200);
    if (buttons["TBSIZEDOWN"]->drawButton() == 0) {
        textBorderSize = std::max(textBorderSize - 1, 1);
    }

    DrawTextEx(consolasFont, "X: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2255 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textX).c_str(), {textMenuBounds.x + 60, textMenuBounds.y + textMenuScrollPosition.y + 2255}, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, "Y: ", { textMenuBounds.x + 35, textMenuBounds.y + textMenuScrollPosition.y + 2285 }, 20, 0.3f, RAYWHITE);
    DrawTextEx(consolasFont, std::to_string(textY).c_str(), { textMenuBounds.x + 60, textMenuBounds.y + textMenuScrollPosition.y + 2285 }, 20, 0.3f, RAYWHITE);
    buttons["PLACETEXT"]->setX(textMenuBounds.x + 110);
    buttons["PLACETEXT"]->setY(textMenuBounds.y + textMenuScrollPosition.y + 2315);
    if (buttons["PLACETEXT"]->drawButton() == 0) {
        imageText.setFontSize(textFontSize);
        imageText.setBolded(textOptions["BOLD"]);
        imageText.setItalic(textOptions["ITALIC"]);
        imageText.setUnderlined(textOptions["UNDERLINED"]);
        imageText.setText(*imageTextString.get());
        imageText.setTextColor(currentTextColor);
        imageText.setShadowColor(currentTextShadowColor);
        imageText.setBorderColor(borderColor);
        imageText.setSpacing(textSpacing);
        imageText.setBorderType(textOptions["RECTANGULAR"] ? BORDER_TYPE::RECTANGULAR : BORDER_TYPE::ROUNDED);
        imageText.setBorderLine(textOptions["LINE"] ? BORDER_LINE::LINE : BORDER_LINE::DOTTED);
        imageText.setRadius(textBorderRadius);
        imageText.setBorderSize(textBorderSize);
        imageText.setX(textX);
        imageText.setY(textY);
        imageText.setTextFont(consolasFont);
        imageText.drawTextTag(currentImage);
        imageTexture = LoadTextureFromImage(currentImage);
        ImageOriginator.setImage(currentImage);
        Memento tempMemento = ImageOriginator.saveState();
        ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
    }
}

void ImageEditor::applyImageFilter(int filterCode) {
    switch (filterCode) {
    case FILTER_TYPES::GRAYSCALE:
        filters["GRAYSCALE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::INVERTED:
        filters["INVERTED"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::FLIP:
        filters["FLIP"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::VERTICAL_FLIP:
        //filters["VERTICAL_FLIP"]->applyVerticalFilterToImage(imagePixels, currentImage.height, currentImage.width);
        static_cast<FlipFilter*>(filters["VERTICAL_FLIP"].get())->applyVerticalFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::LEFT_ROTATE:
        static_cast<RotateFilter*>(filters["LEFT_ROTATE"].get())->applyLeftFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::RIGHT_ROTATE:
        filters["RIGHT_ROTATE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::SHINE:
        filters["SHINE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::CHROME:
        filters["CHROME"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::FADE:
        filters["FADE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::DIFFOFGAUSSIANS:
        filters["DOG"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::ERODE:
        filters["ERODE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::FLARE:
        filters["FLARE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    case FILTER_TYPES::SPARKLE:
        filters["SPARKLE"]->applyFilterToImage(imagePixels, currentImage.height, currentImage.width);
        break;
    default:
        break;
    }
    ImageOriginator.setImage(currentImage);
    Memento tempMemento = ImageOriginator.saveState();
    ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
}

void ImageEditor::renderWindow() {
    while (!WindowShouldClose()) {
        moveWindow();
        // Mai intai actualizez state-ul box-ului de dialog
        if (fileDialog.SelectFilePressed) {
            // Verific ca fila selectat sa aibe o extensie corecta
            std::string extension = "";
            int i = strlen(fileDialog.fileNameText) - 1;
            while (fileDialog.fileNameText[i] != '.') {
                extension.push_back(fileDialog.fileNameText[i--]);
            }
            extension.push_back('.');
            std::reverse(extension.begin(), extension.end());
            if (acceptedExtensions.find(extension) != acceptedExtensions.end()) {
                // Pt linux: /
                if (pathToImage.size() == 0) {
                    pathToImage = TextFormat("%s\\%s", fileDialog.dirPathText, fileDialog.fileNameText);
                    std::string nameString = TextFormat(fileDialog.fileNameText);
                    imageName = std::make_shared<std::string>(nameString);
                    currentImage = LoadImage(pathToImage.c_str());
                    ImageFormat(&currentImage, PIXELFORMAT_UNCOMPRESSED_R8G8B8A8);
                    imageTexture = LoadTextureFromImage(currentImage);
                    Image dummyImage = ImageCopy(currentImage);
                    absoluteCopy = std::move(std::unique_ptr<Image>(&dummyImage));
                    imagePixels = LoadImageColors(currentImage);
                    ImageOriginator.setImage(currentImage);
                    Memento tempMemento = ImageOriginator.saveState();
                    ImageHistory.addMemento(tempMemento); imageIndex++; maxImageIndex++;
                    imageIndex = 0;
                }
            }
        }

        // Verific daca este o imagine pe canvas si daca nu este deschis vreun meniu
        // Pentru a "sincroniza" actiunile
        if (imagePixels) {
            float wheel = GetMouseWheelMove();
            if (wheel != 0 &&
                CheckCollisionPointRec(GetMousePosition(), { 140, 90, imageCanvas.texture.width * 1.f, imageCanvas.texture.height * 1.f })){
                imageScale += 0.1 * wheel;
                imageScale = std::max(imageScale, 0.1f);
                imageScale = std::min(imageScale, 4.0f);
            }
        }

        if (imagePixels) {
            // Calculez centrul texturii
            float xCenter = imageCanvas.texture.width / 2.f;
            float yCenter = imageCanvas.texture.height / 2.f;
            // Calculez noul size al imaginii raportat la scale-ul curent
            float newImageWidth = imageTexture.width * imageScale;
            float newImageHeight = imageTexture.height * imageScale;
            BeginTextureMode(imageCanvas);
            ClearBackground(backgroundColor);
            DrawTextureEx(imageTexture, { imageX = xCenter - (newImageWidth / 2.f), imageY = yCenter - (newImageHeight / 2.f)}, 0, imageScale, WHITE);
            EndTextureMode();
        }

        if (editSubmenus["ARRANGE"]) {
            DrawArrangeSubMenu();
        } else if (editSubmenus["SN_CROP"]) {
            DrawCropSubMenu();
        } else if (editSubmenus["ADJUST"]) {
            DrawAdjustSubMenu();
        } else if (editSubmenus["SN_FILTER"]) {
            DrawFilterSubMenu();
        } else if (editSubmenus["AI_TOOLS"]) {
            DrawAiToolsSubMenu();
        } else if (editSubmenus["DRAW"]) {
            DrawDrawSubMenu();
        } else if (editSubmenus["TEXT"]) {
            DrawTextSubMenu();
        }

        BeginDrawing();
        
        ClearBackground(backgroundColor);
       
        DrawTexturePro(imageCanvas.texture, { 0.0f, 0.0f, imageCanvas.texture.width * 1.f, imageCanvas.texture.height * -1.f },
            { 140.f, 90.f, 1415.f, 785.f},
            { 0, 0 }, 0.0f, WHITE);
        // Text, x, y, fontSize, color
        DrawTextureEx(appIconTexture, { 15, 5 }, 0, 0.05, WHITE);
        if (pathToImage.size() > 0) {
            std::string tempTitle = "Image Editor -" + pathToImage;
            DrawTextEx(consolasFont, tempTitle.c_str(), { 50, 15 }, 20, 0.3f, RAYWHITE);
        }
        else {
            DrawTextEx(consolasFont, "Image Editor", { 50, 15 }, 20, 0.3f, RAYWHITE);
        }
        DrawMenuBar();
        DrawLine(0, 91, windowWidth, 91, RAYWHITE);
        DrawLine(140, 875, 1550, 875, RAYWHITE);
        DrawSideBar();
        DrawLine(140, 91, 140, 875, RAYWHITE);
        DrawLine(1550, 91, 1550, 875, RAYWHITE);
        std::string tempText = std::to_string(imageTexture.width) + " x " + std::to_string(imageTexture.height) + " px";
        DrawTextEx(consolasFont, tempText.c_str(), { 250, 900 }, 20, 0.3f, RAYWHITE);

        if (dropdownMenus["FILE"]) {
            int active = -1;
            int item = GuiDropdownBox({ 12, 80, 50, 50 }, "Open file;New file;Save file", &active, true);

            std::string nameString;
            Image dummy;
            switch (item) {
            case 0: // Open file
                fileDialog.windowActive = !fileDialog.windowActive;
                break;
             case 1:
                if (pathToImage.size() > 0) {

                }
                pathToImage = "\\Untitled.png";
                nameString = "Untitled.png";
                imageName = std::make_shared<std::string>(nameString);
                currentImage = GenImageColor(1024, 512, BLANK);
                ImageFormat(&currentImage, PIXELFORMAT_UNCOMPRESSED_R8G8B8A8);
                imageTexture = LoadTextureFromImage(currentImage);
                dummy = ImageCopy(currentImage);
                absoluteCopy = std::move(std::unique_ptr<Image>(&dummy));
                break;
            case 2:
                if (pathToImage.size() > 0) {
                    std::string separator = "\\";
                    std::string imageSavePath = fileDialog.dirPathText + separator + imageName.get()->c_str();
                    if (ExportImage(LoadImageFromTexture(imageTexture), imageSavePath.c_str())) {
                        pathToImage = imageSavePath;
                    }
                }
                break;
            }
        } else if (dropdownMenus["EDIT"]) {
            int active = -1;
            int item = GuiDropdownBox({ 83, 80, 50, 50 }, "Undo;Redo;Copy;Paste", &active, true);
        } else if (dropdownMenus["VIEW"]) {
            int active = -1;
            int item = GuiDropdownBox({ 160, 80, 50, 50 }, "Zoom in;Zoom out;Image border;Grid display;Fit to window", &active, true);
        } else if (dropdownMenus["IMAGE"]) {
            int active = -1;
            int item = GuiDropdownBox({ 235, 80, 50, 50 }, "Undo;Redo;Copy;Paste", &active, true);
        } else if (dropdownMenus["HELP"]) {
            // pop-up
        }

        DrawRectangleLines(GetScreenWidth() / 2 - imageTexture.width / 2, GetScreenHeight() / 2 - imageTexture.height / 2 - 5, imageTexture.width, imageTexture.height, BLANK);

        if (fileDialog.windowActive) GuiLock();

        if (!IsWindowMinimized()) {
            GuiUnlock();
        }
        GuiWindowFileDialog(&fileDialog);

        if (buttons["SAVE"]->drawButton() == 0 && pathToImage.size() > 0) {
            std::string separator = "\\";
            std::string imageSavePath = fileDialog.dirPathText + separator + imageName.get()->c_str();
            if (ExportImage(LoadImageFromTexture(imageTexture), imageSavePath.c_str())) {
                pathToImage = imageSavePath;
            }
        }
        if (buttons["RESET"]->drawButton() == 0 && pathToImage.size() > 0) {
            
            currentImage = *absoluteCopy.get();
            setAdjustSliderValues();
            imagePixels = LoadImageColors(currentImage);
            UpdateTexture(imageTexture, imagePixels);
        }
        if (buttons["UNDO"]->drawButton() == 0 && pathToImage.size() > 0) {
            try {
                Memento tempMemento = ImageHistory.getMemento(imageIndex--);
                ImageOriginator.restoreFromMemento(tempMemento);
                currentImage = ImageOriginator.getImage();
                imagePixels = LoadImageColors(currentImage);
                UpdateTexture(imageTexture, imagePixels);
                if (imageIndex < 0) imageIndex = 0;
            } catch (...) {
                std::cout << "Nu s-a putut da undo!\n";
            }
        }
        if (buttons["REDO"]->drawButton() == 0 && pathToImage.size() > 0) {
            try {
                Memento tempMemento = ImageHistory.getMemento(imageIndex++);
                ImageOriginator.restoreFromMemento(tempMemento);
                currentImage = ImageOriginator.getImage();
                imagePixels = LoadImageColors(currentImage);
                UpdateTexture(imageTexture, imagePixels);
                if (imageIndex > maxImageIndex) imageIndex = maxImageIndex;
            } catch(...) {
                std::cout << "Nu s-a putut da redo!\n";
            }
        }

        // Dummy rectangle pentru scroll interaction
        DrawRectangle(1550, 0, 300, 90, backgroundColor);

        if (buttons["CLOSE"]->drawButton() == 0) {
            CloseWindow();
            return;
        }
        else if (buttons["MINIMIZE"]->drawButton() == 0) {
            MinimizeWindow();
            GuiLock();
        }

        EndDrawing();
    }
    CloseWindow();
}

ImageEditor& ImageEditor::operator=(const ImageEditor& imageEditor) {
    maxFps = imageEditor.maxFps;
    windowWidth = imageEditor.windowWidth;
    windowHeight = imageEditor.windowHeight;
    backgroundColor = imageEditor.backgroundColor;
    return *this;
}
