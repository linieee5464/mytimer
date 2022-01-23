package com.example.mytimer;
/*Class to update the user note to database*/
/*Design following JSON structure*/
/*Encapsulation*/
public class UserNote {
    private String title;
    private String note;

    public UserNote(String title, String note){
        this.title = title;
        this.note = note;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getTitle(){
        return title;
    }

    public String getNote(){
        return note;
    }
}
