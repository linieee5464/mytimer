package com.example.mytimer;
/* Class to handle calo calculation by predefined formulas*/
/* Ensure encapsulation*/
public class CaloCalculator {
    private final double cardioCalopm = 10;
    private final double hiitCalopm = 10;
    private final double strengthCalopm = 10;
    private double caloBurnt = 0;
    /*Calculate the calories by the time user take exercise*/
    public void calculateCalo(String exercise, double length){
        switch(exercise){
            case "Cardio":
                caloBurnt += cardioCalopm*length;
                break;
            case "HIIT":
                caloBurnt += hiitCalopm*length;
                break;
            case "Strengthen":
                caloBurnt += strengthCalopm*length;
                break;
            default:
                break;
        }
    }

    public double getCalo(){
        return caloBurnt;
    }
}
