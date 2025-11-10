package com.gameclub.team.main;

import com.gameclub.team.service.ValidationService;

import javax.sound.midi.InvalidMidiDataException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        //Survey interaction
        //1. Display the questions
        //create the current participant object
        //get the Validation service interface
        //ask the questions with try and catch
        Scanner scanner = new Scanner(System.in);
        ValidationService validator = new ValidationService();

        boolean validInput = false;
        while (!validInput) {
            System.out.println("Enter your choice from scale (1-5)");
            System.out.println("Q1. I enjoy taking the lead and guiding others during group activities.");
            String rawInput = scanner.nextLine();
            try{
                int score =validator.validateScore(rawInput,1,5);
                // set the answer to the current participant
                validInput = true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

        System.out.println("Q1. I enjoy taking the lead and guiding others during group activities.");
        System.out.println("Q2. I prefer analyzing situations and coming up with strategic solutions.");
        System.out.println("Q3. I work well with others and enjoy collaborative teamwork.");
        System.out.println("Q4. I am calm under pressure and can help maintain team morale.");
        System.out.println("Q5. I like making quick decisions and adapting in dynamic situations.");

















        //2. A new Participant object is created to temporarily hold the answers as they are provided
        //3. The answers are validated through ValidationService
    }
}
