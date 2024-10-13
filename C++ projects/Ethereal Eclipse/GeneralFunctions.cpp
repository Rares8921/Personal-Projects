#include "GeneralFunctions.h"

void GeneralFunctions::readFromFiles() {
    std::ifstream applicationConfig("Config/window.ini");
    if (applicationConfig.is_open()) {
        applicationConfig >> musicVolume;
        applicationConfig >> gameVolume;
        applicationConfig >> vSync;
        applicationConfig >> framerate;
        applicationConfig >> antiAliasing;
    }
    applicationConfig.close();
    std::ifstream keyConfig("Config/keys.ini");
    if (keyConfig.is_open()) {
        for (int i = 0; i < 9; ++i) {
            keyConfig >> keyCodes[i];
        }
    }
    keyConfig.close();
}

GeneralFunctions::GeneralFunctions() {
    readFromFiles();
}

GeneralFunctions::~GeneralFunctions() {
}

const bool& GeneralFunctions::isSpriteClicked(sf::RenderWindow *currentWindow, sf::Sprite currentSprite, sf::Vector2f mousePosition) const {
    // Verific daca jucatorul are cursuroul peste buton
    if (currentSprite.getGlobalBounds().contains(mousePosition)) {
        // Acum daca a apasat butonul
        if (sf::Mouse::isButtonPressed(sf::Mouse::Left)) {
            sf::Cursor handCursor;
            if (!handCursor.loadFromSystem(sf::Cursor::Hand)) {
                std::cout << "Cursorul Hand nu a putut fi incarcat din sistem!";
            }
            currentWindow -> setMouseCursor(handCursor);
            return true;
        }
    }
    return false;
}

float GeneralFunctions::updateDeltaTime(sf::Clock deltaTimeClock) {
    // Folosesc asSeconds() pentru cat mai putina eroare de calcul
    return deltaTimeClock.restart().asSeconds();
}

const float& GeneralFunctions::getMusicVolume() const {
    return musicVolume;
}

const float& GeneralFunctions::getGameVolume() const {
    return gameVolume;
}

const bool& GeneralFunctions::getVSync() const {
    return vSync;
}

const float& GeneralFunctions::getFramerate() const {
    return framerate;
}

const bool& GeneralFunctions::getAntiAliasing() const {
    return antiAliasing;
}

const std::array<int, 10>& GeneralFunctions::getKeyCodes() const {
    return keyCodes;
}

