#pragma once


#include "opencv2/core/core.hpp"
#include "opencv2/core/core_c.h"
#include "opencv2/core/types_c.h"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"
#include "opencv2/imgproc/imgproc_c.h"
#include <stdio.h>
#include <ctype.h>

class Preprocessing {
private:
	void findX(std::shared_ptr<IplImage> source, int* min, int* max);
	void findY(std::shared_ptr<IplImage> source, int* min, int* max);
	CvRect findBoundingBox(std::shared_ptr<IplImage> source);
public:
	Preprocessing() = default;
	~Preprocessing() = default;
	IplImage preProcess(std::shared_ptr<IplImage> source, int width, int height);
};

