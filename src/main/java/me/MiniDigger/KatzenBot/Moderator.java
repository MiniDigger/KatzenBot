package me.MiniDigger.KatzenBot;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Moderation functionality
 * Main Method: isMessageValid will return MessageState
 * Implemented Test Methods:
 * - Caps Lock Percentage
 * Created by firetailor on 30.12.2016.
 */


public class Moderator {


    public enum MessageState {
        valid, caps, blacklist
    }

    public enum PunishmentMethod{
        call, clear, timeout, ban
    }

    private Map<MessageState, String> messages = new HashMap<MessageState, String>();


    public static void moderateMessage (MessageEvent event)
    {
        //Check message and punish if necessary.
        Moderator modObj = new Moderator();
        Moderator.MessageState state = modObj.isMessageValid(event);

        if (state == Moderator.MessageState.blacklist) {
            //Delete Messsage (to be moved to Moderator class)
            event.getBot().send().message(Main.CHAN, Moderator.generateOutput(event.getUser().getNick(), state, Moderator.PunishmentMethod.clear));

            //send information to user
            event.getBot().send().message(Main.CHAN, "@" + event.getUser().getNick() + " your message contained a blacklisted Word.");


        } else if (state == Moderator.MessageState.caps) {
            //Delete Messsage (to be moved to Moderator class)
            event.getBot().send().message(Main.CHAN, Moderator.generateOutput(event.getUser().getNick(), state, Moderator.PunishmentMethod.clear));

            //pass information to user
            event.getBot().send().message(Main.CHAN, "@" + event.getUser().getNick() + " please do not use so many capital letters.");
        }
    }

    public MessageState isMessageValid(MessageEvent Message) {
        MessageState messageStateObj = MessageState.valid;
        String message = Message.getMessage();

        //Check if Percentage of capital letters is in allowed range
        //Check if Message contains a blacklisted word
        if (checkForCapsSpam(message, 0.05F)) {
            messageStateObj = MessageState.caps;
        } else if (checkForBlacklistedWords(message)) {
            messageStateObj = MessageState.blacklist;
        }



        return messageStateObj;
    }

    public static String generateOutput(String username, MessageState msgs, PunishmentMethod pmet) {

        String out = "";

        switch (pmet) {
            case ban:
                    out += "/ban " + username;
                break;
            case timeout:
                    out += "/timeout " + username + " 400";
                break;
            case clear:
                out += "/timeout " + username + " 1";
                break;
            case call:
                    out += "VoteNay @" + username + " lass das bitte!";
                break;
        }



        return out;
    }


    private boolean checkForCapsSpam(String input, float messageIsSpamPercentage){

        if(getNumberOfCapitalLetters(input) > (input.length() * messageIsSpamPercentage))
            return true;

        return false;
    }


    //to be continued ...
    private boolean checkForBlacklistedWords(String input) {

        // todo
        return false;
    }



    private int getNumberOfCapitalLetters(String message) {
        int numberOfCapsLetters = 0;
        for (int k = 0; k < message.length(); k++) {
            if (Character.isUpperCase(message.charAt(k))) numberOfCapsLetters++;
        }
        return numberOfCapsLetters;
    }

}

