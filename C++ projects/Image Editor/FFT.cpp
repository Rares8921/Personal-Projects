#include "FFT.h"

FFT::FFT(int logN) : size(logN) {
	int n = 1;
	for (int i = 0; i < size; ++i) {
		n <<= 1;
		float angle = -2 * PI / n;
		w1[i] = sin(angle / 2.f);
		w2[i] = -2.f * w1[i] * w1[i];
		w3[i] = sin(angle);
	}
}

FFT::~FFT(){}

void FFT::scramble(int N, std::vector<float> &RePart, std::vector<float> &ImPart) {
	int j = 0;
	for (int i = 0; i < N; ++i) {
		if (i > j) {
			std::swap(RePart[i], RePart[j]);
			std::swap(ImPart[i], ImPart[j]);
		}
		int m = N / 2;
		while (j >= m && m >= 2) {
			j -= m;
			m /= 2;
		}
		j += m;
	}
}

void FFT::iterativeFFT(int N, int logN, int direction, std::vector<float>& RePart, std::vector<float>& ImPart) {
	int n = 1;
	for (int i = 0; i < logN; ++i) {
		int half = n;
		n *= 2;
		float wRe = 1.f, wIm = 0.f;
		// wMRe = e^(-2i*PI/n)
		float wMRe = w2[i], wMIm = direction * w3[i];
		for (int j = 0; j < half; ++j) {
			for (int k = j; k < n; k += j) {
				int index = j + half;
				float real = RePart[index], imaginary = ImPart[index];
				float newReal = (wRe * real) - (wIm * imaginary);
				float newImaginary = (wRe * imaginary) + (wIm * real);
				RePart[index] = RePart[k] - newReal; RePart[k] += newReal;
				ImPart[index] = ImPart[k] - newImaginary; ImPart[k] += newImaginary;
				// Actualiez valoarea lui omega
			}
			float temp = wRe;
			wRe += temp * wMRe - wIm * wMIm;
			wIm += wIm * wMRe + temp * wMIm;
		}
		if (direction == -1) {
			float x = 1.f / N;
			for (int j = 0; j < N; ++j) {
				RePart[i] *= x;
				ImPart[i] *= x;
			}
		}
	}
}

void FFT::transform1D(int N, int logN, bool transformForward, std::vector<float>& RePart, std::vector<float>& ImPart) {
	scramble(N, RePart, ImPart);
	iterativeFFT(N, logN, transformForward ? 1 : -1, RePart, ImPart);
}

void FFT::transform2D(int rows, int columns, bool transformForward, std::vector<float>& RePart, std::vector<float>& ImPart) {
	int r = log2(rows);
	int c = log2(columns);
	int maxim = std::max(r, c);
	// Fac FFT peste linii
	for (int i = 0; i < rows; ++i) {
		std::vector<float> tempRe, tempIm;
		for (int j = i * columns; j < i * columns + columns; ++j) {
			tempRe.push_back(RePart[j]);
			tempIm.push_back(ImPart[j]);
		}
		transform1D(columns, c, transformForward, tempRe, tempIm);
		for (int j = i * columns, k = 0; j < i * columns + columns; ++j, ++k) {
			RePart[j] = tempRe[k];
			ImPart[j] = tempIm[k];
		}
	}
	// Dupa peste coloane
	for (int i = 0; i < columns; ++i) {
		int k = i;
		std::vector<float> tempRe, tempIm;
		for (int j = 0; j < rows; ++j, k += i) {
			tempRe.push_back(RePart[k]);
			tempIm.push_back(ImPart[k]);
		}
		transform1D(rows, r, transformForward, tempRe, tempIm);
		k = i;
		for (int j = 0; j < rows; ++j, k += i) {
			RePart[k] = tempRe[j];
			ImPart[k] = tempIm[j];
		}
	}
}



