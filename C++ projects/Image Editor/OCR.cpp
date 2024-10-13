#include "OCR.h"
#include <tesseract/baseapi.h>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <iostream>

using namespace cv;

void OCR::performOCR() {
	tesseract::TessBaseAPI* ocr = new tesseract::TessBaseAPI();
    ocr->Init(NULL, "eng", tesseract::OEM_DEFAULT);
    ocr->SetPageSegMode(tesseract::PSM_AUTO);
    Mat inputImage = cv::imread(imagePath, IMREAD_COLOR);
    imshow("Input", inputImage);
    ocr->SetImage(inputImage.data, inputImage.cols, inputImage.rows, 3, inputImage.step);
    std::cout << std::string(ocr->GetUTF8Text()) << "\n";
    ocr->End(); //Free
}

void OCR::setImagePath(std::string _imagePath) {
	imagePath = _imagePath;
}

/*
    #include "OCR.h"

void OCR::getData() {
	IplImage* src_image;
	IplImage prs_image;
	CvMat row, data;
	char file[255];
	int i, j;
	for (i = 0; i < classes; ++i) {
		for (j = 0; j < samples; ++j) {
			//Load file
			if (j < 10)
				imagePath = "0" + std::to_string(j) + std::to_string(i);
			else
				imagePath = std::to_string(j) + std::to_string(i);
			IplImage tempImg = cvIplImage(imread(imagePath, 0));
			src_image = &tempImg;
			if (!src_image) {
				std::cout << "Eroare la incarcarea imaginii din path-ul: " << imagePath << "\n";
				//exit(-1);
			}
			//process file
			Preprocessing preprocess;
			std::shared_ptr<IplImage> sourceImage(src_image);
			prs_image = preprocess.preProcess(sourceImage, size, size);

			//Set class label
			cvGetRow(trainClasses, &row, i * samples + j);
			cvSet(&row, cvRealScalar(i));
			//Set data 
			cvGetRow(trainData, &row, i * samples + j);

			IplImage* img = cvCreateImage(cvSize(size, size), IPL_DEPTH_32F, 1);
			//convert 8 bits image to 32 float image
			// Convert de la imaginea de 8 biti la imagine de 32 biti
			cvConvertScale(&prs_image, img, 0.0039215, 0);

			cvGetSubRect(img, &data, cvRect(0, 0, size, size));

			CvMat row_header, * row1;
			// Acum transform totul la vector
			row1 = cvReshape(&data, &row_header, 0, 1);
			cvCopy(row1, &row, NULL);
		}
	}
}

void OCR::trainModel() {
	kNearestNeighbours = KNearest::create();
	// trainData, trainClasses, 0, false, K 
}

OCR::OCR() {
	samples = 50;
	classes = 10;
	size = 40;

	trainData = cvCreateMat(samples * classes, size * size, CV_32FC1);
	trainClasses = cvCreateMat(samples * classes, 1, CV_32FC1);

	getData();
	trainModel();
	testModel();
}

OCR::~OCR() {
	delete kNearestNeighbours;
}

float OCR::classifyData(std::shared_ptr<IplImage> trainImage, bool showResult)
{
	IplImage prs_image;
	CvMat data;
	CvMat* nearest = cvCreateMat(1, neighbours, CV_32FC1);
	float result;
	
	Preprocessing p;
	std::shared_ptr<IplImage> sharedImage(trainImage);
	prs_image = p.preProcess(sharedImage, size, size);

	
	IplImage* img32 = cvCreateImage(cvSize(size, size), IPL_DEPTH_32F, 1);
	cvConvertScale(&prs_image, img32, 0.0039215, 0);
	cvGetSubRect(img32, &data, cvRect(0, 0, size, size));
	CvMat row_header, * row1;
	row1 = cvReshape(&data, &row_header, 0, 1);
	

	result = kNearestNeighbours->findNearest(cvarrToMat(row1), neighbours, cvarrToMat(nearest));

	int accuracy = 0;
	for (int i = 0;i < neighbours;i++) {
		if (nearest->data.fl[i] == result)
			accuracy++;
	}
	float pre = 100 * ((float)accuracy / (float)neighbours);
	if (showResult == 1) {
		std::cout << result << " " << pre << " " << accuracy << " " << neighbours << "\n";
	}

	return result;
}

void OCR::testModel() {
	IplImage* src_image;
	IplImage prs_image;
	CvMat row, data;
	char file[255];
	int i, j;
	int error = 0;
	int testCount = 0;
	for (i = 0; i < classes; i++) {
		for (j = 50; j < 50 + samples; j++) {
			imagePath = std::to_string(j) + std::to_string(i);
			IplImage tempImg = cvIplImage(imread(file, 0));
			src_image = &tempImg;
			if (!src_image) {
				std::cout << "Eroare la incarcarea imaginii din path-ul: " << imagePath << "\n";
				//exit(-1);
			}
			//process file
			std::shared_ptr<IplImage> sourceImage(src_image);
			Preprocessing p;
			prs_image = p.preProcess(sourceImage, size, size);
			std::shared_ptr<IplImage> tempImage(&prs_image);
			float r = classifyData(tempImage, 0);
			if ((int)r != i)
				error++;

			testCount++;
		}
	}
	float totalError = 100 * (float)error / (float)testCount;
	std::cout << "Marja de eroare: " << totalError << "\n";
}

*/