std::string GeneralFunctions::getKeyStringFromCode(const sf::Keyboard::Key& k) {
    switch (k) {
    case sf::Keyboard::A:
        return "A";
    case sf::Keyboard::B:
        return "B";
    case sf::Keyboard::C:
        return "C";
    case sf::Keyboard::D:
        return "D";
    case sf::Keyboard::E:
        return "E";
    case sf::Keyboard::F:
        return "F";
    case sf::Keyboard::G:
        return "G";
    case sf::Keyboard::H:
        return "H";
    case sf::Keyboard::I:
        return "I";
    case sf::Keyboard::J:
        return "J";
    case sf::Keyboard::K:
        return "K";
    case sf::Keyboard::L:
        return "L";
    case sf::Keyboard::M:
        return "M";
    case sf::Keyboard::N:
        return "N";
    case sf::Keyboard::O:
        return "O";
    case sf::Keyboard::P:
        return "P";
    case sf::Keyboard::Q:
        return "Q";
    case sf::Keyboard::R:
        return "R";
    case sf::Keyboard::S:
        return "S";
    case sf::Keyboard::T:
        return "T";
    case sf::Keyboard::U:
        return "U";
    case sf::Keyboard::V:
        return "V";
    case sf::Keyboard::W:
        return "W";
    case sf::Keyboard::X:
        return "X";
    case sf::Keyboard::Y:
        return "Y";
    case sf::Keyboard::Z:
        return "Z";
    case sf::Keyboard::Num0:
        return "0";
    case sf::Keyboard::Num1:
        return "1";
    case sf::Keyboard::Num2:
        return "2";
    case sf::Keyboard::Num3:
        return "3";
    case sf::Keyboard::Num4:
        return "4";
    case sf::Keyboard::Num5:
        return "5";
    case sf::Keyboard::Num6:
        return "6";
    case sf::Keyboard::Num7:
        return "7";
    case sf::Keyboard::Num8:
        return "8";
    case sf::Keyboard::Num9:
        return "9";
    case sf::Keyboard::Escape:
        return "Esc";
    case sf::Keyboard::LControl:
        return "LCtrl";
    case sf::Keyboard::LShift:
        return "LShift";
    case sf::Keyboard::LAlt:
        return "LAlt";
    case sf::Keyboard::LSystem:
        return "LSys";
    case sf::Keyboard::RControl:
        return "RCtrl";
    case sf::Keyboard::RShift:
        return "RShift";
    case sf::Keyboard::RAlt:
        return "RAlt";
    case sf::Keyboard::RSystem:
        return "RSys";
    case sf::Keyboard::Menu:
        return "Menu";
    case sf::Keyboard::LBracket:
        return "LBrkt";
    case sf::Keyboard::RBracket:
        return "RBrkt";
    case sf::Keyboard::SemiColon:
        return "SColon";
    case sf::Keyboard::Comma:
        return "Comma";
    case sf::Keyboard::Period:
        return "Period";
    case sf::Keyboard::Quote:
        return "Quote";
    case sf::Keyboard::Slash:
        return "Slash";
    case sf::Keyboard::BackSlash:
        return "BSlash";
    case sf::Keyboard::Tilde:
        return "Tilde";
    case sf::Keyboard::Equal:
        return "Equal";
    case sf::Keyboard::Dash:
        return "Dash";
    case sf::Keyboard::Space:
        return "Space";
    case sf::Keyboard::Return:
        return "Ret";
    case sf::Keyboard::BackSpace:
        return "BSpace";
    case sf::Keyboard::Tab:
        return "Tab";
    case sf::Keyboard::PageUp:
        return "PUp";
    case sf::Keyboard::PageDown:
        return "PDown";
    case sf::Keyboard::End:
        return "End";
    case sf::Keyboard::Home:
        return "Home";
    case sf::Keyboard::Insert:
        return "Insert";
    case sf::Keyboard::Delete:
        return "Delete";
    case sf::Keyboard::Add:
        return "Add";
    case sf::Keyboard::Subtract:
        return "Sub";
    case sf::Keyboard::Multiply:
        return "Mul";
    case sf::Keyboard::Divide:
        return "Div";
    case sf::Keyboard::Left:
        return "Left";
    case sf::Keyboard::Right:
        return "Right";
    case sf::Keyboard::Up:
        return "Up";
    case sf::Keyboard::Down:
        return "Down";
    case sf::Keyboard::Numpad0:
        return "Np0";
    case sf::Keyboard::Numpad1:
        return "Np1";
    case sf::Keyboard::Numpad2:
        return "Np2";
    case sf::Keyboard::Numpad3:
        return "Np3";
    case sf::Keyboard::Numpad4:
        return "Np4";
    case sf::Keyboard::Numpad5:
        return "Np5";
    case sf::Keyboard::Numpad6:
        return "Np6";
    case sf::Keyboard::Numpad7:
        return "Np7";
    case sf::Keyboard::Numpad8:
        return "Np8";
    case sf::Keyboard::Numpad9:
        return "Np9";
    case sf::Keyboard::F1:
        return "F1";
    case sf::Keyboard::F2:
        return "F2";
    case sf::Keyboard::F3:
        return "F3";
    case sf::Keyboard::F4:
        return "F4";
    case sf::Keyboard::F5:
        return "F5";
    case sf::Keyboard::F6:
        return "F6";
    case sf::Keyboard::F7:
        return "F7";
    case sf::Keyboard::F8:
        return "F8";
    case sf::Keyboard::F9:
        return "F9";
    case sf::Keyboard::F10:
        return "F10";
    case sf::Keyboard::F11:
        return "F11";
    case sf::Keyboard::F12:
        return "F12";
    case sf::Keyboard::F13:
        return "F13";
    case sf::Keyboard::F14:
        return "F14";
    case sf::Keyboard::F15:
        return "F15";
    case sf::Keyboard::Pause:
        return "Pause";
    case sf::Keyboard::KeyCount:
        return "KeyC";
    default:
        return "ERR";
        break;
    }
    return "ERR";
}

GeneralFunctions& GeneralFunctions::operator=(const GeneralFunctions& other) {
    if (this != &other) {
        // Copiați membrii clasici
        musicVolume = other.musicVolume;
        gameVolume = other.gameVolume;
        vSync = other.vSync;
        framerate = other.framerate;
        antiAliasing = other.antiAliasing;
        keyCodes = other.keyCodes;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& out, const GeneralFunctions& generalFuncs) {
    out << "GeneralFunctions details:\n";
    out << "Music Volume: " << generalFuncs.getMusicVolume() << "\n";
    out << "Game Volume: " << generalFuncs.getGameVolume() << "\n";
    out << "VSync: " << generalFuncs.getVSync() << "\n";
    out << "Framerate: " << generalFuncs.getFramerate() << "\n";
    out << "AntiAliasing: " << generalFuncs.getAntiAliasing() << "\n";
    out << "Key Codes:\n";
    for (const auto& keyCode : generalFuncs.getKeyCodes()) {
        out << keyCode << " ";
    }
    out << "\n";
    return out;
}
