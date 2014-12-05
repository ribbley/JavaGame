Standard port : 2343

Servercommands:
/list - lists all players connected (name + ip)
/kick [PLAYERNAME] - kicks a specific player (by name)
/stop - stops the server
Keybindings:
WASD - move your character
Mouse - aim (use abilities (Leftclick/Rightclick))
F9 	- Toggle EntityInfo display on/off
F10 - Toggle FPS display on/off
F11 - Toggle Tick display on/off
T - opens textfield for chat
Enter - starts the game @ the beginning

Veri(fication)-Co(de)s [VERICOS]
/*** 000 - 009 RESERVED FOR MANAGEMENT ***\
000 - New client 
001 - Client disconnecting
002 - Client asking for playerlist
003 - Message
004 - Asking for ID-Info ("I don't know this id!")
005
006
007
008
009 - Invalid syntax/request
/*****************************************\
010 - automatic movementupdate (x,y,dx,dy,theta)
011 - HP Status (hp,maxhp)
012 - enable Status (true/false)