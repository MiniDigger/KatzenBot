package me.MiniDigger.KatzenBot;

import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by firetailor on 30.12.2016.
 */



public class Moderator {
    public enum MessageState{
        valid, caps, blacklist

    }

    public MessageState isMessageValid(MessageEvent Message) {
        MessageState messageStateObj = MessageState.valid;
        String message = Message.getMessage();

        if (getNumberOfCapitalLetters(message) > (message.length() * 0.05)) {
            messageStateObj = MessageState.caps;
        } else if (checkForBlacklistedWords(message)) {
            messageStateObj = MessageState.blacklist;
        }

        return messageStateObj;
    }


    private int getNumberOfCapitalLetters(String input){
        Integer numberOfCapsLetters = 0;
        for (int k = 0; k < message.length(); k++) {
            if (Character.isUpperCase(message.charAt(k))) numberOfCapsLetters++;
        }
        return numberOfCapsLetters;
    }

    private boolean checkForBlacklistedWords(String input){

        // todo
        return false;
    }

}
