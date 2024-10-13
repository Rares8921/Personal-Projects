#include "SkillComponent.h"
#include <iostream> // std::cout

SkillComponent::SkillComponent() {
	skills.push_back(Skill(SKILL_TYPES::CONSTITUTION));
	skills.push_back(Skill(SKILL_TYPES::STRENGTH));
	skills.push_back(Skill(SKILL_TYPES::MAGIC_POWER));
	skills.push_back(Skill(SKILL_TYPES::PHYSICAL_DEFENCE));
	skills.push_back(Skill(SKILL_TYPES::MAGIC_DEFENCE));
}

SkillComponent::~SkillComponent() {}

const int SkillComponent::getSkill(const int skill) const {
	if (skills.empty() || skill < 0 || skill >= skills.size()) {
		std::cout << "Skill-ul mentionat nu exista!\n";
		return -1;
	}
	return skills[skill].getCurrentLevel();
}

const void SkillComponent::gainExp(const int skill, const int exp) {
	if (skills.empty() || skill < 0 || skill >= skills.size()) {
		std::cout << "Skill-ul mentionat nu exista!\n";
		return;
	}
	skills[skill].updateExp(exp);
}

SkillComponent& SkillComponent::operator=(const SkillComponent& other) {
	if (this != &other) {
		skills = other.skills;
	}
	return *this;
}

std::ostream& operator<<(std::ostream& out, const SkillComponent& skillComp) {
	for (const SkillComponent::Skill& skill : skillComp.skills) {
		out << skill; 
	}
	return out;
}
