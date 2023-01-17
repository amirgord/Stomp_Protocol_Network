#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/StompProtocol.h"


class KeyboardReader
{
public:
    KeyboardReader(StompProtocol &protocol, User &_user);
    void Run();

private:
    StompProtocol &protocol;
    User &user;

};