#pragma once

#include <vector>
#include <stack>
#include <map>
#include "SFML\Graphics.hpp"
#include "../Entity.h"

class State {
private:
	bool quit;
	bool paused;
protected:
	// Fiecare state va pointa catre state-ul original, fara a se mai crea alta copie
	std::stack<State*>* stackOfStates;
	std::map<std::string, int>* supportedKeys;
	std::map<std::string, int> keyBinds;
	sf::RenderWindow* currentWindow;
	std::map<std::string, sf::Texture> textures;
	float gridSize;
	sf::VideoMode gameVideoMode;
	virtual void initKeyBinds() = 0;
	int counter;
	sf::Event stateEvent;
	sf::Clock stateClock;

	float keytime;
	float keytimeMax;

	sf::Vector2i mousePosScreen;
	sf::Vector2i mousePosWindow;
	sf::Vector2f mousePosView;
	sf::Vector2i mousePosGrid;
public:
	State();
	State(float gridSize,
		sf::RenderWindow* currentWindow, sf::VideoMode gameVideoMode, 
		std::map<std::string, int>* supportedKeys, std::stack<State*>* stackOfStates);
	virtual ~State();
	// Pentru ca State sa fie abstracta
	// Cu alte cuvinte, urmatoarele functii trebuiesc implementate in fiecare clasa ulterior
	const bool getKeytime();
	virtual void checkForQuit();
	virtual void checkForPause();
	// Folosesc const ()& pentru a trimite referinta, ci nu valoarea, astfel nu se va mai crea o copie a valorii
	const bool& getQuitValue() const;
	const bool& getPauseValue() const;
	void endState();
	void pauseState();
	void unpauseState();

	virtual void updateMousePositions(sf::View* view = NULL);
	virtual void updateKeytime(const float& dt);
	virtual void updateInput(const float& deltaTime) = 0;
	// Actualizez state-urile in functie de deltaTime
	virtual void updateState(const float& deltaTime) = 0;
	// Dau render indiferent daca a fost dat un target ca argument
	virtual void renderState(sf::RenderTarget* currentTarget = nullptr) = 0;
	State& operator=(const State& other);
	friend std::ostream& operator<<(std::ostream& out, const State& state);
};

