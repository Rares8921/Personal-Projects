#pragma once

#include "SFML\Graphics.hpp"
#include <map>
#include <iostream>

class AnimationComponent {
private:
	class Animation {
	private:
		sf::Sprite& animationSprite;
		sf::Texture& textureSheet;
		float animationTimer, timer;
		// animationTimer - cat dureaza animatia
		// timer - cat timp a trecut din animatie
		int width;
		int height;
		// IntRect - dreptunghi care are coordonate numere intregi
		// Primul contine prima textura a animatiei, al doilea contine textura curenta
		// Iar ultimul, evident, ultima textura a animatiei
		sf::IntRect startRectangle, currentRectangle, endRectangle;
		bool animationIsDone;
	public:
		Animation(sf::Sprite& animationSprite, sf::Texture& textureSheet, float animationTimer,
				  int start_x, int start_y, int frames_x, int frames_y,
				  int width, int height) : animationSprite(animationSprite), textureSheet(textureSheet), 
			animationTimer(animationTimer), width(width), height(height) {
			// Coordonatele de start, iar inmultirea este necesara pentru a face display corect animatiei
			startRectangle = sf::IntRect(start_x * width, start_y * height, width, height);
			currentRectangle = startRectangle;
			// frames_x * width si frames_y * height sunt coordonatele unde va trebui sa fie sprite-ul
			// la sfarsitul animatiei
			endRectangle = sf::IntRect(frames_x * width, frames_y * height, width, height);
			timer = 0.f;
			// Al doilea argument cu true imi foloseste la resize-ul automat al sprite-ului
			// Preia size-ul texturii
			animationSprite.setTexture(textureSheet, true);
			animationSprite.setTextureRect(startRectangle);
			animationIsDone = false;
		}
		virtual ~Animation();
		// Functii
		const bool& play(const float& deltaTime) {
			// Actualizez timer-ul
			timer += deltaTime * 100.0f;
			animationIsDone = false;
			if (timer >= animationTimer) {
				// Resetez timer-ul cand se termina animatia
				timer = 0.f;
				// Verific daca animatia continua
				if (currentRectangle != endRectangle) {
					// Animez inspre dreapta
					currentRectangle.left += width;
				} else { // Altfel, resetez
					currentRectangle.left = startRectangle.left;
					animationIsDone = true;
				}
				animationSprite.setTextureRect(currentRectangle);
			}
			return animationIsDone;
		}
		const bool& isDone() const {
			return animationIsDone;
		}
		void reset() {
			timer = animationTimer;
			currentRectangle = startRectangle;
		}
		Animation& operator=(const Animation& otherAnimation) {
			if (this != &otherAnimation) {
				animationSprite = otherAnimation.animationSprite;
				textureSheet = otherAnimation.textureSheet;
				animationTimer = otherAnimation.animationTimer;
				timer = otherAnimation.timer;
				width = otherAnimation.width;
				height = otherAnimation.height;
				startRectangle = otherAnimation.startRectangle;
				currentRectangle = otherAnimation.currentRectangle;
				endRectangle = otherAnimation.endRectangle;
				animationIsDone = otherAnimation.animationIsDone;
			}
			return *this;
		}

		// Suprascrierea operatorului de inserție pentru clasa Animation
		friend std::ostream& operator<<(std::ostream& out, const Animation& anim) {
			out << "Animation Timer: " << anim.animationTimer << "\n";
			out << "Animation Width: " << anim.width << "\n";
			out << "Animation Height: " << anim.height << "\n";
			out << "Animation Done: " << anim.animationIsDone << "\n";
			return out;
		}
	};
	sf::Sprite& animationComponentSprite; 
	sf::Texture& animationComponentTexture;
	// Fac cu pointer pentru a nu trebui sa supraincarc operatorul si sa pot schimba animatia daca e cazul
	std::map<std::string, Animation*> animationMapSheet;
	// Am nevoie de lastAnimation pentru a nu executa aceeasi animatie de mai multe ori deodata
	Animation* lastAnimation;
	// Am nevoie de o prioritate pentru a stii ce animatie trebuie executata prima: attack, left etc.
	Animation* priorityAnimation;
public:
	AnimationComponent(sf::Sprite& animationComponentSprite, sf::Texture& animationComponentTexture);
	virtual ~AnimationComponent();
	void addAnimation(const std::string animationName,
		float animationTimer,
		int start_x, int start_y, int frames_x, int frames_y,
		int width, int height, const int& ENTITY_CODE);
	bool playAnimation(const std::string animationName, const float& deltaTime, const bool priority = false);
	AnimationComponent& operator=(const AnimationComponent& otherAnimation);
	friend std::ostream& operator<<(std::ostream& out, const AnimationComponent& animComp);
};