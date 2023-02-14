#include "../include/StompProtocol.h"
#include "../include/SocketReader.h"

#include <thread>
#include <unordered_map>
using namespace std;
using std::string;

StompProtocol::StompProtocol(User &_user) : user(_user)
{
}

std::string StompProtocol::handleStompMessageFromServer(string message)
{
    // parse the message:
    std::unordered_map<std::string, std::string> parsedResponse;
    if(message.length() > 0 && message.at(0) == 'M')
        parsedResponse = StompParser::parse_stomp_report(message);
    else
        parsedResponse = StompParser::parse_stomp_message(message);
    std::string command = parsedResponse["title"];
    std::string output = "";

    // handle the message:
    if(command == "CONNECTED")
    {
        user.setLoggedIn(true);
        output = "Login successful";
    }
    else if (command == "ERROR")
    {
        output = parsedResponse["message"];
        user.send(handleLogout());
    }
    else if (command == "RECEIPT")
    {
        int receiptId = std::stoi(parsedResponse["receipt-id"]);
        output = user.getReceiptOutput(receiptId);
        if(output == "") // hapeens when the user has loggedout
            user.disconnect();
    } 
    else if (command == "MESSAGE")
    {

        std::string gameName = parsedResponse["destination"];
        user.updateGame(gameName, parsedResponse["body"]);
    } 

    // return the output:
    return output;
}

string StompProtocol::buildFrameFromKeyboardCommand(std::string userCommand)
{
    if(userCommand == "1")
        userCommand = "login 127.0.0.1:7777 admin admin";
    else if(userCommand == "2")
        userCommand = "join Germany_Japan";
    else if(userCommand == "3")
        userCommand = "report events1.json";
    else if(userCommand == "4")
        userCommand = "summary Germany_Japan admin summary04.txt";
    else if(userCommand == "5")
        userCommand = "exit Germany_Japan";
    else if(userCommand == "6")
        userCommand = "logout";
    else if(userCommand == "7")
        userCommand = "login 127.0.0.1:7777 eran admin";



    std::vector<std::string> parsedCommand = StompParser::parseCommand(userCommand,' ');
    std::string command = parsedCommand.at(0);
    std::string output = "";

    if(command != "login"){
        if(!user.isConnected())
        {
            std::cout << "User is not logged in" << std::endl;
            return "";
        }
    }

    if(command == "login") 
    {
        output = handleLogin(parsedCommand);
    } 
    else if (command == "logout")
    {
        output = handleLogout();
    } 
    else if (command == "join")
    {
        output = handleJoin(parsedCommand);
    } 
    else if (command == "report")
    {
        output = handleReport(parsedCommand);
    } 
    else if (command == "summary")
    {
        output = handleSummary(parsedCommand);
    } 
    else if (command == "exit")
    {
        output = handleExit(parsedCommand);
    } 

    return output;
}


string StompProtocol::handleLogin(std::vector<std::string> parsedCommand)
{

    // send error if user is already logged in
    if(user.isConnected())
    {
        std::cout << "User is already logged in" << std::endl;
        return "";
    }
    
    user.setConnected(parsedCommand);

    return "CONNECT\naccept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:" + user.getUserName() + "\npasscode:" + user.getPassword() + "\n\n" + '\0';
}

string StompProtocol::handleLogout()
{
    
    user.setTerminate(true);
    int receiptId = user.getReceiptId("");
    return "DISCONNECT\nreceipt:" + std::to_string(receiptId) + "\n\n" + '\0';
} 

string StompProtocol::handleJoin(std::vector<std::string> parsedCommand)
{
    user.subscribe(parsedCommand.at(1));
    int receiptId = user.getReceiptId("Joined channel " + parsedCommand.at(1));
    return "SUBSCRIBE\ndestination:/" + parsedCommand.at(1) + "\nid:" + std::to_string(user.getSubIdOfGame(parsedCommand.at(1))) + "\nreceipt:" + std::to_string(receiptId) + "\n\n" + '\0';
}

// exits from a game send unsubscribe frame
string StompProtocol::handleExit(std::vector<std::string> parsedCommand)
{
    int receiptId = user.getReceiptId("Exited channel " + parsedCommand.at(1));
    int subId = user.getSubIdOfGame(parsedCommand.at(1));

    if(subId == -1)
    {
        std::cout << "User is not subscribed to this game" << std::endl;
        return "";
    }
    user.unsubscribe(parsedCommand.at(1));
    return "UNSUBSCRIBE\nid:" + std::to_string(subId) + "\nreceipt:" + std::to_string(receiptId) + "\n\n" + '\0';
} 

string StompProtocol::handleReport(std::vector<std::string> parsedCommand)
{
    names_and_events gameEvents = parseEventsFile(parsedCommand.at(1));

    string teamA = gameEvents.team_a_name;
    string teamB = gameEvents.team_b_name;

    if(user.getSubIdOfGame(teamA + "_" + teamB) == -1)
    {
        std::cout << "User is not subscribed to this game" << std::endl;
        return "";
    }

    for (Event& e : gameEvents.events)
    {
        string output = "SEND\ndestination:/" + e.get_team_a_name() + "_" + e.get_team_b_name() + "\n\n" +
                        "user: " + user.getUserName() + "\n" +
                        "team a: " + e.get_team_a_name() + "\n" +
                        "team b: " + e.get_team_b_name() + "\n" +
                        "event name:" + e.get_name() + "\n" +
                        "time: " + std::to_string(e.get_time()) + "\n" +
                        "general game updates: \n" + StompParser::getStringFromMap(e.get_game_updates()) +
                        "team a updates: \n" + StompParser::getStringFromMap(e.get_team_a_updates()) +
                        "team b updates: \n" + StompParser::getStringFromMap(e.get_team_b_updates()) +
                        "description: " + e.get_discription() + '\0';
        user.send(output);
    }
    return "";
} 

string StompProtocol::handleSummary(std::vector<std::string> parsedCommand)
{
    // assuming the game stats updated proprly
    string gameName = parsedCommand.at(1);
    string userName = parsedCommand.at(2);
    string fileName = parsedCommand.at(3);
    
    // print? write into a file?
    user.summreizeGame(gameName,userName,fileName);

    return "";
}

//TODO when error
bool StompProtocol::getShouldTerminate()
{
    return false;
}

void StompProtocol::setShouldTerminate(bool value)
{
    // shouldTerminate = value;
}   

