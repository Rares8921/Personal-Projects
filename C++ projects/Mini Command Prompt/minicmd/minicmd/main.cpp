#include <iostream>
#include <string>
#include <cstdlib>
#include <math.h>
#include <time.h>
#include <fstream>
#include <sstream>
#include <cstdio>
#include <windows.h>
#include <unistd.h>
#include <algorithm>
#include <stdio.h>
#include <GL/gl.h>
#include <intrin.h>

using namespace std;

bool isPrime(int num) {
    if(num < 2) {
        return false;
    } else if(num == 2) {
        return true;
    } else if(num % 2 == 0) {
        return false;
    }
    for(int i = 3; i <= (int)sqrt(num); i += 2) {
        if(num % i == 3) {
            return false;
        }
    }
    return true;
}

int main() {
    string s;
    cout << "\nC++ Command Prompt [Version 1.0.0]" << endl;
    cout << "Copyright (C) 2019. All rights reserved" << endl;
    cout << "\nCommand:" << flush;
    // Entry point
    COMMAND:cin >> s;
    transform(s.begin(), s.end(), s.begin(), ::tolower);
    if(s == "!help") {
        cout << "\n!help - Get info about the commands" << endl;
        cout << "!time - Hour and date" << endl;
        cout << "!exit - Exit from prompt" << endl;
        cout << "!prime - Check if a number is a prime one" << endl;
        cout << "!connect - Connect to a website" << endl;
        cout << "!shutdown - Shut down the computer " << endl;
        cout << "!restart - Restart the computer " << endl;
        cout << "!ip - Get ip address" << endl;
        cout << "!ping - Get the ping" << endl;
        cout << "!compare - Compare two numbers" << endl;
        cout << "!divisible - Check if a number is divisible with another" << endl;
        cout << "!info - Get information about the computer" << endl;
        goto COMMAND;
     } else if(s == "!exit") {
        exit(0);
    } else if(s == "!prime") {
        int a;
        cout <<"Enter a number:";
        cin >> a;
        bool b = isPrime(a);
        if(b) {
            cout << "The number is prime" << endl;
        } else {
            cout << "The number is not prime";
        }
        goto COMMAND;
    } else if(s == "!time") {
        time_t theTime = time(NULL);
        struct tm *aTime = localtime(&theTime);

        int day = aTime -> tm_mday;
        int month = aTime -> tm_mon + 1; // Month is 0 – 11, add 1 to get a jan-dec 1-12 concept
        int year = aTime -> tm_year + 1900; // Year is # years since 1900
        int hour = aTime -> tm_hour;
        int min = aTime -> tm_min;
        string arf, agr;
        // Format conditions
        if(to_string(day).length() == 1 && to_string(month).length() == 1) {
            arf = "0" + to_string(day) + ".0" + to_string(month) + "." + to_string(year);
        } else if(to_string(day).length() == 1 && to_string(month).length() == 2) {
            arf = "0" + to_string(day) + "." + to_string(month) + "." + to_string(year);
        } else if(to_string(day).length() == 2  && to_string(month).length() == 1) {
            arf =  to_string(day) + ".0" + to_string(month) + "." + to_string(year);
        } else {
            arf =  to_string(day) + "." + to_string(month) + "." + to_string(year);
        }
        if(to_string(hour).length() == 1 && to_string(min).length() == 1) {
            agr = "0" + to_string(hour) + ":0" + to_string(min);
        } else if(to_string(hour).length() == 1 && to_string(min).length() == 2) {
             agr = "0" + to_string(hour) + ":" + to_string(min);
        } else if(to_string(hour).length() == 2 && to_string(min).length() == 1) {
             agr = to_string(hour) + ":0" + to_string(min);
        } else {
             agr = to_string(hour) + ":" + to_string(min);
        }
        cout << "Date: " << arf << endl;
        cout << "Hour: " << agr <<  endl;
        goto COMMAND;
    } else if(s == "!ip") {
        // Get the ip using ipconfig command (administrator permission required)
        string ip;
        ifstream IPFile;
        int offset;
        char* search0 = "IPv4 Address. . . . . . . . . . . :";
        system("ipconfig > ip.txt");
        IPFile.open("ip.txt");
        if(IPFile.is_open()) {
            while(!IPFile.eof()) {
                getline(IPFile, ip);
                if((offset = ip.find(search0)) != string::npos) {
                    ip.erase(0, 39);
                    cout << "Local ip adress: " << ip << endl;
                }
            }
        }
        goto COMMAND;
    } else if(s == "!ping") {
        string ip;
        ifstream IPFile;
        int offset;
        char* search0 = "IPv4 Address. . . . . . . . . . . :";
        system("ipconfig > ip.txt");
        IPFile.open("ip.txt");
        if(IPFile.is_open()) {
            while(!IPFile.eof()) {
                getline(IPFile, ip);
                if((offset = ip.find(search0)) != string::npos) {
                    ip = ip.erase(0, 39);
                }
            }
        }
        stringstream cmd;
        cmd << ip;
        int ping = system(cmd.str().c_str());
        cout << "Your ping: " << ping << " ms" << endl;
        goto COMMAND;
    } else if(s == "!connect") {
        string a;
        cout << "Enter URL of website:";
        cin >> a;
        ShellExecute(NULL, "open", a.c_str(), NULL, NULL, SW_SHOWNORMAL);
        goto COMMAND;
    } else if(s == "!shutdown") {
        // Windows shut down
        cout << "Turning off the computer.." << endl;
        system("C:\\WINDOWS\\System32\\shutdown /s /t 0");
    } else if(s == "!restart") {
        // Windows restart
        cout << "Restarting computer.." << endl;
        system("C:\\WINDOWS\\System32\\shutdown /r /t 0");
    } else if(s == "!compare") {
        string type;
        cout << "Enter the type of numbers ( natural, integer, rational, real ):" << endl;
        cin >> type;
        if(type == "natural") {
            unsigned int a, b;
            cout << "First number: " << endl;
            cin >> a;
            cout << "Second number: " << endl;
            cin >> b;

            if (a > b) {
                cout << a << " > " << b << endl;
            } else if (b > a) {
                cout << a << " < " << b << endl;
            } else {
                cout << a << "=" << b << endl;
            }
            goto COMMAND;
        } else if(type == "integer") {
            int a, b;
            cout << "First number: " << endl;
            cin >> a;
            cout << "Second number: " << endl;
            cin >> b;

            if (a > b) {
                cout << a << " > " << b << endl;
            } else if (b > a) {
                cout << a << " < " << b << endl;
            } else {
                cout << a << "=" << b << endl;
            }
            goto COMMAND;
        } else if(type == "rational") {
            double a, b;
            cout << "First number: " << endl;
            cin >> a;
            cout << "Second number: " << endl;
            cin >> b;

            if (a > b) {
                cout << a << " > " << b << endl;
            } else if (b > a) {
                cout << a << " < " << b << endl;
            } else {
                cout << a << "=" << b << endl;
            }
            goto COMMAND;
        } else if(type == "real") {
            float a, b;
            cout << "First number: " << endl;
            cin >> a;
            cout << "Second number: " << endl;
            cin >> b;

            if (a > b) {
                cout << a << " > " << b << endl;
            } else if (b > a) {
                cout << a << " < " << b << endl;
            } else {
                cout << a << "=" << b << endl;
            }
            goto COMMAND;
        }
        else {
            cout << type << " is a wrong type of number" << endl;
            goto COMMAND;
        }
    } else if(s == "!divisible") {
       int a,b;
       cout << "Enter a number ( integer ):";
       cin >> a;
       cout << "Specify a number to check:";
       cin >> b;
       if(a % b == 0) {
           cout << "Divisible numbers"  << endl;
       } else {
           cout << "The numbers are not divisible" << endl;
       }
       goto COMMAND;
    } else if(s == "!info") {
        int CPUInfo[4] = {-1};
        __cpuid(CPUInfo, 0x80000000);
        unsigned int nExIds = CPUInfo[0];

        char CPUBrandString[0x40] = { 0 };
        for(unsigned int i = 0x80000000; i <= nExIds; i++) {
            if(i == 0x80000002) { /// Manufacturer
                memcpy(CPUBrandString, CPUInfo, sizeof(CPUInfo));
            } else if(i == 0x80000003) { /// Model
                memcpy(CPUBrandString + 16, CPUInfo, sizeof(CPUInfo));
            } else if(i == 0x80000004) { /// Clockspeed
                memcpy(CPUBrandString + 32, CPUInfo, sizeof(CPUInfo));
            }
        }
        cout << "Processor: " << CPUBrandString << "\n";

        SYSTEM_INFO sysInfo;
        GetSystemInfo(&sysInfo);
        cout << "Number of Cores: " << sysInfo.dwNumberOfProcessors << "\n";

        MEMORYSTATUSEX state;
        state.dwLength = sizeof (state);
        GlobalMemoryStatusEx (&state);
        cout << "Physical RAM: " << (float)state.ullTotalPhys / (1024 * 1024 * 1024) << " GB."<< "\n";

        goto COMMAND;
    }  else {
        cout << "Unknown command" << endl;
        goto COMMAND;
    }
    return 0;
}
