#include "GameState.h"
#include "../GameRun.h"
#include <iostream>
#include <cmath>

void GameState::initDeferredRender()
{
	renderTexture.create(1300, 850);

	renderSprite.setTexture(renderTexture.getTexture());
	renderSprite.setTextureRect(sf::IntRect(0, 0, 1300, 850));
}

//Initializer functions
void GameState::initView() {
	mapView = sf::View(sf::FloatRect(0.f, 0.f, 1300.f, 850.f));
	mapView.setCenter(1300.f / 2.f, 850.f / 2.f);
	currentWindow->setView(mapView);
}

void GameState::initKeyBinds()
{
	keyBinds.emplace("MOVE_LEFT", supportedKeys->at("A"));
	keyBinds.emplace("MOVE_RIGHT", supportedKeys->at("D"));
	keyBinds.emplace("MOVE_UP", supportedKeys->at("W"));
	keyBinds.emplace("MOVE_DOWN", supportedKeys->at("S"));
	keyBinds.emplace("BACK", supportedKeys->at("ESC"));
	keyBinds.emplace("PAUSE", supportedKeys->at("P"));
	keyBinds.emplace("TAB", supportedKeys->at("TAB"));
	keyBinds.emplace("ITEM", supportedKeys->at("I"));
	keyBinds.emplace("JUMP", supportedKeys->at("SPC"));
}

void GameState::initFonts() {
	if (!currentFont.loadFromFile("Fonts/Iceland/Iceland.ttf")) {
		std::cout << "Font-ul Iceland nu a putut fi incarcat pentru Zona 1!";
	}
}

void GameState::initTextures()
{
	if (!textures["PLAYER_SHEET"].loadFromFile("Textures/mainSheet.png")) {
		std::cout << "PLAYER_SHEET nu a putut fi incarcat!";
	}
	if (!textures["RAT1_SHEET"].loadFromFile("Textures/rat1.png")) {
		std::cout << "RAT1_SHEET nu a putut fi incarcat!";
	}
	if (!textures["RAT2_SHEET"].loadFromFile("Textures/rat2.png")) {
		std::cout << "RAT2_SHEET nu a putut fi incarcat!";
	}
	if (!textures["RAT3_SHEET"].loadFromFile("Textures/rat3.png")) {
		std::cout << "RAT3_SHEET nu a putut fi incarcat!";
	}
	if (!textures["RAT4_SHEET"].loadFromFile("Textures/rat4.png")) {
		std::cout << "RAT4_SHEET nu a putut fi incarcat!";

	}
}

void GameState::initPauseMenu()
{ 
	sf::VideoMode vm = sf::VideoMode(1300, 850);
	pauseMenu = new PauseMenu(vm, currentFont);

	pauseMenu->addButton("QUIT", GUI::p2pY(74.f, vm), GUI::p2pX(13.f, vm), GUI::p2pY(6.f, vm), (vm.width + vm.height) / 60.f, "Quit");
}

void GameState::initShader() {
	if (!gameShader.loadFromFile("vertex_shader.vert", "fragment_shader.frag")) {
		std::cout << "Shader-ul pentru harta nu a putut fi incarcat!\n";
	}
}

void GameState::initKeyTime() {
	keyTimeMax = 0.3f;
	keyTimer.restart();
}

void GameState::initPlayer() {
	currentPlayer = new Player(1455.11, 4142.69, textures["PLAYER_SHEET"]);
}

void GameState::initPlayerStatus() {
	sf::VideoMode vm = sf::VideoMode(1300, 850);
	playerStatus = new PlayerGUI(currentPlayer, vm);
}

void GameState::initEnemySystem() {
	enemySystem = new EnemySystem(textures, activeEnemies, *currentPlayer);
}

void GameState::initTileMap() {
	currentTileMap = new TileMap("zone" + std::to_string(gameLevel) + ".slmp");
}

void GameState::initTextSystem() {
	textTags = new TextTags("Fonts/Pixel/Pixel.ttf");
}

