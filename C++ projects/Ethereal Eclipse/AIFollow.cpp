#include "AIFollow.h"
#include <iostream>

AIFollow::AIFollow(Entity& selfEntity, Entity& currentEntity) 
	: selfEntity(selfEntity), currentEntity(currentEntity) {

}

AIFollow::~AIFollow() {
}

void AIFollow::update(const float& deltaTime) {
	// Calculez distanta de la entitate la player
	sf::Vector2f move;
	move.x = currentEntity.getPosition().x - selfEntity.getPosition().x;
	move.y = currentEntity.getPosition().y - selfEntity.getPosition().y;
	// Calculez lungimea vectorului player-ului
	float lungime = sqrt(pow(move.x, 2) + pow(move.y, 2));
	move /= lungime;
	// Verific ca player-ul sa fie in raza de actiunii a entitatii
	// Si verific ca entitatea sa nu preia pozitia player-ului si sa ramana statica
	if ((selfEntity.getPosition().x != currentEntity.getPosition().x) && std::abs(lungime) < 1800.f) {
		selfEntity.moveEntity(deltaTime, move.x, move.y);
	}
}

AIFollow& AIFollow::operator=(const AIFollow& other) {
	if (this != &other) {
		selfEntity = other.selfEntity;
		currentEntity = other.currentEntity;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const AIFollow& aiFollow) {
	return out;
}
