#pragma once

#include "../include/event.h"
#include "../include/StompParser.h"

#include <iostream>
#include <string>
#include <vector>
#include <unordered_map>
#include <map>



using std::string;
using std::vector;
using std::unordered_map;


class Game
{
private:

    string teamA;
    string teamAGoals;
    string teamAPosession;

    string teamB;
    string teamBGoals;
    string teamBPosession;

    vector<vector<string>> events; // [[time, eventName, description, user], [time, eventName, description, user], ...]
    
public:
    Game();
    ~Game();
    Game(string teamA, string teamB);
    void updateGame(string updaes);
    void summerizeGame(string userName,string fileName);

};


/*
germany vs japan
Game stats:
General stats:
active: false
before halftime: false
germany stats:
goals: 1
possession: 51%
japan stats:
goals: 2
possession: 49%
Game event reports:
0 - kickoff:

The game has started! What an exciting evening!


1980 - goal!!!!:

GOOOAAALLL!!! Germany lead!!! Gundogan finally has success in the box as he steps up to take the penalty, sends Gonda the wrong way, and slots the ball into the left-hand corner to put Germany 1-0 up! A needless penalty to concede from Japan's point of view, and after a bright start, the Samurai Blues trail!


2940 - Another goal!!!!:

BALL IN THE NET!!! Germany think they've doubled their lead following a brilliant passage of play, but there looks to an issue of offside as Havertz wheels away! Muller's cross from the right is headed away to Kimmich, who hits a low shot towards the bottom corner Gonda sees it late and parries it away, but Gnabry drills the ball back across the face and Havertz taps it in! The goal is given initially, but no doubt we'll be going upstairs for a second look---


3000 - No goal:

No goal! After a VAR review, a goal for Germany is ruled out.


3060 - halftime:

The first half ends, and what a half it's been! Germany almost suffered an early scare as Maeda tapped in after just eight minutes, but his offside position gave the 2014 champions a huge let-off This spooked them into gear, and the remainder of the half was dominated by the Germans, who peppered the Japan goal winning a 33rd-minute Gundogan slotted it home to put Die Nationalelf into a deserved lead. They almost doubled it half-time, as Havertz finished off a great attacking move, but he was also caught in an offside position, meaning the lead is still just one goal at the break.


4500 - goalgoalgoalgoalgoal!!!:

GOOOOAAAALLLL!!!!! Japan have parity and boy do they deserve it!!! Mitoma drives fowrard before feeding Minamino, whose ball across the face is parried away by Neuer. Doan reacts quickest to smash the ball into the back of the net, and the three Japan substitutes combine to put them back on level terms at 1-1!


4980 - goalgoalgoalgoalgoal!!!:

GOOOOOOAAAAALLLL!!!! Can you believe Itakura's long ball from a free-kick in his own half is met by Asano, who beats Schlotterbeck with a beauty of a first touch. He darts into the box and smashes a shot from the tightest of angles, which beats Neuer and flies into the roof of the net!!! What a turnaround, what a story, and what a finish, to put Japan 2-1 up!!!


5400 - final whistle:

Well, what a way to kick off Group E! Germany sit at the bottom of the group following that defeat, while Japan are at the top with one win from one! Spain and Costa Rica are yet to play their corresponding fixture, but if the rest of the games are anything like this one, we're in for one hell of a treat, and a very competitive set of fixtures.


*/