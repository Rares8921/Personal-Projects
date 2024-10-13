#pragma once

#include <string>

class OCR {
private:
	std::string imagePath;
public:
	void performOCR();
	void setImagePath(std::string _imagePath);
};

