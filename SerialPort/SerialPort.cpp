

#include <iostream>

#include <thread>
#include <chrono>

#include <stdio.h>
#include <windows.h>


using namespace std;


const static char* SERIAL_PORT = "\\\\.\\COM3";

static int MILL_SECONDS = 1000;
// controls the DTR line
static double FREQUENCY = 0.5;
// controls the RTS line
static int RTS_LINE = 1;
// stops the serial port thread
static bool running = true;



void run() {
	HANDLE hSerialPort = CreateFile(SERIAL_PORT, (GENERIC_READ | GENERIC_WRITE),
			 0,	0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

	if(INVALID_HANDLE_VALUE == hSerialPort) {
		cout << "Unable to connect to serial port " << SERIAL_PORT << endl;
		return;
	}

	while(running) {
		DCB serialParams = { 0 };
		serialParams.DCBlength = sizeof(serialParams);

		// get the current configuration
		GetCommState(hSerialPort, &serialParams);

		// set the DTR
		if(DTR_CONTROL_ENABLE == serialParams.fDtrControl) {
			serialParams.fDtrControl = DTR_CONTROL_DISABLE;
		} else {
			serialParams.fDtrControl = DTR_CONTROL_ENABLE;
		}

		// write the configuration
		SetCommState(hSerialPort, &serialParams);

		// thread waits for X milliseconds
		chrono::milliseconds dura((int) (1/FREQUENCY) * MILL_SECONDS);
		this_thread::sleep_for(dura);
	}

	CloseHandle(hSerialPort);
}


void mainMenu() {
	char input;

	while(true) {
		cout << "Please select one of the following options:" << endl;
		cout << "1: change the frequency (DTR)" << endl;
		cout << "2: change the ???? (RTS)" << endl;
		cout << "e: end the program" << endl;

		input = getchar();
		cout << endl;

		switch (input) {
			case '1':
				cout << "Set the frequency in Hz (DTR)" << endl;
				cin >> FREQUENCY;
				break;
			case '2':
				cout << "Set the ???? in Hz (RTS)" << endl;
				cin >> RTS_LINE;
				break;
			case 'e':
				// exit the program
				running = false;
				return;
			default:
				cout << "Invalid input" << endl;
				break;
		}
	}
}


int main() {
	// starts the serial port thread
	thread serialPortThread(run);
	// menu for changing the frequency
	mainMenu();
	// don't stop the program until the thread has ended
	serialPortThread.join();
}

