#include "OpacityAdjust.h"
#include <cmath>
#include <iostream>

void OpacityAdjust::adjustPixel(Color& imagePixel, int value) {
	if (imagePixel.a != 0) {
		value = std::min(255, value);
		value = std::max(0, value);
		imagePixel.a = value;
	}
}

void OpacityAdjust::adjustImage(Color*& imagePixels, int height, int width, int value) {
	// Value este de la 0 la 100, trebuie transformat astfel incat sa fie de la 0 la 255
	// 0 ~ 0 si 100 ~ 255
	// Astfel, voi crea o generare:
	// In 101 pasi, voi creste cu 2.05 valoarea alpha-ul daca pasul este par, iar cu 3.05 daca este impar
	// Dupa 101 de pasi, deci pentru value = 100, valoarea alpha-ului va fi chiar 255
	// Apoi voi rotunji valoarea pentru a avea alpha-ul in functie de valoarea din slidebar
	value = std::min(value, 100);
	value = std::max(value, 0);
	float newValue = (value / 2.f * 2.05f) + ((value + 1) / 2.f * 3.05);
	int truncatedValue = round(newValue);
	for (int i = 0; i < height; ++i) {
		for (int j = 0; j < width; ++j) {
			adjustPixel(imagePixels[i * height + j], truncatedValue);
		}
	}
}
