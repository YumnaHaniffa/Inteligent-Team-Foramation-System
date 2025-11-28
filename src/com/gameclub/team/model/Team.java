package com.gameclub.team.model;
import java.util.*;
import java.util.stream.Collectors;

//Represents a collection of members
public class Team {


    private String teamName;
    //participant list
    //INDICATION OF AGGREGATION - the team has participants but the participants can exist without the team
    private final List<Participant> members;


    //Default initialization
    public Team() {
        this.members = new ArrayList<>(); // what creating a team object a list will be created to add the members
    }

    //with name
    public Team(String name) {
        this.teamName = name;
        this.members = new ArrayList<>();

    }

    public String getTeamName() {
        return teamName;
    }

    public List<Participant> getMembers() {
        return members;
    }


    //add participants
    public void addPlayers(Participant p) {
        if (p != null) {
            this.members.add(p);
        } else {
            System.err.println("ERROR: Attempted to add a null participant to " + teamName);
        }

    }


    // (Game Variety) Helper ---
    public int getGameCount(String game) {
        if (game == null) return 0;
        return (int) members.stream()
                .filter(p -> p != null && game.equals(p.getPreferredGame()))
                .count();
    }
    // Personality Mix
    public int getPersonalityCount(String personalityType) {
        if (personalityType == null) return 0;
        return (int) members.stream()
                .filter(p -> p != null && personalityType.equals(p.getPersonalityType()))
                .count();
    }


    public int getTotalSkill() {
        return members.stream().filter(Objects::nonNull).mapToInt(Participant::getSkillLevel).sum();
    }

    public double getAverageSkill() {
        if (members.isEmpty()) {
            return 0.0;
        }
        //Total Skill / Number of Members
        return (double) getTotalSkill() / members.size();
    }



    //===================threading ===========================//
    public Team deepCopy() {
        Team newTeam = new Team(this.teamName);
        for (Participant member : this.members) {
            newTeam.getMembers().add(member.deepCopy());

        }
        return newTeam;
    }

    //Finds a member in this team by name. Used by the SwapEvaluationTask
    //locate the player within the temporary copy of the team

    public Participant getMemberByName(String name) {
        return members.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    //Calculate the number of unique roles currently in the team
    public int getUniqueRoleCount() {
        return(int)members.stream().map(Participant::getPreferredRole).distinct().count();
    }
}