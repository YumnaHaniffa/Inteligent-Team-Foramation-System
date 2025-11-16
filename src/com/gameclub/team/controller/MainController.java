package com.gameclub.team.controller;

import java.util.Scanner;

public class MainController {

    private static Scanner scanner = new Scanner(System.in);

    public static int promptPersonalityRating(String question){
        while(true){
            System.out.print("Personality Traits");
            System.out.print("Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)");

            try{
                int rate = Integer.parseInt(scanner.nextLine());
                if(rate >= 1 && rate <= 5){
                    return rate;
                }
                else{
                    System.out.println("Please enter a value between 1 and 5");
                }

            }catch(NumberFormatException e){
                System.out.println("Invalid input ! Please enter a value between 1 and 5");
            }
        }

    }

    public static int promptForSelection(String message, int min, int max) {
        while(true){
            System.out.println(message);
            try{
                int value = Integer.parseInt(scanner.nextLine());
                if(value >= min && value <= max){
                    return value;
                }
                System.out.println("Enter a number between"+min+" and "+max);
            }
            catch (NumberFormatException e){
                System.out.println("Invalid input");

            }

        }
    }









}
