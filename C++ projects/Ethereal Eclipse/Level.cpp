#include "GameRun.h"
#include "Level.h"

void Level::initWindow() {
    std::ifstream windowConfig("Config/window.ini");

    // sf::Style::Titlebar | sf::Style::Close - aplicatia nu-si poate schimba marimea
    levelWindow = new sf::RenderWindow(sf::VideoMode(1300, 850), zoneTitle, sf::Style::Titlebar | sf::Style::Close);
    appSize = static_cast<sf::Vector2f>(levelWindow->getSize());
    mapSize = sf::Vector2f(1300, 850);
    // Pentru ca jucatorul sa nu tina apasat si sa se dconsidere ca fiind spam de atacuri
    levelWindow->setKeyRepeatEnabled(false);
    levelWindow->setFramerateLimit(GeneralFunctions::getFramerate());
    levelWindow->setVerticalSyncEnabled(GeneralFunctions::getVSync()); // v-sync off
}

void Level::initContent() {
    switch (level) {
        case 1:
            if (!gameMapTexture.loadFromFile("Textures/zone1.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            } else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(4, 4);
            }
            break;
        case 2:
            if (!gameMapTexture.loadFromFile("Textures/zone3.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(4.5, 4.5);
            }
            break;
        case 3:
            if (!gameMapTexture.loadFromFile("Textures/zone2.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(3, 3);
            }
            break;
        case 4:
            if (!gameMapTexture.loadFromFile("Textures/zone4.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(3, 3);
            }
            break;
        case 5:
            if (!gameMapTexture.loadFromFile("Textures/zone5.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(1.5, 1.5);
            }
            break;
        case 6:
            if (!gameMapTexture.loadFromFile("Textures/zone6.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(4, 4);
            }
            break;
        case 7:
            if (!gameMapTexture.loadFromFile("Textures/zone7.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(1.5, 1.5);
            }
            break;
        case 8:
            if (!gameMapTexture.loadFromFile("Textures/zone8.png")) {
                std::cout << "Imaginea de fundal a nivelului nu a putut fi incarcata!";
            }
            else {
                gameMapTexture.setSmooth(true);
                gameMapSprite.setTexture(gameMapTexture);
                gameMapSprite.setScale(1.5, 1.5);
            }
            break;
        default:
            break;
    }
    mapView = new sf::View(sf::FloatRect(0.f, 0.f, 1300.f, 850.f));
    levelWindow->setView(*mapView);
}

void Level::initStates() {
    sf::VideoMode vm = sf::VideoMode(1300.f, 850.f);
    stackState.push(new GameState(56, levelWindow, vm, &supportedKeys, &stackState, level));
}

void Level::initKeys() {   
    std::array<int, 10> codes = GeneralFunctions::getKeyCodes();
    supportedKeys.emplace("A", sf::Keyboard::Key(codes[0]));
    supportedKeys.emplace("D", sf::Keyboard::Key(codes[1]));
    supportedKeys.emplace("W", sf::Keyboard::Key(codes[2]));
    supportedKeys.emplace("S", sf::Keyboard::Key(codes[3]));
    supportedKeys.emplace("ESC", sf::Keyboard::Key(codes[4]));
    supportedKeys.emplace("P", sf::Keyboard::Key(codes[5]));
    supportedKeys.emplace("TAB", sf::Keyboard::Key(codes[6]));
    supportedKeys.emplace("I", sf::Keyboard::Key(codes[7]));
    supportedKeys.emplace("SPC", sf::Keyboard::Key(codes[8]));
}

Level::Level(int level, std::string zoneTitle) : level(level), zoneTitle(zoneTitle) {
    initWindow();
    initKeys();
    initStates();
    initContent();
}

Level::~Level() {
    delete levelWindow;
    delete mapView;
    while (!stackState.empty()) {
        delete stackState.top();
        stackState.pop();
    }
}

void Level::updateEvents() {
    while (levelWindow->pollEvent(levelWindowEvent)) {
        if (levelWindowEvent.type == sf::Event::Closed) {
            // Inchid jocul
            levelWindow->close();
            // Si deschid meniul principal
            GameRun game;
            game.runGameMenu();
        }
    }
}

void Level::updateWindow() {
    updateEvents();
    if (!stackState.empty()) {
        stackState.top()->updateState(deltaTime);
    }
}

void Level::renderWindow() {
    levelWindow->clear();
    // Render continutului
    levelWindow->draw(gameMapSprite);
    if (GeneralFunctions::getAntiAliasing()) {
        sf::RenderTexture renderTexture;
        renderTexture.create(levelWindow->getSize().x * 2, levelWindow->getSize().y * 2);
        renderTexture.clear(sf::Color::Color(255, 255, 255, 0));
        renderTexture.display();
        sf::Sprite sprite(renderTexture.getTexture());
        sprite.setScale(sf::Vector2f(0.5f, 0.5f));
        sprite.setTextureRect(sf::IntRect(0, 0, levelWindow->getSize().x, levelWindow->getSize().y));
        levelWindow->draw(sprite);
    }
    if (!stackState.empty() && levelWindow->hasFocus()) {
        unpauseState();
        stackState.top()->renderState(levelWindow);
        if (stackState.top()->getQuitValue()) {
            // TODO: o animatie sau un eveniment inainte de a se incheia state-ul
            stackState.top()->endState();
            delete stackState.top();
            stackState.pop();
        }
    }
    else { // In momentul in care aplicatia nu mai are state-uri inseamna ca trebuie inchisa
        //pauseState();
        levelWindow->close();
    }
    //updateMapView();
    //mapView -> move(0.f, 0.1f);
    //levelWindow->setView(*mapView);
    levelWindow->display();
}

void Level::runLevel() {
    while (levelWindow->isOpen()) {
        deltaTime = GeneralFunctions::updateDeltaTime(deltaTimeClock);
        deltaTimeClock.restart();
        updateWindow();
        renderWindow();
    }
}

Level& Level::operator=(const Level& other) {
    if (this != &other) {
        // Copierea membrilor clasici
        mapView = other.mapView;
        levelWindow = other.levelWindow;
        gameMapTexture = other.gameMapTexture;
        level1ButtonTexture = other.level1ButtonTexture;
        gameMapSprite = other.gameMapSprite;
        level1ButtonSprite = other.level1ButtonSprite;
        levelWindowEvent = other.levelWindowEvent;
        stackState = other.stackState;
        supportedKeys = other.supportedKeys;
        deltaTimeClock = other.deltaTimeClock;
        deltaTime = other.deltaTime;
        appSize = other.appSize;
        mapSize = other.mapSize;
        level = other.level;
        zoneTitle = other.zoneTitle;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const Level& level) {
    out << "Level: " << level.level << "\n";
    out << "Zone Title: " << level.zoneTitle << "\n";
    return out;
}
