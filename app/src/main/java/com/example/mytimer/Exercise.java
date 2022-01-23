package com.example.mytimer;

import android.graphics.Bitmap;

public class Exercise {
    private String name;

    private String description;
    private Bitmap image;

    public Exercise(String name, String length, String desc, Bitmap image) {
        this.name = name;
        this.description = desc;
        this.image = image;
    }

    public Exercise(){

    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setImage(Bitmap img){
        this.image = img;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Bitmap getImage(){
        return image;
    }
}
