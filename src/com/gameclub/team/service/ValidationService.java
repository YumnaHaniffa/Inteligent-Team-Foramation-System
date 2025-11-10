package com.gameclub.team.service;

import com.gameclub.team.controller.TeamFormationInt;

import java.util.InputMismatchException;

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
    //


}
