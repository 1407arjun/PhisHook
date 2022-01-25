package com.arjun.phishook;

import java.util.ArrayList;

public class ConstantsActivity {
    private static ArrayList<Message> messages;

    public static ArrayList<Message> getMessages() {
        if (messages == null)
            return new ArrayList<>();
        return messages;
    }

    public static void setMessages(ArrayList<Message> newMessages) {
        messages = newMessages;
    }
}
