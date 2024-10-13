#pragma once

#include <vector>
#include "SFML\Graphics.hpp"

// Definitie google:
// Constitution (also called toughness, endurance, or vigor)
// is an attribute that represents a character's physical resilience, stamina, hardiness, and good health.
enum SKILL_TYPES {
	CONSTITUTION = 0,
	STRENGTH,
	MAGIC_POWER,
	PHYSICAL_DEFENCE,
	MAGIC_DEFENCE
};

class SkillComponent {
private:
	class Skill {
	private:
		int type;
		int currentLevel, maxLevel;
		int currentExp, nextExp;
	public:
		Skill(int type) : type(type) {
			currentLevel = 1;
			maxLevel = 100;
			currentExp = 0;
			nextExp = 0;
		}

		inline const int& getType() const { return type; }
		inline const int& getCurrentLevel() const { return currentLevel; }
		inline const int& getCurrentExp() const { return currentExp; }
		inline const int& getNextExp() const { return nextExp; }

		inline void setCurrentLevel(const int level) { currentLevel = level; }
		inline void setMaxLevel(const int level) { maxLevel = level; }

		void updateExp(const int exp) {
			if (exp < 0) {
				currentExp = std::max(currentExp - exp, 0);
			}
			else {
				currentExp += exp;
				updateLevel();
			}
		}

		void updateLevel() {
			if (currentLevel < maxLevel) {
				while (currentExp >= nextExp) {
					if (currentLevel < maxLevel) {
						++currentLevel;
						nextExp = (currentLevel * currentLevel + 12 * currentLevel);
					}
				}
			}
		}

		~Skill() {}

		Skill& operator=(const Skill& other) {
			if (this != &other) {
				type = other.type;
				currentLevel = other.currentLevel;
				maxLevel = other.maxLevel;
				currentExp = other.currentExp;
				nextExp = other.nextExp;
			}
			return *this;
		}


		friend std::ostream& operator<<(std::ostream& out, const Skill& skill) {
			out << "Type: " << skill.type << "\n";
			out << "Current Level: " << skill.currentLevel << "\n";
			out << "Max Level: " << skill.maxLevel << "\n";
			out << "Current Exp: " << skill.currentExp << "\n";
			out << "Next Exp: " << skill.nextExp << "\n";
			return out;
		}
	};
	std::vector<Skill> skills;
public:
	SkillComponent();
	virtual ~SkillComponent();

	const int getSkill(const int skill) const;
	const void gainExp(const int skill, const int exp);
	SkillComponent& operator=(const SkillComponent& other);
	friend std::ostream& operator<<(std::ostream& out, const SkillComponent& skillComp);
};

