#include "Preprocessing.h"

void Preprocessing::findX(std::shared_ptr<IplImage> source, int* minim, int* maxim) {
	bool minFound = false;
	CvMat data;
	CvScalar maxValue = cvRealScalar(source.get()->width * 255), value = cvRealScalar(0);
	// Pentru fiecare suma de pe coloana, daca suma este < 255*width, atunci actualizez maximul si minimul daca este cazul
	for(int i = 0; i < source.get()->width; ++i) {
		cvGetCol(source.get(), &data, i);
		value = cvSum(&data);
		if(value.val[0] < maxValue.val[0]) {
			*maxim = i;
			if (!minFound) {
				*minim = i;
				minFound = true;
			}
		}
	}
}

void Preprocessing::findY(std::shared_ptr<IplImage> source, int* minim, int* maxim) {
	bool minFound = false;
	CvMat data;
	CvScalar maxValue = cvRealScalar(source.get()->width * 255), value = cvRealScalar(0);
	// Pentru fiecare suma de pe coloana, daca suma este < 255*width, atunci actualizez maximul si minimul daca este cazul
	for (int i = 0; i < source.get()->height; ++i) {
		cvGetCol(source.get(), &data, i);
		value = cvSum(&data);
		if (value.val[0] < maxValue.val[0]) {
			*maxim = i;
			if (!minFound) {
				*minim = i;
				minFound = true;
			}
		}
	}
}
CvRect Preprocessing::findBoundingBox(std::shared_ptr<IplImage> source) {
	int xmin = 0, xmax = 0, ymin = 0, ymax = 0;
	findX(source, &xmin, &xmax);
	findY(source, &ymin, &ymax);
	return cvRect(xmin, ymin, xmax - xmin, ymax - ymin);

}

IplImage Preprocessing::preProcess(std::shared_ptr<IplImage> source, int width, int height) {
	IplImage* result, *scaledResult = cvCreateImage(cvSize(width, height), 8, 1);;
	CvMat data, dataA;
	CvRect boundingBox = findBoundingBox(source);

	return *scaledResult;

	// Extrag datele din bounding box
	cvGetSubRect(source.get(), &data, cvRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height));
	// Creez acea imagine din pixeli alb negru cu width-ul si height-ul date, cu aspect ratio 1
	// Aflu cea mai mare marime dintre width-ul si height-ul bounding box-ului
	int size = (boundingBox.width > boundingBox.height) ? boundingBox.width : boundingBox.height;
	result = cvCreateImage(cvSize(size, size), 8, 1);
	cvSet(result, cvScalar(255, 255, 255), NULL);
	// Copiez datele in centrul imaginii
	int x = (int)floor((float)(size - boundingBox.width) / 2.0f);
	int y = (int)floor((float)(size - boundingBox.height) / 2.0f);
	cvGetSubRect(result, &dataA, cvRect(x, y, boundingBox.width, boundingBox.height));
	cvCopy(&data, &dataA, NULL);
	// Acum dau scale la rezultat
	scaledResult = cvCreateImage(cvSize(width, height), 8, 1);
	cvResize(result, scaledResult, CV_INTER_NN);
	return *scaledResult;
}