void GameState::initSounds() {
	//sf::SoundBuffer buffer;
	//if (!buffer.loadFromFile("Sounds/mainMusic.mp3")) {
	//	throw "Melodia jocului nu a putut fi incarcata!\n";
	//}
	//musicSound.setBuffer(buffer);
	//musicSound.setLoop(true);
	//musicSound.play();
}


GameState::GameState(float gridSize,
	sf::RenderWindow* currentWindow, sf::VideoMode gameVideoMode,
	std::map<std::string, int>* supportedKeys, std::stack<State*>* stackOfStates, int level)
	: State(gridSize, currentWindow, gameVideoMode, supportedKeys, stackOfStates), gameLevel(level) {

	initDeferredRender();
	initView();
	initKeyBinds();
	initFonts();
	initTextures();
	initPauseMenu();
	initShader();
	initKeyTime();
	initPlayer();
	initPlayerStatus();
	initEnemySystem();
	initTileMap();
	initTextSystem();
	initSounds();
}

GameState::~GameState() {
	musicSound.stop();
	delete pauseMenu;
	delete currentPlayer;
	delete playerStatus;
	delete enemySystem;
	delete currentTileMap;
	delete textTags;

	for (int i = 0; i < activeEnemies.size(); ++i) {
		delete activeEnemies[i];
	}
}

const bool GameState::getKeyTime() {
	if (keyTimer.getElapsedTime().asSeconds() >= keyTimeMax) {
		keyTimer.restart();
		return true;
	}
	return false;
}

//Functions
void GameState::updateView(const float& deltaTime)
{	
	sf::Vector2f pozitieInAplicatie = static_cast<sf::Vector2f>(sf::Mouse::getPosition(*currentWindow));
	// Setez noul centru al viewport-ului in raport cu pozitia mouse-ului si cu coordonatele hartii
	// Cu alte cuvinte, pot misca mouse-ul in anumite directii pentru a vedea o portiune respectiva din harta
	// Si mouse-ul se poate misca doar la o distanta relativa cu pozitia curenta a player-ului
	mapView.setCenter(
		std::floor(getPlayerCoords().x + (mousePosWindow.x - currentWindow->getSize().x / 2.f) / 10.f),
		std::floor(getPlayerCoords().y + (mousePosWindow.y - currentWindow->getSize().y / 2.f) / 10.f)
	);
	sf::Vector2f mapSize = currentTileMap->getMaxSize();

	// Actualizez miscarea pe verticala
	// Verific daca harta permite mutarea viewport-ului in functie de directia in care merge playerul
	if (mapSize.x >= mapView.getSize().x) {
		// Aici verific daca poate fi mutat viewport-ul(daca nu iese in afara hartii)
		if (mapView.getCenter().x - mapView.getSize().x / 2.f < 0.f) {
			mapView.setCenter(mapView.getSize().x / 2.f, mapView.getCenter().y);
			// Altfel setez viewport-ul la extremele posibile
		}
		else if (mapView.getCenter().x + mapView.getSize().x / 2.f > mapSize.x) {
			mapView.setCenter(mapSize.x - mapView.getSize().x / 2.f, mapView.getCenter().y);
		}
	}

	// Actualizez miscarea pe orizontala
	// Aceeasi logica ca la miscarea pe verticala
	if (mapSize.y >= mapView.getSize().y) {
		if (mapView.getCenter().y - mapView.getSize().y / 2.f < 0.f) {
			mapView.setCenter(mapView.getCenter().x, mapView.getSize().y / 2.f);
		}
		else if (mapView.getCenter().y + mapView.getSize().y / 2.f > mapSize.y) {
			mapView.setCenter(mapView.getCenter().x, mapSize.y - mapView.getSize().y / 2.f);
		}
	}

	viewGridPosition = sf::Vector2i((int) mapView.getCenter().x / (int) gridSize, mapView.getCenter().y / gridSize);

	// Actualizez cu noul view	
	currentWindow->setView(mapView);
}

