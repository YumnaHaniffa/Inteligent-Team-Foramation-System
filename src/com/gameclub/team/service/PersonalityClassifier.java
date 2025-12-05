package com.gameclub.team.service;
public class PersonalityClassifier{

    public String classify(double normalizedScore) {
        try {
            if (normalizedScore >= 90) {
                return "Leader";
            } else if (normalizedScore >= 70) {
                return "Balanced";
            } else if (normalizedScore >= 50) {
                return "Thinker";
            } else {
                throw new IllegalArgumentException("Invalid  personality score: " + normalizedScore);

            }
        }catch (Exception e){
            return "Invalid";

        }

    }


}
