#include "../include/ConnectionHandler.h"
#include "../include/StompProtocol.h"
#include "../include/KeyboardReader.h"
#include "../include/SocketReader.h"
#include "../include/User.h"

#include <stdlib.h>
#include <thread>

using namespace std;

int main(int argc, char *argv[]) {

	// login 127.0.0.1:7777 eran qqqqqqq

	std::cout << "Starting client" << std::endl;
	while(true) {
	// set objects:
		User user;
		StompProtocol protocol(user);
		KeyboardReader keyboardReader(protocol, user);
		SocketReader socketReader(protocol, user);

		// run threads:
		std::thread keyboardThread(&KeyboardReader::Run, &keyboardReader);
		std::thread socketThread(&SocketReader::Run, &socketReader);
		

		// wait for the threads to finish:
		keyboardThread.join();
		socketThread.join();
	}

	return 0;
}