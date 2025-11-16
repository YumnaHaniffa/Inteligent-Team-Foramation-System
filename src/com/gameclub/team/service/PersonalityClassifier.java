package com.gameclub.team.service;

import com.gameclub.team.model.Participant;

public class PersonalityClassifier{

    //Check for Leader -> 90-100
            // Balanced -> 70-89
            // Thinker -> 50-69
    // else -> ????????
    public String classify(int normalizedScore) {
        if (normalizedScore >= 90) {
            return "Leader";
        }
        else if (normalizedScore >= 70) {
            return "Balanced";
        }
        else if (normalizedScore >= 50) {
            return "Thinker";
        }
        else{
            throw new IllegalArgumentException("Invalid  personality score: " + normalizedScore);

        }

    }

//    //get the personality count for team
//    public int getPersonalityCount(String personality){
//        int personalityCount = 0;
//
//        if(personality == null){
//            return 0;
//        }
//        for(Participant p : members){
//            if(p.getPersonalityType()!= null && p.getPersonalityType().equalsIgnoreCase(personality)){
//                personalityCount++;
//
//            }
//
//        }
//        return personalityCount;
//    }

}
