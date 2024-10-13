#pragma once
#include "Entity.h"
#include <cmath>

class AIFollow {
private:
	Entity& selfEntity, & currentEntity;
public:
	AIFollow(Entity& selfEntity, Entity& currentEntity);
	~AIFollow();
	void update(const float& deltaTime);
	AIFollow& operator=(const AIFollow& other);
	friend std::ostream& operator<<(std::ostream& out, const AIFollow& aiFollow);
};