const sf::View& GameState::getMapView() const {
	return mapView;
}

const sf::Vector2f& GameState::getPlayerCoords() const {
	return static_cast<sf::Vector2f>(currentPlayer->getPosition());
}

void GameState::saveData() {
	std::ofstream fout("Config/levels.ini");
	if (fout.is_open()) {
		AttributeComponent* ac = currentPlayer->getAttributeComponent();
		fout << ac->getCurrentLevel() << "\n";
		fout << ac->getCurrentExp() << "\n";
		fout << ac->getVitality() << "\n";
		fout << ac->getStrength() << "\n";
		fout << ac->getAgility() << "\n";
		fout << ac->getIntelligence() << "\n";
		fout << ac->getCurrentHp() << "\n";
		fout << ac->getCurrentMp() << "\n";
		fout << ac->getMagicPower() << "\n";
		fout << ac->getMaxHp() << "\n";
		fout << ac->getMaxMp() << "\n";
		fout << ac->getPhysicalDefence() << "\n";
		fout << ac->getMagicDefence() << "\n";
		fout << ac->getMinAttackDamage() << "\n";
		fout << ac->getMaxAttackDamage() << "\n";
		fout << ac->getMinMagicDamage() << "\n";
		fout << ac->getMinMagicDamage() << "\n";
	}
	fout.close();
}

void GameState::updateInput(const float& deltaTime) {
	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key(keyBinds.at("BACK"))) && getKeyTime()) {
		// Salvez datele curente
		saveData();
		musicSound.stop();
		GameRun gameRun;
		currentWindow->close();
		gameRun.runGameMenu();
	}
	sf::Vector2f v2f1 = sf::Vector2f(1455.11, 4160.69);
	if (currentPlayer->getGlobalBounds().contains(v2f1)) {
		saveData();
		musicSound.stop();
		GameRun gameRun;
		currentWindow->close();
		gameRun.runGameMenu();
	}
	if (currentPlayer->getAttributeComponent()->isDead()) {
		musicSound.stop();
		GameRun gameRun;
		currentWindow->close();
		gameRun.runGameMenu();
	}
	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key(keyBinds.at("PAUSE"))) && getKeyTime()) {
		if (!getPauseValue()) {
			pauseState();
		} else {
			unpauseState();
		}
	}
}

void GameState::updatePlayerInput(const float& deltaTime) {
	sf::Keyboard::Key left = sf::Keyboard::Key(keyBinds.at("MOVE_LEFT"));
	sf::Keyboard::Key right = sf::Keyboard::Key(keyBinds.at("MOVE_RIGHT"));
	sf::Keyboard::Key up = sf::Keyboard::Key(keyBinds.at("MOVE_UP"));
	sf::Keyboard::Key down = sf::Keyboard::Key(keyBinds.at("MOVE_DOWN"));
	if (!getPauseValue()) {
		if (sf::Keyboard::isKeyPressed(left) && sf::Keyboard::isKeyPressed(up) &&
			sf::Keyboard::isKeyPressed(right) && sf::Keyboard::isKeyPressed(down)) {
			currentPlayer->moveEntity(deltaTime, 0.0f, 0.0f);
		}
		else if (sf::Keyboard::isKeyPressed(left) && sf::Keyboard::isKeyPressed(up)) {
			currentPlayer->moveEntity(deltaTime, -1.0f, -1.0f);
		}
		else if (sf::Keyboard::isKeyPressed(left) && sf::Keyboard::isKeyPressed(down)) {
			currentPlayer->moveEntity(deltaTime, -1.0f, 1.0f);
		}
		else if (sf::Keyboard::isKeyPressed(left) && sf::Keyboard::isKeyPressed(right)) {
			currentPlayer->moveEntity(deltaTime, 0.0f, 0.0f);
		}
		else if (sf::Keyboard::isKeyPressed(left)) {
			currentPlayer->moveEntity(deltaTime, -1.0f, 0.0f);
		}
		else if (sf::Keyboard::isKeyPressed(right) && sf::Keyboard::isKeyPressed(up)) {
			currentPlayer->moveEntity(deltaTime, 1.0f, -1.0f);
		}
		else if (sf::Keyboard::isKeyPressed(right) && sf::Keyboard::isKeyPressed(down)) {
			currentPlayer->moveEntity(deltaTime, 1.0f, 1.0f);
		}
		else if (sf::Keyboard::isKeyPressed(right)) {
			currentPlayer->moveEntity(deltaTime, 1.0f, 0.0f);
		}
		else if (sf::Keyboard::isKeyPressed(up) && sf::Keyboard::isKeyPressed(down)) {
			currentPlayer->moveEntity(deltaTime, 0.0f, 0.0f);
		}
		else if (sf::Keyboard::isKeyPressed(up)) {
			currentPlayer->moveEntity(deltaTime, 0.0f, -1.0f);
		}
		else if (sf::Keyboard::isKeyPressed(down)) {
			currentPlayer->moveEntity(deltaTime, 0.0f, 1.0f);
		}
	}
}

