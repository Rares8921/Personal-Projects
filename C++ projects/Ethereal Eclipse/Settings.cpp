#include "Settings.h"
#include "Game.h"

// Functii statice

// Functii de initializare

void Settings::initWindow() {
    // sf::Style::Titlebar | sf::Style::Close - aplicatia nu-si poate schimba marimea
    settingsWindow = new sf::RenderWindow(sf::VideoMode(1311, 805), "Settings menu", sf::Style::Titlebar | sf::Style::Close);
    // Pentru ca jucatorul sa nu tina apasat si sa se considere ca fiind spam de atacuri
    settingsWindow->setKeyRepeatEnabled(false);
    settingsWindow->setFramerateLimit(getFramerate());
    settingsWindow->setVerticalSyncEnabled(getVSync()); // v-sync off
}

void Settings::initContent() {
    // Setez imaginea de fundal
    if (!backgroundTexture.loadFromFile("Textures/startMenuBackground.jpg")) {
        std::cout << "Imaginea de fundal a meniului de settings nu a putut fi incarcata!";
    } else {
        backgroundSprite.setTexture(backgroundTexture);
    }
    sf::Vector2u appSize = settingsWindow -> getSize();
    if (!menuTexture.loadFromFile("Textures/settingsSectionBackground.png")) {
        std::cout << "Meniul din settings nu a putut fi initializat!";
    } else {
        menuTexture.setSmooth(true);
        menuSprite.setTexture(menuTexture);
        menuSprite.setPosition(appSize.x / 4.40f, appSize.y / 15.0f);
    }
    if (!titleTexture.loadFromFile("Textures/settings.png")) {
        std::cout << "Titlul din settings nu a putut fi initializat!";
    }  else {
        titleTexture.setSmooth(true);
        titleSprite.setTexture(titleTexture);
        titleSprite.setScale(0.5, 0.5);
        titleSprite.setPosition(535, 140);
    }

    if (!icelandFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
        std::cout << "Font-ul Iceland nu a putut fi incarcat in meniul Settings!";
    }
    musicVolumeText.setFont(icelandFont);
    musicVolumeText.setString("Music volume:");
    musicVolumeText.setCharacterSize(35);
    musicVolumeText.setPosition(360, 240);

    gameVolumeText.setFont(icelandFont);
    gameVolumeText.setString("Game volume:");
    gameVolumeText.setCharacterSize(35);
    gameVolumeText.setPosition(360, 340);
    
    vSyncText.setFont(icelandFont);
    vSyncText.setString("VSync:");
    vSyncText.setCharacterSize(35);
    vSyncText.setPosition(360, 440);

    frameRateText.setFont(icelandFont);
    frameRateText.setString("Framerate:");
    frameRateText.setCharacterSize(35);
    frameRateText.setPosition(360, 540);

    antiAliasingText.setFont(icelandFont);
    antiAliasingText.setString("Antialiasing:");
    antiAliasingText.setCharacterSize(35);
    antiAliasingText.setPosition(360, 640);

    // Incarc slider-ul pentru music volume
    musicVolumeSlider = GUI::Slider(580, 267.5);
    musicVolumeSlider.create(0, 100);
    musicVolumeSlider.setSliderValue(getMusicVolume());
    gameVolumeSlider = GUI::Slider(580.0, 367.5);
    gameVolumeSlider.create(0, 100);
    gameVolumeSlider.setSliderValue(getGameVolume());
    frameRateSlider = GUI::Slider(540.0, 567.5);
    frameRateSlider.create(30, 180);
    std::cout << getFramerate() << "\n";
    frameRateSlider.setSliderValue(getFramerate());
}

void Settings::initButtons() {
    vSyncButton = new GUI::Button(475, 440, 85, 55, icelandFont, getVSync() ? "True" : "False", 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100));

    antiAliasingButton = new GUI::Button(555, 640, 85, 55, icelandFont, getAntiAliasing() ? "True" : "False", 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100));
}

void Settings::saveToFile() {
    std::ofstream fout;
    fout.open("Config/window.ini");
    if (fout.is_open()) {
        fout << musicVolumeSlider.getSliderValue() << "\n";
        fout << gameVolumeSlider.getSliderValue() << "\n";
        fout << (vSyncButton->getText() == "True") << "\n";
        fout << frameRateSlider.getSliderValue() << "\n";
        fout << (antiAliasingButton->getText() == "True");
    }
    fout.close();
}

// Constructor
Settings::Settings() {
    initWindow();
    initContent();
    initButtons();
}

// DestructorW
Settings::~Settings() {
    delete settingsWindow;
}

// Functii

void Settings::updateEvents() {
    while (settingsWindow->pollEvent(windowEvent)) {
        if (windowEvent.type == sf::Event::Closed) {
            saveToFile();
            settingsWindow->close();
            Game game;
            game.runApplication();
        }
    }
}

void Settings::updateWindow() {
    updateEvents();
}

void Settings::renderWindow() {
    settingsWindow->clear();
    // Render continutului
    // Prima oara dau render la "lume"
    settingsWindow->draw(backgroundSprite);
    // Adaugarea meniului
    settingsWindow->draw(menuSprite);
    settingsWindow->draw(titleSprite);
    settingsWindow->draw(musicVolumeText);
    musicVolumeSlider.renderSlide(*settingsWindow);
    settingsWindow->draw(gameVolumeText);
    gameVolumeSlider.renderSlide(*settingsWindow);
    settingsWindow->draw(vSyncText);

    vSyncButton->updateButton(sf::Mouse::getPosition(*settingsWindow));
    vSyncButton->renderButton(*settingsWindow);

    settingsWindow->draw(frameRateText);
    frameRateSlider.renderSlide(*settingsWindow);
    settingsWindow->draw(antiAliasingText);

    antiAliasingButton->updateButton(sf::Mouse::getPosition(*settingsWindow));
    antiAliasingButton->renderButton(*settingsWindow);
    settingsWindow->display();
}

void Settings::runSettingsMenu() {
    while (settingsWindow->isOpen()) {
        updateWindow();
        renderWindow();
    }
}

Settings& Settings::operator=(const Settings& other) {
    if (this != &other) {
        settingsWindow = other.settingsWindow;
        backgroundTexture = other.backgroundTexture;
        menuTexture = other.menuTexture;
        titleTexture = other.titleTexture;
        backgroundSprite = other.backgroundSprite;
        menuSprite = other.menuSprite;
        titleSprite = other.titleSprite;
        windowEvent = other.windowEvent;
        musicVolumeText = other.musicVolumeText;
        gameVolumeText = other.gameVolumeText;
        vSyncText = other.vSyncText;
        frameRateText = other.frameRateText;
        antiAliasingText = other.antiAliasingText;
        icelandFont = other.icelandFont;
        musicVolumeSlider = other.musicVolumeSlider;
        gameVolumeSlider = other.gameVolumeSlider;
        frameRateSlider = other.frameRateSlider;
        if (other.vSyncButton != nullptr) {
            vSyncButton = new GUI::Button(*other.vSyncButton);
        }
        else {
            vSyncButton = nullptr;
        }
        if (other.antiAliasingButton != nullptr) {
            antiAliasingButton = new GUI::Button(*other.antiAliasingButton);
        }
        else {
            antiAliasingButton = nullptr;
        }
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const Settings& settings) {
    return out;
}
