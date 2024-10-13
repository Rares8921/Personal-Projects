#include "AnimationComponent.h"
#include <iostream>

AnimationComponent::AnimationComponent(sf::Sprite& animationSprite, sf::Texture& animationComponentTexture)
	: animationComponentSprite(animationSprite), animationComponentTexture(animationComponentTexture), 
	lastAnimation(NULL), priorityAnimation(NULL) {

}

AnimationComponent::~AnimationComponent() {
	// Trebuie sa dealoc memoria pentru fiecare animatie
	for (auto& animation : animationMapSheet) {
		delete animation.second;
	}
}

void AnimationComponent::addAnimation(const std::string animationName, 
	float animationTimer, 
	int start_x, int start_y, int frames_x, int frames_y, 
	int width, int height, const int& ENTITY_CODE) {
	switch (ENTITY_CODE) {
		case 1: // Daca entitatea este jucator, atunci maresc putin scale-ul
			animationComponentSprite.setScale(1.3f, 1.3f);
			break;
		default:
			animationComponentSprite.setScale(1.5f, 1.5f);
			break;
	}
	animationMapSheet[animationName] = new Animation(animationComponentSprite, animationComponentTexture, 
												animationTimer, 
												start_x, start_y, frames_x, frames_y,
												width, height);
}

bool AnimationComponent::playAnimation(const std::string animationName, const float &deltaTime, const bool priority) {
	if (priorityAnimation) {
		if (priorityAnimation == animationMapSheet[animationName]) {
			if (lastAnimation != animationMapSheet[animationName]) {
				if (lastAnimation != NULL) {
					lastAnimation->reset();
				}
				lastAnimation = animationMapSheet[animationName];
			}
			if (animationMapSheet[animationName]->play(deltaTime)) {
				priorityAnimation = NULL;
			}
		}
	} else {
		if (lastAnimation != animationMapSheet[animationName]) {
			if (lastAnimation != NULL) {
				lastAnimation->reset();
			}
			lastAnimation = animationMapSheet[animationName];
		}
		animationMapSheet[animationName]->play(deltaTime);
	}
	return animationMapSheet[animationName] -> isDone();
}

AnimationComponent& AnimationComponent::operator=(const AnimationComponent& other) {
	animationComponentSprite = other.animationComponentSprite;
	animationComponentTexture = other.animationComponentTexture;
	if (other.lastAnimation) {
		lastAnimation = new Animation(*other.lastAnimation);
	}  else {
		lastAnimation = nullptr;
	}
	if (other.priorityAnimation) {
		priorityAnimation = new Animation(*other.priorityAnimation);
	} else {
		priorityAnimation = nullptr;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const AnimationComponent& animComp) {
	return out;
}

AnimationComponent::Animation::~Animation() {
}
