#include "GameRun.h"
#include "Game.h"
#include "Level.h"

void GameRun::initWindow() {
    // sf::Style::Titlebar | sf::Style::Close - aplicatia nu-si poate schimba marimea
    gameWindow = new sf::RenderWindow(sf::VideoMode(1269, 835), "Game menu");
    // Pentru ca jucatorul sa nu tina apasat si sa se considere ca fiind spam de atacuri
    gameWindow->setKeyRepeatEnabled(false);
    gameWindow->setFramerateLimit(getFramerate());
    gameWindow->setVerticalSyncEnabled(getVSync()); // v-sync off
}

void GameRun::initContent() {
    if (!gameMapTexture.loadFromFile("Textures/mapBackground.png")) {
        std::cout << "Imaginea de fundal a meniului de start nu a putut fi incarcata!\n";
    } else {
        gameMapSprite.setTexture(gameMapTexture);
    }
    if (!zoneTexture.loadFromFile("Textures/mapMarker.png")) {
        std::cout << "Imaginile marker-elor zonelor accesibile nu au putut fi incarcate!\n";
    } else {
        zoneOneSprite.setTexture(zoneTexture);
        zoneOneSprite.setScale(0.1f, 0.1f);
        zoneOneSprite.setPosition(795, 325);
        if (!innacessibleZoneTexture.loadFromFile("Textures/mapMarker1.png")) {
            std::cout << "Imaginile marker-elor zonelor neaccesibile nu au putut fi incarcate!\n";
        } else {
            zoneTwoSprite.setTexture(zoneTexture);
            zoneTwoSprite.setScale(0.1f, 0.1f);
            zoneTwoSprite.setPosition(795, 213);

            zoneThreeSprite.setTexture(zoneTexture);
            zoneThreeSprite.setScale(0.1f, 0.1f);
            zoneThreeSprite.setPosition(476, 203);

            zoneFourSprite.setTexture(zoneTexture);
            zoneFourSprite.setScale(0.1f, 0.1f);
            zoneFourSprite.setPosition(128, 395);

            zoneFiveSprite.setTexture(zoneTexture);
            zoneFiveSprite.setScale(0.1f, 0.1f);
            zoneFiveSprite.setPosition(1030, 677);

            zoneSixSprite.setTexture(zoneTexture);
            zoneSixSprite.setScale(0.1f, 0.1f);
            zoneSixSprite.setPosition(1144, 92);

            zoneSevenSprite.setTexture(zoneTexture);
            zoneSevenSprite.setScale(0.1f, 0.1f);
            zoneSevenSprite.setPosition(240, 683);

            zoneEightSprite.setTexture(zoneTexture);
            zoneEightSprite.setScale(0.1f, 0.1f);
            zoneEightSprite.setPosition(564, 504);
        }
    }
}

GameRun::GameRun() {
    initWindow();
    initContent();
}

GameRun::~GameRun() {
    delete gameWindow;
}

void GameRun::updateEvents() {
    while (gameWindow->pollEvent(gameWindowEvent)) {
        if (gameWindowEvent.type == sf::Event::Closed) {
            // Inchid jocul
            gameWindow->close();
            // Si deschid meniul principal
            Game game;
            game.runApplication();
        }
        // Mai intai gasesc pozitia mouse-ului pe ecran
        // Si dupa o convertesc la coordonate ale aplicatiei
        sf::Vector2i pozitieGlobala = sf::Mouse::getPosition();
        sf::Vector2f pozitieInAplicatie = gameWindow->mapPixelToCoords(pozitieGlobala);
        // Acum pentru ca am coordonatele in functie de tot ecranul, trebuie sa scad coordonatele din
        // Logica e asemanatoare cu sumele partiale pe matrice
        pozitieInAplicatie.x -= gameWindow->getPosition().x;
        pozitieInAplicatie.y -= gameWindow->getPosition().y;
        if (isSpriteClicked(gameWindow, zoneOneSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(1, "Abraxus' Temple");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneTwoSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(2, "Fort Ruins");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneThreeSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(3, "Drakus Sector");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneFourSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(4, "Sunkein's Lair");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneFiveSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(5, "Arkal District");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneSixSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(6, "Zenan's Underground");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneSevenSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(7, "Axenburg Island");
            level.runLevel();
        } else if (isSpriteClicked(gameWindow, zoneSevenSprite, pozitieInAplicatie)) {
            gameWindow->close();
            Level level(8, "Kajar's Mountains");
            level.runLevel();
        }
    }
}

void GameRun::updateWindow() {
    updateEvents();
}

void GameRun::renderWindow() {
    gameWindow->clear();
    gameWindow->draw(gameMapSprite);
    gameWindow->draw(zoneOneSprite);
    //gameWindow->draw(zoneTwoSprite);
    gameWindow->draw(zoneThreeSprite);
    gameWindow->draw(zoneFourSprite);
    gameWindow->draw(zoneFiveSprite);
    gameWindow->draw(zoneSixSprite);
    gameWindow->draw(zoneSevenSprite);
    gameWindow->draw(zoneEightSprite);
    gameWindow->display();
}

void GameRun::runGameMenu() {
    while (gameWindow->isOpen()) {
        updateWindow();
        renderWindow();
    }
}

GameRun& GameRun::operator=(const GameRun& other) {
    if (this != &other) {
        gameWindow = other.gameWindow;
        gameMapTexture = other.gameMapTexture;
        zoneTexture = other.zoneTexture;

        gameMapSprite = other.gameMapSprite;
        zoneOneSprite = other.zoneOneSprite;
        zoneTwoSprite = other.zoneTwoSprite;

        gameWindowEvent = other.gameWindowEvent;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const GameRun& gameRun) {
    out << "Game Window: " << gameRun.gameWindow << "\n";
    return out;
}
