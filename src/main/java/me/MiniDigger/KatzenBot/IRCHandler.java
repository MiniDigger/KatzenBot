package me.MiniDigger.KatzenBot;


import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Creates a connection to an IRC Server and set up a listener
 * Created by firetailor on 26.11.2016.
 */
public class IRCHandler {

    String _serverHostname;
    Integer _serverPort;

    String _user;
    String _pass;
    String _chan;
    Configuration _configuration;

    List<Configuration.ServerEntry> _serverList;

    public IRCHandler(String serverHostname, Integer serverPort, String botUserName, String botAuthKey, String targetChannelName) {
        _serverHostname = serverHostname;
        _serverPort = serverPort;

        _user = botUserName;
        _pass = botAuthKey;
        _chan = targetChannelName;

        _serverList = new ArrayList<>();


    }

    /**
     * Generate Configuration
     * To be called before Execute()
     */
    public void Configure() {

        _serverList.add(new Configuration.ServerEntry(_serverHostname, _serverPort));

        Configuration config = new Configuration.Builder()
                .setName(_user) //Nick of the bot. CHANGE IN YOUR CODE
                .setLogin(_user) //Login part of hostmask, eg name:login@host
                .setRealName("KatzenBot made by MiniDigger")
                .setAutoNickChange(true) //Automatically change nick when the current one is in use
                .addAutoJoinChannel(_chan) //Join #pircbotx channel on connect
                .setAutoReconnect(false)
                .setServerPassword(_pass)
                .setServers(_serverList)
                .addListener(new KatzenBotListener())
                .buildConfiguration(); //Create an immutable configuration from this builder

        _configuration = config;
    }

    /**
     * Execute IRC Client.
     * If not yet done will call Configure to get connection information
     */
    public void Execute() {

        if (null != _configuration) {
            PircBotX myBot = new PircBotX(_configuration);

            try {
                myBot.startBot();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
            }
        } else {
            Configure();
            Execute();
        }

    }
}
