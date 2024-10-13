#pragma once

#include <string>
#include <opencv2/core/base.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/dnn/dnn.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>

using namespace cv;

class AutomaticColorization {
private:
	std::string protoFile, weightsFile, sourcePath;
	Mat sourceImage;
	const int height = 224, width = 224;
	float hullPoints[626];
	dnn::Net ann;
	// Cele doua layere pentru ANN
	Ptr<dnn::Layer> firstLayer, secondLayer;
	Mat lab, L, input;
	Mat inputBlob, result;
	void initHullPoints();
public:
	AutomaticColorization() = default;
	~AutomaticColorization() = default;
	void setSourcePath(std::string _sourcePath);
	void colorize();
};

