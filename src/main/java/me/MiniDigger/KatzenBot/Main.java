package me.MiniDigger.KatzenBot;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 01.10.2016.
 */
public class Main {

    public static String USER = "";
    public static String PASS = "";
    public static String CHAN = "";

    public static void main(String[] args) {
        if (args.length == 3) {
            USER = args[0];
            PASS = args[1];
            CHAN = args[2];
        }

        // irc client
        List<Configuration.ServerEntry> servers = new ArrayList<>();
        servers.add(new Configuration.ServerEntry("irc.chat.twitch.tv", 6667));

        Configuration config = new Configuration.Builder()
                .setName(USER) //Nick of the bot. CHANGE IN YOUR CODE
                .setLogin(USER) //Login part of hostmask, eg name:login@host
                .setRealName("KatzenBot made by MiniDigger")
                .setAutoNickChange(true) //Automatically change nick when the current one is in use
                .addAutoJoinChannel(CHAN) //Join #pircbotx channel on connect
                .setAutoReconnect(true)
                .setServerPassword(PASS)
                .setServers(servers)
                .addListener(new KatzenBotListener())
                .buildConfiguration(); //Create an immutable configuration from this builder

        PircBotX myBot = new PircBotX(config);
        try {
            myBot.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }
}
