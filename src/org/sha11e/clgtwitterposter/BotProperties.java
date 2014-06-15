package org.sha11e.clgtwitterposter;

import org.sha11e.clgtwitterposter.ConsoleLogger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class BotProperties {
    
    public BotProperties() {
	loadProperties();
    }
    
    private final String PROPERTIES_PATH = "ClgTwitterPoster.properties";
    
    private void loadProperties() {
	ConsoleLogger.LOG.info("Loading properties" + ConsoleLogger.NL);
	InputStream input = null;
	try {
	    input = new FileInputStream(PROPERTIES_PATH);
	    props.load(input);
	} catch (FileNotFoundException ex) {
	    ConsoleLogger.LOG.warning("Unable to find the properties file("
		                   + PROPERTIES_PATH + ")" + ConsoleLogger.NL);
	    createDefaultPropertiesFile();
	    ConsoleLogger.LOG.info("Closing the program" + ConsoleLogger.NL);
	    System.exit(0);
	} catch (IOException ex) {
	    ConsoleLogger.LOG.severe("An error occured while reading from " +
			            "properties file: " + ex.getMessage() +
			            ConsoleLogger.NL);
	    ConsoleLogger.LOG.info("Closing the program");
	    System.exit(1);
	} finally {
	    try {
		input.close();
	    } catch (IOException e) {
		ConsoleLogger.LOG.warning("Failed to close the input stream for "
				        + PROPERTIES_PATH  + ConsoleLogger.NL);
	    }
	}
    }
    
    private void createDefaultPropertiesFile() {
        ConsoleLogger.LOG.info("Creating a default properties file"
                             + ConsoleLogger.NL);
        OutputStream output = null;

        try {
            output = new FileOutputStream(PROPERTIES_PATH);
            String fileTxt = String.format("### IRC SETTINGS ###%n"
                             + "# The IRC server and port(usually 6667) you want to connect to(e.g. irc.quakenet.org)%n"
                             + "irc.server=irc.quakenet.org%n" 
                             + "irc.port=6667%n%n"
                             + "# The bot you authenticate with(e.g. \"Q@CServe.quakenet.org\" for QuakeNet%n"
                             + "# or \"authserv\" for GameSurge)%n"
                             + "irc.authbot=Q@CServe.quakenet.org%n%n"
        		     + "# The username and password for your IRC bot%n"
        		     + "irc.username=BotUsername%n"
        		     + "irc.password=BotPassword%n%n"
        	             + "# The nickname you want your bot to use%n" 
        	             + "irc.nickname=BotNick%n%n"
        	             + "# The command to mask your host(e.g. \"MODE botname +x\" for QuakeNet and GameSurge)%n"
        	             + "# Please use 'botname' as a placeholder for your bot's nick. The program will replace%n"
        	             + "# the string with the nick your bot ends up using%n"
        	             + "irc.hostmask=MODE botname +x%n%n"
        		     + "# The channel you want to join, e.g. \"#ChannelName\" and the password to the channel%n"
        		     + "# Leave irc.channel.password empty if the channel has no password%n"
        		     + "irc.channel=#Channel%n"
        		     + "irc.channel.password=%n%n"
        		     + "# The bot's administrators. These people will be able to%n"
        		     + "# send irc commands to the bot(e.g. join or part channels)%n"
        		     + "# Type their authnames - currentNick!user@AUTHNAME.users.quakenet.org%n"
        		     + "# Seperate admins with a comma(,) - e.g. irc.admins=Person1,Person2%n"
        		     + "# Changing this property will affect the bot without having to restart it%n"
        		     + "irc.admins=Person1%n%n%n"
        		     + "### TWITTER SETTINGS ###%n"
        		     + "# Set value to true or false%n"
        		     + "twitter.debug=false%n%n"
        		     + "twitter.consumerKey=%n"
        		     + "twitter.consumerSecret=%n"
        		     + "twitter.accessToken=%n"
        		     + "twitter.accessTokenSecret="
        		    );
            
            output.write(fileTxt.getBytes());
            output.flush();
            ConsoleLogger.LOG.info("Successfully created the default properties file");
        } catch(FileNotFoundException e) {
              ConsoleLogger.LOG.severe("Unble to create the properties file. "
                + PROPERTIES_PATH + ": " + e.getMessage() + ConsoleLogger.NL);
        } catch (IOException e) {
	      ConsoleLogger.LOG.warning("An error occured while trying to " +
			             "write a log to " + PROPERTIES_PATH + ": " +
			             e.getMessage() + ConsoleLogger.NL);
	}
    }
    
    public String getProperty(String key) {
	return props.getProperty(key);
    }
    
    private Properties props = new Properties();;
    
}
