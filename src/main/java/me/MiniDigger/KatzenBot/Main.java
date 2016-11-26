package me.MiniDigger.KatzenBot;



import java.util.Arrays;

/**
 * Created by Martin on 01.10.2016.
 */
public class Main {

    public static String USER = "";
    public static String PASS = "";
    public static String CHAN = "";

    public static void main(String[] args) {

        //Check for Argument existance. Exit if unexpected number of Arguments.
        if (args.length == 3) {
            USER = args[0];
            PASS = args[1];
            CHAN = args[2];

            //Add # on the beginning of the Channel name if not existant.
            if (!CHAN.startsWith("#")) {
                CHAN = "#" + CHAN;
                System.out.println("fixed channel name for you ;) (" + CHAN + ")");
            }
        } else {
            System.err.println("Got unexpected number of Arguments");
            System.err.println("usage: java -jar KatzenBot-jar-with-dependencies.jar <username> <oauth token> <channel>");
            System.out.println(Arrays.toString(args));
            return;
        }

        //Check Argument length. Exit on length == 0
        if (USER.length() == 0 || PASS.length() == 0 || CHAN.length() == 0) {
            System.err.println("usage: java -jar KatzenBot-jar-with-dependencies.jar <username> <oauth token> <channel>");
            System.out.println("args can't be zero-length " + Arrays.toString(args));
            return;
        }

        System.out.println("Starting IRC Client for user " + USER + " in channel " + CHAN);

        IRCHandler ircHandlerObj = new IRCHandler("irc.chat.twitch.tv", 6667 ,USER, PASS, CHAN);
        ircHandlerObj.Execute(ircHandlerObj.GetConfig());
    }
}
