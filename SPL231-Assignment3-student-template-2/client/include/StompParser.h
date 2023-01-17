#pragma once
#include "../include/event.h"

#include <string>
#include <vector>
#include <unordered_map>
#include <map>

class StompParser
{
private:

public:
    StompParser();
    static std::unordered_map<std::string, std::string> parse_stomp_message(std::string& message);
    static std::vector<std::string> parseCommand(const std::string& command, char del);
    static std::string getStringFromMap(std::map<std::string, std::string> map);
    static Event parseEvent(std::string body);
    static std::unordered_map<std::string, std::string> parse_stomp_report(std::string& message); 
    


};
