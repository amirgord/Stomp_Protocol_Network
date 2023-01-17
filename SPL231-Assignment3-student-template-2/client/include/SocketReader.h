#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/StompProtocol.h"

class SocketReader
{
public:
    SocketReader(StompProtocol &protocol, User& user);
    void Run();

private:
    StompProtocol &protocol;
    User &user;

};
