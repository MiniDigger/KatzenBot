package me.MiniDigger.KatzenBot;

/**
 * Created by firetailor on 26.11.2016.
 */

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class IRCHandler {

    String _serverHostname;
    Integer _serverPort;

    String _user;
    String _pass;
    String _chan;

    List<Configuration.ServerEntry> _serverList;

    public IRCHandler(String serverHostname, Integer serverPort, String botUserName, String botAuthKey, String targetChannelName)
    {
        _serverHostname = serverHostname;
        _serverPort = serverPort;

        _user = botUserName;
        _pass = botAuthKey;
        _chan = targetChannelName;

        _serverList = new ArrayList<>();


    }

    public Configuration GetConfig(){

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

        return config;
    }

    public void Execute(Configuration configuration){
        PircBotX myBot = new PircBotX(configuration);

        try {
            myBot.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }
}
