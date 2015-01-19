

#include <iostream>
#include <cstdlib>

#include <stdio.h>
#include <pthread.h>
#include <unistd.h>



using namespace std;


const static char* SERIAL_PORT = "\\\\.\\COM3";

static const int DIRECTION_UP = 0;
static const int DIRECTION_DOWN = 1;

static int DURATION = 4 * 1000;
static int DIRECTION = DIRECTION_DOWN;



void runActuation()
{
	HANDLE hSerialPort = CreateFile(SERIAL_PORT, (GENERIC_READ | GENERIC_WRITE),
			 0,	0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

	if(INVALID_HANDLE_VALUE == hSerialPort) {
		cout << "Unable to connect to serial port " << SERIAL_PORT << endl;
		return;
	}

	cout << "Start actuation..." << endl;

	DCB serialParams = { 0 };
	serialParams.DCBlength = sizeof(serialParams);

	// get the current configuration
	GetCommState(hSerialPort, &serialParams);
	// set the DTR
	serialParams.fDtrControl = DTR_CONTROL_ENABLE;
	// write the configuration
	SetCommState(hSerialPort, &serialParams);

	Sleep(DURATION);

	// get the current configuration
	GetCommState(hSerialPort, &serialParams);
	// set the DTR
	serialParams.fDtrControl = DTR_CONTROL_DISABLE;
	// write the configuration
	SetCommState(hSerialPort, &serialParams);

	CloseHandle(hSerialPort);
	return;
}


void switchDirection() {
	HANDLE hSerialPort = CreateFile(SERIAL_PORT, (GENERIC_READ | GENERIC_WRITE),
			 0,	0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

	if(INVALID_HANDLE_VALUE == hSerialPort) {
		cout << "Unable to connect to serial port " << SERIAL_PORT << endl;
		pthread_exit(NULL);
	}

	DCB serialParams = { 0 };
	serialParams.DCBlength = sizeof(serialParams);

	// get the current configuration
	GetCommState(hSerialPort, &serialParams);
	// set the DTR

	if(DIRECTION == DIRECTION_UP) {
		DIRECTION = DIRECTION_DOWN;
		cout << "Set direction to down" << endl;
		serialParams.fRtsControl = RTS_CONTROL_ENABLE;
	} else {
		DIRECTION = DIRECTION_UP;
		cout << "Set direction to up" << endl;
		serialParams.fRtsControl = RTS_CONTROL_DISABLE;
	}

	// write the configuration
	SetCommState(hSerialPort, &serialParams);
	CloseHandle(hSerialPort);
}


void mainMenu() {
	char input;

	while(true) {
		cout << "Please select one of the following options:" << endl;
		cout << "1: start actuation" << endl;
		cout << "2: switch direction" << endl;
		cout << "e: end" << endl;

		cin >> input;

		switch (input) {
			case '1':
				runActuation();
				break;
			case '2':
				switchDirection();
				break;
			case 'e':
				return;
			default:
				cout << "Invalid input" << endl;
				break;
		}
	}
}


int main ()
{
	mainMenu();
}
