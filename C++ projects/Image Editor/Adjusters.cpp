#include "Adjusters.h"

int Adjusters::rgbToHsv(int code) {
    float newCode = code / 255.f;
    float fractionaryPart = newCode - (int)newCode;
    if (fractionaryPart >= 0.5) {
        return (int)newCode + 1;
    }
    return (int)newCode;
}

int Adjusters::maxim(int a, int b) {
    return a >= b ? a : b;
}

int Adjusters::maxim(int a, int b, int c) {
    return maxim(a, maxim(b, c));
}

int Adjusters::minim(int a, int b) {
    return a <= b ? a : b;
}

int Adjusters::minim(int a, int b, int c) {
    return minim(a, minim(b, c));
}
