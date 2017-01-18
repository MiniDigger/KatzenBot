package me.MiniDigger.KatzenBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.ListenerExceptionEvent;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Listener for IRC Handler
 * Parse all incomming Messages and try to execute them in Command Handler
 * Created by Martin on 01.10.2016.
 */
public class KatzenBotListener extends ListenerAdapter {

    private CommandHandler commandHandler = new CommandHandler();

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        event.getBot().send().message(Main.CHAN, "HeyGuys");
        commandHandler.initCommands();
    }


    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String[] args = event.getMessage().split(" ");
        String label = args[0];
        String channel = event.getChannel().getName();
        String sender = event.getUser().getNick();

        Moderator modObj = new Moderator();
        Moderator.MessageState state = modObj.isMessageValid(event);

        if (state == Moderator.MessageState.blacklist) {
            event.getBot().send().message(Main.CHAN, "/timeout " + event.getUser().getNick() + " 1");
            event.getBot().send().message(Main.CHAN, "@" + event.getUser().getNick() + " your message contained a blacklisted Word.");
        } else if (state == Moderator.MessageState.caps) {
            event.getBot().send().message(Main.CHAN, "/timeout " + event.getUser().getNick() + " 1");
            event.getBot().send().message(Main.CHAN, "@" + event.getUser().getNick() + " please do not use so many capital letters.");
        }

        commandHandler.executeCommand(label, args, sender, channel, event);
    }


    @Override
    public void onListenerException(ListenerExceptionEvent event) throws Exception {
        System.err.println(event.getException().getMessage());
        event.getException().printStackTrace();
    }

    // Setting instance to zero to prevent cloned messages
    public static KatzenBotListener instance = null;

    public KatzenBotListener() {
        if (instance != null) throw new KatzenBotException("Object already exists");
        instance = this;
    }

    class KatzenBotException extends RuntimeException {

        public KatzenBotException(String s) {
            super("Meow? " + s);
        }
    }
}
