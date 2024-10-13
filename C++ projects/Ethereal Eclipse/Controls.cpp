#include "Controls.h"
#include "Game.h"

// Functii de initializare

void Controls::saveToFile() {
    std::ofstream fout("Config/keys.ini");
    if (fout.is_open()) {
        fout << moveLeftButton->getButtonKeyCode() << "\n";
        fout << moveRightButton->getButtonKeyCode() << "\n";
        fout << moveUpButton->getButtonKeyCode() << "\n";
        fout << moveDownButton->getButtonKeyCode() << "\n";
        fout << closeButton->getButtonKeyCode() << "\n";
        fout << pauseButton->getButtonKeyCode() << "\n";
        fout << tabButton->getButtonKeyCode() << "\n";
        fout << inventoryButton->getButtonKeyCode() << "\n";
        fout << jumpButton->getButtonKeyCode();
    }
    fout.close();
}

void Controls::initWindow() {
    // sf::Style::Titlebar | sf::Style::Close - aplicatia nu-si poate schimba marimea
    controlsWindow = new sf::RenderWindow(sf::VideoMode(1311, 805), "Controls menu", sf::Style::Titlebar | sf::Style::Close);
    // Pentru ca jucatorul sa nu tina apasat si sa se considere ca fiind spam de atacuri
    controlsWindow->setKeyRepeatEnabled(false);
    controlsWindow->setFramerateLimit(getFramerate());
    controlsWindow->setVerticalSyncEnabled(getVSync()); // v-sync off
}

