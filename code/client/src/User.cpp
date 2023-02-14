#include "../include/User.h"
#include "../include/StompParser.h"
#include "../include/ConnectionHandler.h"
#include "../include/Game.h"

#include <string>
#include <vector>
#include <unordered_map>
#include <utility>

using std::pair;


User::User() : receiptIdMaker(0), userName(""), password(""), isConnectionHandlerConnected(false), isLogedIn(false), terminate(false), gameNames(), games(), receiptIdToMessage(), connectionHandler()
{
}

User::~User()
{
    gameNames.clear();
    games.clear();
}

int User::getReceiptId(string receiptOutput)
{
    receiptIdMaker++;
    receiptIdToMessage[receiptIdMaker] = receiptOutput;
    return receiptIdMaker;
}

std::string User::getUserName() 
{
    return userName;
}

void User::setUserName(std::string name)
{
    userName = name;
}

std::string User::getPassword()
{
    return password;
}

void User::setPassword(std::string pass)
{
    password = pass;
}

bool User::isConnected()
{
    return isConnectionHandlerConnected;
}

void User::setConnected(std::vector<std::string> &parsedCommand)
{

    // maybe host is -  std::string host = "stomp.cs.bgu.ac.il";
    std::vector<std::string> host_port = StompParser::parseCommand(parsedCommand.at(1),':');
	std::string host = host_port.at(0);
	short port = std::stoi(host_port.at(1));

    // try to connect to the server:
    connectionHandler.setHostAndPort(host, port);
    // connectionHandler = ConnectionHandler(host, port);


    if (!connectionHandler.connect()) {
        std::cerr << "ERROR: can't connect to " << host << ":" << port << std::endl;
        // connectionHandler = nullptr;
    }
    else{
        isConnectionHandlerConnected = true;
        userName = parsedCommand.at(2);
        password = parsedCommand.at(3);
    }
}

bool User::isLoggedIn()
{
    return isLogedIn;
}

void User::setLoggedIn(bool loggedIn)
{
    isLogedIn = loggedIn;
}

ConnectionHandler &User::getConnectionHandler()
{
    return connectionHandler;
}

void User::subscribe(string gameName)
{
    vector<string> parsedNames = StompParser::parseCommand(gameName,'_');
    gameNames.push_back(gameName);
    games.push_back(Game(parsedNames.at(0),parsedNames.at(1)));
}

int User::getSubIdOfGame(string gameName)
{
    return indexOf(gameName); // the subId is the index of the game in the vector
}

void User::unsubscribe(string gameName)
{
    int index = indexOf(gameName);
    games.erase(games.begin()+index);
    gameNames.erase(gameNames.begin()+index);
}

void User::unsubscribeAll()
{
    gameNames.clear();
    games.clear();
}

void User::disconnect()
{
    isConnectionHandlerConnected = false;
    isLogedIn = false;
    gameNames.clear();
    games.clear();
    receiptIdToMessage.clear();
    userName = "";
    password = "";
    connectionHandler.close();
}

void User::addReceiptIdToMessage(int receiptId, string message)
{
    receiptIdToMessage[receiptId] = message;
}

std::string User::getReceiptOutput(int receiptId)
{
    return receiptIdToMessage[receiptId];
}

void User::updateGame(string gameName, string body)
{
    games.at(indexOf(gameName)).updateGame(body);
}

void User::send(string message)
{   
    if(message == "")
    {
        std::cout << ">>> Invalid command" << std::endl;
        return;
    }
    if (!getConnectionHandler().sendLine(message)) {
        std::cout << ">>> Disconnected" << std::endl;
        std::cout << ">>> EXIT" << std::endl;
        // shouldTerminate = true;
    }
}

void User::summreizeGame(string gameName,string userName,string fileName)
{
    // string completePath = "../data/";
    // fileName = completePath + fileName;
    games.at(indexOf(gameName)).summerizeGame(userName,fileName);

}


int User::indexOf(string gameName)
{
    int index = -1;
    for (unsigned int i = 0; i < games.size(); i++) {
        if (gameNames[i] == gameName) {
            index = i;
            break;
        }
    }
    return index;
}

bool User::shouldTerminate()
{
    return terminate;
}

void User::setTerminate(bool _terminate)
{
    terminate = _terminate;
}
