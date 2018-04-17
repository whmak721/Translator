package com.example.hang.googletranslate;

import java.util.ArrayList;

/**
 * Created by HanG on 22/3/2018.
 */

public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String translate;
    private ArrayList<String> matches;
    private String targetLang;
    private int choice;

    public void setMatches(ArrayList<String> matches){
        this.matches = matches;
    }
    public ArrayList<String> getMatches(){
        return matches;
    }

    public void setChoice(int choice){
        this.choice = choice;
    }
    public int getChoice(){
        return choice;
    }

    public void setTargetLang(String targetLang){
        this.targetLang = targetLang;
    }
    public String getTargetLang(){
        return targetLang;
    }

    public void setTranslate(String translate){
        this.translate = translate;
    }
    public String getTranslate(){
        return translate;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}