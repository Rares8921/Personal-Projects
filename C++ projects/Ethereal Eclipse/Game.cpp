#include "Game.h"
#include "Controls.h"
#include "Settings.h"
#include "GameRun.h"

// Functii statice

// Functii de initializare

void Game::initWindow() {
    // sf::Style::Titlebar | sf::Style::Close - aplicatia nu-si poate schimba marimea
	applicationWindow = new sf::RenderWindow(sf::VideoMode(1311, 805), "Ethereal Eclipse", sf::Style::Titlebar | sf::Style::Close);
    // Pentru ca jucatorul sa nu tina apasat si sa se considere ca fiind spam de atacuri
    applicationWindow->setKeyRepeatEnabled(false);
    applicationWindow->setFramerateLimit(getFramerate());
    applicationWindow->setVerticalSyncEnabled(getVSync()); // v-sync off
}

void Game::initContent() {
    // Setez imaginea de fundal
    if (!backgroundTexture.loadFromFile("Textures/startMenuBackground.jpg")) {
        std::cout << "Imaginea de fundal a meniului de start nu a putut fi incarcata!";
    } else {
        backgroundSprite.setTexture(backgroundTexture);
    }
    sf::Vector2u appSize = applicationWindow->getSize();
    // Initializare meniu
    if (!menuTexture.loadFromFile("Textures/startSectionBackground.png")) {
        std::cout << "Meniul nu a putut fi initializat!";
    } else {
        menuTexture.setSmooth(true);
        menuSprite.setTexture(menuTexture);
        menuSprite.setPosition(appSize.x / 3.5f, appSize.y / 15.0f);
    }
    // Setez mesajul meniului
     if (!gameTitleTexture.loadFromFile("Textures/Ethereal-Eclipse.png")) {
        std::cout << "Imaginea titlului nu a putut fi incarcata!";
    } else {
        gameTitleTexture.setSmooth(true);
        gameTitleSprite.setTexture(gameTitleTexture);
        gameTitleSprite.setScale(0.5, 0.5);
        gameTitleSprite.setPosition(450, 140);
    }
    // Incarc pozele corespunzatoare fiecarui buton
    if (!playButtonTexture.loadFromFile("Textures/playButton.png")) {
        std::cout << "Imaginea de fundal a butonului de play nu a putut fi incarcata!";
    } else {
        playButtonSprite.setTexture(playButtonTexture);
        playButtonSprite.setPosition(540, 270);
    }

    if (!charactersButtonTexture.loadFromFile("Textures/controlsButton.png")) {
        std::cout << "Imaginea de fundal a butonului sectiunii controls nu a putut fi incarcata!";
    } else {
        charactersButtonSprite.setTexture(charactersButtonTexture);
        charactersButtonSprite.setPosition(540, 370);
    }

    if (!settingsButtonTexture.loadFromFile("Textures/settingsButton.png")) {
        std::cout << "Imaginea de fundal a butonului de settings nu a putut fi incarcata!";
    } else {
        settingButtonSprite.setTexture(settingsButtonTexture);
        settingButtonSprite.setPosition(540, 470);
    }

    if (!exitButtonTexture.loadFromFile("Textures/exitButton.png")) {
        std::cout << "Imaginea de fundal a butonului de exit nu a putut fi incarcata!";
    } else {
        exitButtonSprite.setTexture(exitButtonTexture);
        exitButtonSprite.setPosition(540, 570);
    }
}

// Constructor
Game::Game() {
    initWindow();
    initContent();
}

// DestructorW
Game::~Game() {
	delete applicationWindow;
}

// Functii

void Game::updateEvents() {
    while (applicationWindow->pollEvent(windowEvent)) {
        if (windowEvent.type == sf::Event::Closed) {
            applicationWindow->close();
        }
        // Mai intai gasesc pozitia mouse-ului pe ecran
        // Si dupa o convertesc la coordonate ale aplicatiei
        sf::Vector2i pozitieGlobala = sf::Mouse::getPosition();
        sf::Vector2f pozitieInAplicatie = applicationWindow->mapPixelToCoords(pozitieGlobala);
        // Acum pentru ca am coordonatele in functie de tot ecranul, trebuie sa scad coordonatele din
        // Logica e asemanatoare cu sumele partiale pe matrice
        pozitieInAplicatie.x -= applicationWindow->getPosition().x;
        pozitieInAplicatie.y -= applicationWindow->getPosition().y;
        if (isSpriteClicked(applicationWindow, playButtonSprite, pozitieInAplicatie)) {
            std::cout << "Play\n";
            applicationWindow->close();
            GameRun game;
            game.runGameMenu();
        } else if (isSpriteClicked(applicationWindow, charactersButtonSprite, pozitieInAplicatie)) {
            applicationWindow->close();
            Controls controlsMenu;
            controlsMenu.runControlsMenu();
            std::cout << "Characters\n";
        } else if (isSpriteClicked(applicationWindow, settingButtonSprite, pozitieInAplicatie)) {
            applicationWindow->close();
            Settings settingsMenu;
            settingsMenu.runSettingsMenu();
            std::cout << "Settings\n";
        } else if (isSpriteClicked(applicationWindow, exitButtonSprite, pozitieInAplicatie)) {
            std::cout << "Exit\n";
            applicationWindow->close();
        } else {
            sf::Cursor arrowCursor;
            if (!arrowCursor.loadFromSystem(sf::Cursor::Arrow)) {
                std::cout << "Cursorul Arrow nu a putut fi incarcat din sistem!";
            }
            applicationWindow->setMouseCursor(arrowCursor);
        }
    }
}

void Game::updateWindow() {
    updateEvents();
}

void Game::renderWindow() const {
    applicationWindow->clear();
    // Render continutului
    // Prima oara dau render la "lume"
    applicationWindow->draw(backgroundSprite);
    // Adaugarea meniului
    applicationWindow->draw(menuSprite);
    applicationWindow->draw(gameTitleSprite);
    applicationWindow->draw(playButtonSprite);
    applicationWindow->draw(charactersButtonSprite);
    applicationWindow->draw(settingButtonSprite);
    applicationWindow->draw(exitButtonSprite);
    applicationWindow->display();
}

void Game::runApplication() {
    while(applicationWindow->isOpen()) {
        updateWindow();
        renderWindow();
    }
}

Game& Game::operator=(const Game& other) {
    if (this != &other) {
        applicationWindow = other.applicationWindow;
        backgroundTexture = other.backgroundTexture;
        gameTitleTexture = other.gameTitleTexture;
        menuTexture = other.menuTexture;
        playButtonTexture = other.playButtonTexture;
        charactersButtonTexture = other.charactersButtonTexture;
        settingsButtonTexture = other.settingsButtonTexture;
        exitButtonTexture = other.exitButtonTexture;
        backgroundSprite = other.backgroundSprite;
        gameTitleSprite = other.gameTitleSprite;
        menuSprite = other.menuSprite;
        playButtonSprite = other.playButtonSprite;
        charactersButtonSprite = other.charactersButtonSprite;
        settingButtonSprite = other.settingButtonSprite;
        exitButtonSprite = other.exitButtonSprite;
        windowEvent = other.windowEvent;
        isHovered = other.isHovered;
        isActive = other.isActive;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const Game& game) {
    out << "Application Window: " << game.applicationWindow << "\n";
    return out;
}
