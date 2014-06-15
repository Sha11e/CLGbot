package org.sha11e.clgtwitterposter;

import java.io.IOException;
import java.util.Iterator;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.sha11e.clgtwitterposter.ConsoleLogger;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class IrcPoster extends PircBot {
    
    public IrcPoster() {
	ConsoleLogger.LOG.info("Setting up the IRC bot" + ConsoleLogger.NL);
	props = new BotProperties();
	channel = props.getProperty("irc.channel");
	
	setVersion("CLG Twitter Poster 1.0");
	setFinger("You shouldn't go around fingering bots!");
	setName(props.getProperty("irc.nickname"));
	setAutoNickChange(true);
	connectToIrc();
	ts = new TwitterStreamer(props, this);
	ConsoleLogger.LOG.info("The IRC bot has been set up successfully."
			     + ConsoleLogger.NL);
    }
    
    private void connectToIrc() {
	ConsoleLogger.LOG.info("Connecting to IRC server" + ConsoleLogger.NL);
	String server = props.getProperty("irc.server");
	int port = Integer.parseInt(props.getProperty("irc.port"));

	try {
	    connect(server, port);
	} catch (NickAlreadyInUseException e) {
	    ConsoleLogger.LOG.warning("Nickname is already in use. "
	        + "Adding numbers for a unique nickname" + ConsoleLogger.NL);
	} catch (IOException e) {
	    ConsoleLogger.LOG.warning("Could not connect to the irc server: "
			            + e.getMessage() + ConsoleLogger.NL);
	    terminateProgram();
	} catch (IrcException e) {
	    ConsoleLogger.LOG.severe("Server did not let the bot join: "
			           + e.getMessage() + ConsoleLogger.NL);
	    terminateProgram();
	}
    }
    
    @Override
    protected void onConnect() {
        String authBot = props.getProperty("irc.authbot");
        String username = props.getProperty("irc.username");
        String password = props.getProperty("irc.password");
        String hostmask = props.getProperty("irc.hostmask");
        String channel = props.getProperty("irc.channel").toLowerCase();
        String channelPassword = props.getProperty("irc.channel.password");
        
	ConsoleLogger.LOG.info("Authenticating the bot" + ConsoleLogger.NL);
	sendRawLine("PRIVMSG " + authBot + " :AUTH "
	          + username + " " + password);
	ConsoleLogger.LOG.info("Hiding the bot's hostname" + ConsoleLogger.NL);
	sendRawLine(hostmask.replace("botname", getNick()));
	ConsoleLogger.LOG.info("Joining " + channel + ConsoleLogger.NL);
	if (channelPassword.isEmpty()) {
	    joinChannel(channel);
	} else {
	    joinChannel(channel, channelPassword);
	}
    }
    
    @Override
    protected void onMessage(String channel, String sender, String login,
		    String hostname, String message) {
	if (message.equalsIgnoreCase("!following")) {
	    sendMessage("You can see who has been followed by visiting http://twitter.com/ClgTweetBot/following");
	} else if (message.equalsIgnoreCase("!help")) {
	    sendMessage("Command(s) availible: !following");
	}
    }
    
    @Override
    protected void onPrivateMessage(String sender, String login,
		                    String hostname, String message) {
	if (!message.startsWith("rawline:") || !userIsAuthed(hostname)) {
	    return;
	}
	    String[] admins = props.getProperty("irc.admins").split(",");
	    for (String admin : admins) {
	        String authName = hostname.substring(0, hostname.indexOf('.'));
	        if (authName.equalsIgnoreCase(admin)) {
	            String command = message.replace("rawline:", "");
	            if (command.toUpperCase().startsWith("QUIT")) {
	        	terminateProgram();
	            } else if (command.startsWith("JOIN")) {
	        	ConsoleLogger.LOG.info("Joining " + (command.split(" "))[1]);
	            }
		    sendRawLine(command);
	        }
	    }
    }
    
    private boolean userIsAuthed(String hostname) {
  	String server = props.getProperty("irc.server").replace("irc.", "");
  	
  	return hostname.endsWith(server);
      }
    
    public void sendMessage(String message) {
	sendMessage(channel, message);
    }
    
    @Override
    protected void onKick(String channel, String kickerNick,
		          String kickerLogin, String kickerHostname,
		          String recipientNick, String reason) {
	if (recipientNick.equalsIgnoreCase(getNick())) {
	    ConsoleLogger.LOG.warning("The bot was kicked from " + channel
                + " by " + kickerNick + " (" + reason + ")" + ConsoleLogger.NL);
	}
    }

    @Override
     protected void onDisconnect() {
	ConsoleLogger.LOG.warning("The bot was disconnected from the server"
			     + ConsoleLogger.NL);
	ConsoleLogger.LOG.info("Attempting to reconnect to the server" + ConsoleLogger.NL);
	try {
	    reconnect();
	} catch (NickAlreadyInUseException e) {
	    ConsoleLogger.LOG.warning("Nick already in use, but autoNickChange is on" + ConsoleLogger.NL);
	} catch (IOException e) {
	    ConsoleLogger.LOG.severe("Could not reconnect to server" + ConsoleLogger.NL);
	} catch (IrcException e) {
	    ConsoleLogger.LOG.severe("The server would not let the bot join" + ConsoleLogger.NL);
	}
	if (!isConnected()) {
	    terminateProgram();
	}
    }
    
    private void terminateProgram() {
	ConsoleLogger.LOG.info("Quitting from the server(if the bot is still connected" + ConsoleLogger.NL);
	quitServer();
	ConsoleLogger.LOG.info("Closing the file handler to the log file" + ConsoleLogger.NL);
	ConsoleLogger.closeHandler();
	ConsoleLogger.LOG.info("Closing the program" + ConsoleLogger.NL);
	System.exit(0);
    }
    
    private BotProperties props;
    private TwitterStreamer ts;
    private String channel;
}
