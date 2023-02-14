#include "../include/SocketReader.h"

#include <thread>
#include <chrono>


SocketReader::SocketReader(StompProtocol &protocol,User &user) : protocol(protocol), user(user)
{}

void SocketReader::Run()
{
    while(!user.shouldTerminate())
    {
        while(!user.isConnected()){
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }
        std::string response;

        // get a response from the server:
        if (!user.getConnectionHandler().getLine(response)) {
            std::cout << ">>> Disconnected" << std::endl;
            std::cout << ">>> EXIT" << std::endl;
            break;
        }
        
        // parse the response:
        std::string output = protocol.handleStompMessageFromServer(response);

		// print the response:
        if(output == "")
            continue;
        // std::cout << ">>> Recived from server " << output.length() << " bytes:" << std::endl;
        std::cout << output <<  std::endl;
        if (output == "ERROR") {
            std::cout << ">>> Exit" << std::endl;
        }
    }

    
    // std::cout << ">>> SocketReader terminated" << std::endl;
}