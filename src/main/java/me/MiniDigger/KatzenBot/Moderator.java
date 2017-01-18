package me.MiniDigger.KatzenBot;

import org.pircbotx.hooks.events.MessageEvent;

/**
 * Moderation functionality
 * Main Method: isMessageValid will return MessageState
 *
 * Implemented Test Methods:
 *  - Caps Lock Percentage
 *
 * Created by firetailor on 30.12.2016.
 */



public class Moderator {
    private float messageIsSpamPercentage = 0.05F;


    public enum MessageState{
        valid, caps, blacklist
    }

    public MessageState isMessageValid(MessageEvent Message) {
        MessageState messageStateObj = MessageState.valid;
        String message = Message.getMessage();

        //Check if Percentage of capital letters is in allowed range
        if (getNumberOfCapitalLetters(message) > (message.length() * messageIsSpamPercentage)) {
            messageStateObj = MessageState.caps;
        //Check if Message contains a blacklisted word
        } else if (checkForBlacklistedWords(message)) {
            messageStateObj = MessageState.blacklist;
        }

        return messageStateObj;
    }


    private int getNumberOfCapitalLetters(String message){
        int numberOfCapsLetters = 0;
        for (int k = 0; k < message.length(); k++) {
            if (Character.isUpperCase(message.charAt(k))) numberOfCapsLetters++;
        }
        return numberOfCapsLetters;
    }

    //to be continued ...
    private boolean checkForBlacklistedWords(String input){

        // todo
        return false;
    }

}
