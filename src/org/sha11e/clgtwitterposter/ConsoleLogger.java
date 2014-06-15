package org.sha11e.clgtwitterposter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLogger {

    public final static Logger LOG = Logger.getLogger("org.sha11e.clgtwitterposter");
    public final static String NL = System.getProperty("line.separator");
    private static FileHandler fHandler;

    static {
	LOG.setLevel(Level.ALL);
	try {
	    fHandler = new FileHandler("ClgTwitterPoster.log");
	} catch(IOException e) {
	    ConsoleLogger.LOG.warning("IO exception opening ClgTwitterPoster.log: " +
			    	      e.getMessage() + ConsoleLogger.NL);
	}
	fHandler.setLevel(Level.WARNING);
	LOG.addHandler(fHandler);
    }
    
    public static void closeHandler() {
	fHandler.close();
    }

}