void Controls::initKeys() {
    std::array<int, 10> codes = getKeyCodes();
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

void Controls::initContent() {
    // Setez imaginea de fundal
    if (!backgroundTexture.loadFromFile("Textures/startMenuBackground.jpg")) {
        std::cout << "Imaginea de controls a meniului de settings nu a putut fi incarcata!";
    }
    else {
        backgroundSprite.setTexture(backgroundTexture);
    }
    sf::Vector2u appSize = controlsWindow->getSize();
    if (!menuTexture.loadFromFile("Textures/settingsSectionBackground.png")) {
        std::cout << "Meniul din controls nu a putut fi initializat!";
    }
    else {
        menuTexture.setSmooth(true);
        menuSprite.setTexture(menuTexture);
        menuSprite.setPosition(appSize.x / 4.40f, appSize.y / 15.0f);
    }
    if (!titleTexture.loadFromFile("Textures/controls.png")) {
        std::cout << "Titlul din controls nu a putut fi initializat!";
    } else {
        titleTexture.setSmooth(true);
        titleSprite.setTexture(titleTexture);
        titleSprite.setScale(0.5, 0.5);
        titleSprite.setPosition(535, 140);
    }

    if (!icelandFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
        std::cout << "Font-ul Iceland nu a putut fi incarcat in meniul Settings!";
    }
    moveLeftText.setFont(icelandFont);
    moveLeftText.setString("Move left:");
    moveLeftText.setCharacterSize(35);
    moveLeftText.setPosition(360, 220);

    moveRightText.setFont(icelandFont);
    moveRightText.setString("Move right:");
    moveRightText.setCharacterSize(35);
    moveRightText.setPosition(360, 320);

    moveUpText.setFont(icelandFont);
    moveUpText.setString("Move up:");
    moveUpText.setCharacterSize(35);
    moveUpText.setPosition(360, 420);

    moveDownText.setFont(icelandFont);
    moveDownText.setString("Move down:");
    moveDownText.setCharacterSize(35);
    moveDownText.setPosition(360, 520);

    jumpText.setFont(icelandFont);
    jumpText.setString("Jump:");
    jumpText.setCharacterSize(35);
    jumpText.setPosition(360, 620);

    closeText.setFont(icelandFont);
    closeText.setString("Close game:");
    closeText.setCharacterSize(35);
    closeText.setPosition(660, 220);

    pauseText.setFont(icelandFont);
    pauseText.setString("Pause game:");
    pauseText.setCharacterSize(35);
    pauseText.setPosition(660, 320);

    tabText.setFont(icelandFont);
    tabText.setString("Player stats:");
    tabText.setCharacterSize(35);
    tabText.setPosition(660, 420);

    inventoryText.setFont(icelandFont);
    inventoryText.setString("Inventory:");
    inventoryText.setCharacterSize(35);
    inventoryText.setPosition(660, 520);

}

void Controls::initButtons() {
    moveLeftButton = new GUI::Button(510, 220, 105, 55, 
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["A"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["A"]);

    moveRightButton = new GUI::Button(525, 320, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["D"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["D"]);

    moveUpButton = new GUI::Button(495, 420, 105, 55, 
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["W"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["W"]);

    moveDownButton = new GUI::Button(530, 520, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["S"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["S"]);

    jumpButton = new GUI::Button(455, 620, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["SPC"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["SPC"]);

    closeButton = new GUI::Button(840, 220, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["ESC"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["ESC"]);

    pauseButton = new GUI::Button(850, 320, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["P"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["P"]);

    tabButton = new GUI::Button(850, 420, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["TAB"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["TAB"]);

    inventoryButton = new GUI::Button(810, 520, 105, 55,
        icelandFont, getKeyStringFromCode(sf::Keyboard::Key(supportedKeys["I"])), 35,
        sf::Color::White, sf::Color::White, sf::Color::White,
        sf::Color::Color(68, 69, 69, 50), sf::Color::Color(68, 69, 69, 75), sf::Color::Color(68, 69, 69, 100),
        sf::Color::Color(255, 255, 255, 50), sf::Color::Color(255, 255, 255, 75), sf::Color::Color(255, 255, 255, 100),
        supportedKeys["I"]);
    
}

// Constructor
Controls::Controls() {
    initWindow();
    initKeys();
    initContent();
    initButtons();
}

// DestructorW
Controls::~Controls() {
    delete controlsWindow;
}

// Functii

void Controls::updateEvents() {
    while (controlsWindow->pollEvent(windowEvent)) {
        if (windowEvent.type == sf::Event::Closed) {
            // Salvez content-ul in fila

            saveToFile();

            controlsWindow->close();
            Game game;
            game.runApplication();
        }
        if (windowEvent.type == sf::Event::KeyPressed) {
            lastKeyPressed = sf::Keyboard::Key(windowEvent.key.code);
        }
        // Mai intai gasesc pozitia mouse-ului pe ecran
        // Si dupa o convertesc la coordonate ale aplicatiei
        sf::Vector2i pozitieGlobala = sf::Mouse::getPosition();
        sf::Vector2f pozitieInAplicatie = controlsWindow->mapPixelToCoords(pozitieGlobala);
        // Acum pentru ca am coordonatele in functie de tot ecranul, trebuie sa scad coordonatele din
        // Logica e asemanatoare cu sumele partiale pe matrice
        pozitieInAplicatie.x -= controlsWindow->getPosition().x;
        pozitieInAplicatie.y -= controlsWindow->getPosition().y;
    }
}

void Controls::updateWindow() {
    updateEvents();
}

void Controls::renderWindow() {
    controlsWindow->clear();
    // Render continutului
    // Prima oara dau render la "lume"
    controlsWindow->draw(backgroundSprite);
    // Adaugarea meniului
    controlsWindow->draw(menuSprite);
    controlsWindow->draw(titleSprite);
    controlsWindow->draw(moveLeftText);
    controlsWindow->draw(moveRightText);
    controlsWindow->draw(moveUpText);
    controlsWindow->draw(moveDownText);
    controlsWindow->draw(closeText);
    controlsWindow->draw(pauseText);
    controlsWindow->draw(tabText);
    controlsWindow->draw(inventoryText);
   
    moveLeftButton->updateButton(sf::Mouse::getPosition(*controlsWindow), moveLeftButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    moveLeftButton->renderButton(*controlsWindow, moveLeftButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));

    moveRightButton->updateButton(sf::Mouse::getPosition(*controlsWindow), moveRightButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    moveRightButton->renderButton(*controlsWindow, moveRightButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    moveUpButton->updateButton(sf::Mouse::getPosition(*controlsWindow), moveUpButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    moveUpButton->renderButton(*controlsWindow, moveUpButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    moveDownButton->updateButton(sf::Mouse::getPosition(*controlsWindow), moveDownButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    moveDownButton->renderButton(*controlsWindow, moveDownButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    //jumpButton->updateButton(sf::Mouse::getPosition(*controlsWindow), jumpButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    //jumpButton->renderButton(*controlsWindow, jumpButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
   
    closeButton->updateButton(sf::Mouse::getPosition(*controlsWindow), closeButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    closeButton->renderButton(*controlsWindow, closeButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    pauseButton->updateButton(sf::Mouse::getPosition(*controlsWindow), pauseButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    pauseButton->renderButton(*controlsWindow, pauseButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    tabButton->updateButton(sf::Mouse::getPosition(*controlsWindow), tabButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    tabButton->renderButton(*controlsWindow, tabButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    inventoryButton->updateButton(sf::Mouse::getPosition(*controlsWindow), inventoryButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    inventoryButton->renderButton(*controlsWindow, inventoryButton->isPressed() ? lastKeyPressed : sf::Keyboard::Key(-1));
    
    lastKeyPressed = sf::Keyboard::Key(-1);
    
    controlsWindow->display();
}

void Controls::runControlsMenu() {
    while (controlsWindow->isOpen()) {
        updateWindow();
        renderWindow();
    }
}

Controls& Controls::operator=(const Controls& other) {
    if (this != &other) {
        controlsWindow = other.controlsWindow;
        backgroundTexture = other.backgroundTexture;
        menuTexture = other.menuTexture;
        titleTexture = other.titleTexture;
        backgroundSprite = other.backgroundSprite;
        menuSprite = other.menuSprite;
        titleSprite = other.titleSprite;
        windowEvent = other.windowEvent;
        moveLeftText = other.moveLeftText;
        moveRightText = other.moveRightText;
        moveUpText = other.moveUpText;
        moveDownText = other.moveDownText;
        closeText = other.closeText;
        pauseText = other.pauseText;
        tabText = other.tabText;
        inventoryText = other.inventoryText;
        jumpText = other.jumpText;
        icelandFont = other.icelandFont;
        moveLeftButton = other.moveLeftButton;
        moveRightButton = other.moveRightButton;
        moveUpButton = other.moveUpButton;
        moveDownButton = other.moveDownButton;
        jumpButton = other.jumpButton;
        closeButton = other.closeButton;
        pauseButton = other.pauseButton;
        tabButton = other.tabButton;
        inventoryButton = other.inventoryButton;
        lastKeyPressed = other.lastKeyPressed;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const Controls& controls) {
    out << "RenderWindow: " << controls.controlsWindow << "\n";
    return out;
}
