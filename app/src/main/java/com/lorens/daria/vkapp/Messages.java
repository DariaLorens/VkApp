package com.lorens.daria.vkapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ilyas on 07-Oct-18.
 */

public class Messages implements Serializable {

    ArrayList<Message> messages;
    public Messages() {
        messages = new ArrayList<>();
    }
    public void clear(){
        messages.clear();
    }
    public int size(){
        return messages.size();
    }

    public void addMessage(Message message){
        messages.add(message);
    }


    public void setup(){
        Collections.reverse(messages);
    }

    public Message getMessage(int position){
        return messages.get(position);
    }
}
