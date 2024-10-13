#pragma once

#include <opencv2/objdetect.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/core/base.hpp>

using namespace cv;

class BackgroundRemover {
protected:
	int dilateType, dilateSize;
	int errosionType, errosionSize;
	int largestArea, largestContourIndex;
	std::string sourcePath;
	Mat sourceImage, hsvImage, rgbImage, grayImage;
	Mat edges, dilateGrad, ditaledElement, floodFilled;
	Mat channel[3];
	Rect boundingRectangle;
	Mat largestContour;
	Mat modifiedImage;
	Mat sobel(Mat grayImage);
public:
	BackgroundRemover() = default;
	~BackgroundRemover() = default;
	void setSourcePath(std::string _sourcePath);
	void removeBackground();
};

