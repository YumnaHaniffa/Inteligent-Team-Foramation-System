package com.gameclub.team.model;

import com.gameclub.team.service.PersonalityClassifier;

import java.util.List;
import java.util.Objects;

//Represents an individual member
public class Participant {

    private String playerId;
    private String name;
    private String email;

    private String preferredGame;
    private int skillLevel; //-> how does the survey data provides this
    private String preferredRole;

    //Foe each question the rating is taken
    private List<Integer> persona_rating;
    private int personalityScore;
    private String personalityType;      //PersonalityClassifier Datatype


    private int compositeScore;


    public Participant(String playerId, String name, String email, String preferredGame, int skillLevel, String preferredRole, List<Integer> persona_rating) {
        this.playerId = playerId;
        this.name = name;
        this.email = email;

        this.preferredGame = preferredGame;
        this.skillLevel = skillLevel;
        this.preferredRole = preferredRole;

        this.persona_rating = persona_rating;
        this.personalityScore = calculatePersonalityScore();


        //assign personality using personality Classifier class
        // COMPOSITION//
        PersonalityClassifier classifier = new PersonalityClassifier();
        this.personalityType = classifier.classify(personalityScore);

        this.compositeScore = calculateCompositeScore();


    }
    public Participant(String playerId, String name, String email, String preferredGame, int skillLevel,String role ,String preferredRole) {}



    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailMail() {
        return email;
    }

    public void setEmailMail(String emailMail) {
        this.email = emailMail;
    }


    public String getPlayerId() {
        return playerId;
    }

    public String getPreferredGame() {
        return preferredGame;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public int getPersonalityScore() {
        return personalityScore;
    }

    public String getPersonalityType() {
        return personalityType;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setPreferredGame(String preferredGame) {
        this.preferredGame = preferredGame;
    }

    public void setPreferredRole(String preferredRole) {
        this.preferredRole = preferredRole;
    }

    public void setPersonalityScore(int personalityScore) {
        this.personalityScore = personalityScore;
    }

    public void setPersonalityType(String personalityType) {
        this.personalityType = personalityType;
    }

    public void setSkillLevel(int SkillLevel) {
        skillLevel = SkillLevel;
    }


    public void setCompositeScore(int compositeScore) {
        this.compositeScore = compositeScore;
    }


    public int getCompositeScore() {
        return getSkillLevel() + getPersonalityScore();
    }

    public int calculatePersonalityScore() {
        int sum = 0;
        for (int rating : persona_rating) {
            sum += rating;

        }
        return sum;

    }

    public int calculateCompositeScore() {
        return this.skillLevel + this.personalityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Participant)) return false;
        Participant p = (Participant) o;
        return this.playerId.equals(p.playerId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }


    @Override
    public String toString() {
        return "--- PARTICIPANT ---\n" +
                "Name: " + name + "\n" +
                "Skill Level (1-10): " + skillLevel + "\n" +
                "Game Interest: " + preferredGame + "\n" +
                "Preferred Role: " + preferredRole + "\n" +
                "Personality Type: " + personalityType + " (Mix Constraint Input)\n" +
                "---------------------------------";
    }
    }

