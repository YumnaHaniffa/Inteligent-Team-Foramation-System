package com.gameclub.team.service;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

import java.util.*;

//Exception Handling and Validating team formation
public class ValidationService implements ValidationServiceInt {

    //Validate participant details -> Id, Name and email
    public String validate_id(String inputId) {
        String id_pattern = "^[A-Z]\\d{3}$";
        if (inputId.length() < 4) {
            throw new IllegalArgumentException("The participant id must be 4 characters "); //as the input length is not 4
        }
        //check if id follows the "P001" pattern
        if (!id_pattern.matches(inputId)) {
            System.out.println("The participant id is invalid");
        }
        //check if the id already exists

        return inputId;

    }
    //Validate Name
//    public String validate_name(String inputName) {
//
//    }

    //Validate email
    public String validate_email(String inputEmail) {
        String email_pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$";
        if (!email_pattern.matches(inputEmail)) {
            System.out.println("The participant email is invalid");
        }
        return inputEmail;
    }



    // The validations for Survey data-> personality queries , interest and role selection
    // rate for each question be in (1-5) scale
    // the selecting of role should be in scale(1-5)
    //for the interest scale(1-5)
    public int validateScore(String rawInput,int min, int max){
        int score;
        //Check if the input is an integer
        try{
            score = Integer.parseInt(rawInput.trim());
        }
        catch(NumberFormatException e){
            throw new InputMismatchException("Input must be a whole number");

        }
        //Check if the input is within the scale of (1-5)
        if(score < min || score > max){
            throw new InputMismatchException("Input must be between"+min+" and"+ max);
        }
        return score;
    }
    //Team formation constraint handling

    //4. Check for the constraints
    // The algorithm checks a constraint, and if it fails, it fixes the failure, and then checks all constraints again until every rule is met.
    //-> IMPORTANT -> games that exceed the count by more than one
    //-> more than one game group exceed th cap


    public List<Map<String, Object>> checkGameCap(List<Team> teams, int gameMax) {

        //create list to store teams that failed the validation with the information
        List<Map<String,Object>> failedTeams = new ArrayList<>();


        //Create a map to store count of players for each game
        for (Team team : teams) {
            HashMap<String, Integer> gameCountMap = new HashMap<>(); // how many players for each game in each team

            //count the players to each game of the current team
            for(Participant player : team.getMembers()){
                String gameName = player.getPreferredGame();
                gameCountMap.put(gameName, gameCountMap.getOrDefault(gameName, 0) + 1);
            }

            //Compare the count for the most common game against the defined cap
            // consider other limitations
            for(Map.Entry<String, Integer> entry : gameCountMap.entrySet()){
                String gameName = entry.getKey();
                int count = entry.getValue();

                if(count > gameMax){
                    Map<String,Object> failure = new HashMap<>();
                    failure.put("Team",team);
                    failure.put("gameName",gameName);
                    failedTeams.add(failure); // can add information on how each team violates the rule

                    break; //move to the next team

                }

            }
        }
        return failedTeams;
    }
    //IF FAILS -> what happens to the stored teams
    //1. Identify the player to be removed -> this is the player with the lowest rank(to reduce the impact of avg skill) in the falling team

    public  void fixGameCapFailure(List<Map<String,Object>> failedTeams,List<Team> teams){
        for(Map<String,Object> failedT : failedTeams){
            Team failedTeam  = (Team)failedT.get("team"); // get the current team from the filed teams
            String violatingGame = (String) failedT.get("violatingGame");


        }
    }





}