void GameState::updatePlayerStatus(const float& deltaTime)
{
	playerStatus->update(deltaTime);

	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Key(keyBinds.at("TAB"))) && getKeyTime()) {
		playerStatus->toggleCharacterTab();
	}
}

void GameState::updatePauseMenuButtons() {
	if (pauseMenu->isButtonPressed("QUIT")) {
		GameRun gameRun;
		currentWindow->close();
		gameRun.runGameMenu();
	}
}

void GameState::updateTileMap(const float& deltaTime)
{
	currentTileMap->updateForCollision(currentPlayer, deltaTime);
	currentTileMap->updateTileCollision(currentPlayer, deltaTime);
	currentTileMap->updateTiles(currentPlayer, deltaTime, *enemySystem);
}

void GameState::updatePlayer(const float& deltaTime) {
	currentPlayer->update(deltaTime, mousePosView, mapView);
}

void GameState::updateCombatAndEnemies(const float& deltaTime) {
	if (sf::Mouse::isButtonPressed(sf::Mouse::Left) && currentPlayer->getWeapon()->getAttackTimer()) {
		currentPlayer->setInitAttacking(true);
	}
	int index = 0;
	for (auto* enemy : activeEnemies) {
		enemy->updateEntity(deltaTime, mousePosView, mapView);

		currentTileMap->updateForCollision(enemy, deltaTime);
		currentTileMap->updateTileCollision(enemy, deltaTime);

		std::cout << currentPlayer->getInitAttacking() << "\n";
		updateCombat(enemy, index, deltaTime);

		//DANGEROUS!!!
		if (enemy->isDead()) {
			currentPlayer->gainExp(enemy->getExpAmount());
			textTags->addTextTag(EXPERIENCE_TAG,
				currentPlayer->getPosition().x - 40.f, currentPlayer->getPosition().y - 30.f,
				enemy->getExpAmount(), "+", "EXP");

			enemySystem->removeEnemy(index);
			continue;
		} else if (enemy->isDespawnTimerDone()) {
			enemySystem->removeEnemy(index);
			continue;
		}

		++index;
	}

	currentPlayer->setInitAttacking(false);
}


void GameState::updateCombat(Enemy* enemy, const int index, const float& deltaTime) {
	if (currentPlayer->getInitAttacking()
		&& enemy->getGlobalBounds().contains(mousePosView)
		&& enemy->getSpriteDistance(*currentPlayer) < currentPlayer->getWeapon()->getRange()
		&& enemy->isDamageTimerDone())
	{
		//Get to this!!!!
		int dmg = static_cast<int>(currentPlayer->getDamage());
		enemy->loseHP(dmg);
		enemy->resetDamageTimer();
		textTags->addTextTag(DEFAULT_TAG, enemy->getPosition().x, enemy->getPosition().y, dmg, "", "");
	}

	//Check for enemy damage
	if (currentPlayer->getGlobalBounds().contains(enemy->getPosition()) && currentPlayer->getDamageTimer())
	{
		int dmg = enemy->getAttributeComponent()->getMaxAttackDamage();
		currentPlayer->loseHp(dmg);
		textTags->addTextTag(NEGATIVE_TAG, currentPlayer->getPosition().x - 30.f, currentPlayer->getPosition().y, dmg, "-", "HP");
	}
}


