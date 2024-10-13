#pragma once

#include <vector>
#include <cmath>

constexpr float PI = 3.141592653589793;

// Folosesc FFT pentru a reduce time complexity-ul de la O(N^2) la O(N*log(N))
// https://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm
class FFT {
private:
	std::vector<float> w1, w2, w3;
	int size;
public:
	FFT(int logN);
	virtual ~FFT();
	void scramble(int N, std::vector<float> &RePart, std::vector<float> &ImPart);
	//https://en.wikipedia.org/wiki/Butterfly_diagram
	void iterativeFFT(int N, int logN, int direction, std::vector<float>& RePart, std::vector<float>& ImPart);
	void transform1D(int N, int logN, bool transformForward, std::vector<float>& RePart, std::vector<float>& ImPart);
	void transform2D(int rows, int columns, bool transformForward, std::vector<float>& RePart, std::vector<float>& ImPart);
};

