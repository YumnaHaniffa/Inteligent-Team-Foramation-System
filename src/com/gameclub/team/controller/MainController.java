package com.gameclub.team.controller;

import java.util.Scanner;

public class MainController {

    private static final Scanner scanner = new Scanner(System.in);

    public static int promptPersonalityRating(String question){
       System.out.print("\nPersonality Traits : Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)");
       while(true){
           System.out.println(question + " -> Rating (1-5): ");

            try{
                String input = scanner.nextLine().trim();
                int rate = Integer.parseInt(input);
                if(rate >= 1 && rate <= 5){
                    return rate;
                }
                else{
                    System.out.println(" Error: Please enter a value between 1 and 5");
                }

            }catch(NumberFormatException e){
                System.out.println("Error: Invalid input ! Please enter a value between 1 and 5");
            }
        }

    }

    public static int promptForSelection(String message, int min, int max) {
        while(true){
            System.out.println(message);
            System.out.print("Enter selection ("+ min +" - "+ max +"):" );
            try{
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if(value >= min && value <= max){
                    return value;
                }
                System.out.println("Error : Enter a number between "+ min +" and "+ max);
            }
            catch (NumberFormatException e){
                System.out.println("Error: Invalid input. Please enter a numeric value");

            }

        }
    }









}