void GameState::updateState(const float& deltaTime) {
	updateMousePositions(&mapView);
	updateKeytime(deltaTime);
	updateInput(deltaTime);

	if (!getPauseValue())  {
		updateView(deltaTime);
		updatePlayerInput(deltaTime);
		updateTileMap(deltaTime);
		updatePlayer(deltaTime);
		updatePlayerStatus(deltaTime);

		//Update all enemies
		//CHANGE: Loop outside, and make functions take one enemy at a time
		updateCombatAndEnemies(deltaTime);

		//Update systems
		textTags->updateTextTags(deltaTime);

	} else {
		pauseMenu->updatePauseMenu(mousePosWindow);
		updatePauseMenuButtons();
	}
}

void GameState::renderState(sf::RenderTarget* currentTarget) {
	if (!currentTarget) {
		currentTarget = currentWindow;
	}
	if (getPauseValue()) {
		currentTarget->setView(currentTarget->getDefaultView());
		pauseMenu->renderPauseMenu(*currentTarget);
	} else {
		currentTarget->setView(mapView);
		currentTileMap->renderTileMap(*currentTarget, currentPlayer->getGridPosition(gridSize), &currentShader, currentPlayer->getCenter(), false);
		for (auto* enemy : activeEnemies) {
			enemy->renderEntity(*currentTarget, &currentShader, currentPlayer->getCenter(), true);
		}
		currentPlayer->renderEntity(*currentTarget, &currentShader, currentPlayer->getCenter(), true);
		currentTileMap->renderDeferred(*currentTarget, &currentShader, currentPlayer->getCenter());
		textTags->renderTextTags(*currentTarget);
		currentTarget->setView(currentTarget->getDefaultView());
		playerStatus->render(*currentTarget);
		currentTarget->setView(mapView);
	}
}

GameState& GameState::operator=(const GameState& other) {
	if (this != &other) {
		renderSprite = other.renderSprite;
		mapView = other.mapView;
		pauseMenu = other.pauseMenu;
		currentPlayer = other.currentPlayer;
		playerStatus = other.playerStatus;
		currentTileMap = other.currentTileMap;
		currentFont = other.currentFont;
		activeEnemies = other.activeEnemies;
		enemySystem = other.enemySystem;
		keyTimer = other.keyTimer;
		keyTimeMax = other.keyTimeMax;
		viewGridPosition = other.viewGridPosition;
		textTags = other.textTags;
		gameLevel = other.gameLevel;
		musicSound = other.musicSound;
		attackSound = other.attackSound;
		playerDeathSound = other.playerDeathSound;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const GameState& gameState) {
	out << "Pause Menu: " << gameState.pauseMenu << "\n";
	out << "Current Player: " << gameState.currentPlayer << "\n";
	out << "Player Status: " << gameState.playerStatus << "\n";
	out << "Current Tile Map: " << gameState.currentTileMap << "\n";
	out << "Active Enemies: ";
	for (const auto& enemy : gameState.activeEnemies) {
		out << enemy << ", ";
	}
	out << "\n";
	out << "Enemy System: " << gameState.enemySystem << "\n";
	out << "Key Timer: " << gameState.keyTimer.getElapsedTime().asSeconds() << "\n";
	out << "Key Time Max: " << gameState.keyTimeMax << "\n";
	out << "View Grid Position: " << gameState.viewGridPosition.x << ", " << gameState.viewGridPosition.y << "\n";
	out << "Text Tags: " << gameState.textTags << "\n";
	out << "Game Level: " << gameState.gameLevel << "\n";
	return out;
}
