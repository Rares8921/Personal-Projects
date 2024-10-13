#pragma once

#include "Includes.h"
#include <memory>
#include <iostream>

enum FILTER_TYPES {
	GRAYSCALE = 0,
	GRAYSCALE_ALPHA,
	INVERTED,
	FLIP,
	VERTICAL_FLIP,
	LEFT_ROTATE,
	RIGHT_ROTATE,
	LIGHT,
	SHADE,
	SHINE,
	CHROME,
	FADE,
	DIFFOFGAUSSIANS,
	ERODE,
	FLARE,
	SPARKLE,
};

class Filter {
private:
	// Red, Green, Blue, Alpha
	int r, g, b, a;
	// Alpha - opacitatea fiecarui pixel. 
	// Se foloseste alpha composing pentru zonele transparente si anti-aliasing pentru muchiile acestor zone.
	// Alpha composing - combin o imagine cu un background pentru a obtine aspectul transparent
protected:
	std::shared_ptr<Image> currentImage, newImage;
	int height, width;
public:
	Filter();
	Filter(Image inputImage);
	virtual ~Filter() = default;
	// Limitez valoarea pentru rgba

	template<typename numType>
	numType clampValue(numType value);
	template<typename numType>
	numType clampValue(numType value, numType infLim, numType maxLim);

	const int& getR() const;
	const int& getG() const;
	const int& getB() const;
	const int& getA() const;

	const int& getRGBEncoding(int r, int g, int b) const;
	const int& getRGBAEncoding(int r, int g, int b, int a) const;

	const int& getGreen(int color) const;
	const int& getRed(int color) const;
	const int& getBlue(int color) const;
	const int& getOpacity(int color) const;

	// "Fortez" ca fiecare clasa ulterioara de filtru sa aiba o proprie implementare a funtiei applyFilter
	// Pentru ca voi actualiza pixel cu pixel iar codul va fi rescris 
	virtual void applyFilterToPixel(Color& color) = 0;
	virtual void applyFilterToImage(Color*& imagePixels, int height, int width) = 0;
};
