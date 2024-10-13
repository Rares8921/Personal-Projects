
#include "State.h"

State::State() {
	counter = 0;
	quit = false;
	paused = false;
}

State::State(float gridSize,
	sf::RenderWindow* currentWindow, sf::VideoMode gameVideoMode,
	std::map<std::string, int>* supportedKeys, std::stack<State*>* stackOfStates)
    : gridSize(gridSize), currentWindow(currentWindow), supportedKeys(supportedKeys), stackOfStates(stackOfStates) {
	
	counter = 0;
	quit = false;
	paused = false;
}

State::~State() {
	while (!stackOfStates->empty()) {
		delete stackOfStates->top();
		stackOfStates->pop();
	}
	delete stackOfStates;
}

const bool State::getKeytime() {
	if (keytime >= keytimeMax) {
		keytime = 0.f;
		return true;
	}
	return false;
}

void State::checkForQuit() {
	// Verific daca actiunea curenta se termina
	// sau daca aplicatia nu mai este focused
	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key(keyBinds["BACK"]))) {
		quit = true;
	}
}

void State::checkForPause() {
	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key(keyBinds["PAUSE"]))) {
		std::cout << stateClock.getElapsedTime().asSeconds() << "\n";
		if (!paused) {
			paused = true;
		} else if (stateClock.getElapsedTime().asSeconds() >= 0.7) {
			paused = false;
			stateClock.restart();
		}
	}
}

const bool& State::getQuitValue() const {
	return quit;
}

const bool& State::getPauseValue() const {
	return paused;
}

void State::endState() {
	quit = true;
}

void State::pauseState() {
	paused = true;
}

void State::unpauseState() {
	paused = false;
}

void State::updateMousePositions(sf::View* view) {
	mousePosScreen = sf::Mouse::getPosition();
	mousePosWindow = sf::Mouse::getPosition(*currentWindow);

	if (view) {
		currentWindow->setView(*view);
	}

	mousePosView = currentWindow->mapPixelToCoords(sf::Mouse::getPosition(*currentWindow));
	mousePosGrid =
		sf::Vector2i(
			static_cast<int>(mousePosView.x) / static_cast<int>(gridSize),
			static_cast<int>(mousePosView.y) / static_cast<int>(gridSize)
		);

	currentWindow->setView(currentWindow->getDefaultView());
}

void State::updateKeytime(const float& deltaTime) {
	if (keytime < keytimeMax) {
		keytime += 100.f * deltaTime;
	}
}

State& State::operator=(const State& other) {
	if (this != &other) {
		quit = other.quit;
		paused = other.paused;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const State& state) {
	out << "Quit: " << (state.quit ? "true" : "false") << "\n";
	out << "Paused: " << (state.paused ? "true" : "false") << "\n";
	return out;
}
