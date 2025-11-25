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
    // Constructor used for temporary 'isSwapSafe' checks
    public Team(String Name, List<Participant> initialMembers) {
        this.teamName = Name;
        // Defensive copy to prevent external modification
        this.members = new ArrayList<>(initialMembers != null ? initialMembers : Collections.emptyList());
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
    //remove player
    public void removePlayer(Participant p) {
        if (p != null) {
            this.members.remove(p);
        } else {
            System.err.println("ERROR: Attempted to remove a null participant from " + teamName);
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
//
//    // --- P3 (Role Diversity) Helper ---
//    public Participant lowestRankedPlayerByRole(String role) {
//        if (role == null) return null;
//        return members.stream()
//                .filter(p -> p != null && role.equals(p.getPreferredRole()))
//                .min(Comparator.comparingInt(Participant::getSkillLevel))
//                .orElse(null);
//    }
//    public Participant lowestRankedPlayerWithRedundantRole() {
//        if (members.isEmpty()) return null;
//
//        Map<String, Long> roleCounts = members.stream()
//                .filter(Objects::nonNull)
//                .collect(Collectors.groupingBy(Participant::getPreferredRole, Collectors.counting()));
//
//        Set<String> redundantRoles = roleCounts.entrySet().stream()
//                .filter(entry -> entry.getValue() > 1)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//
//        if (redundantRoles.isEmpty()) return null;
//
//        return members.stream()
//                .filter(p -> p != null && redundantRoles.contains(p.getPreferredRole()))
//                .min(Comparator.comparingInt(Participant::getSkillLevel))
//                .orElse(null);
//    }

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

    // Helper to identify player to swap out in skill optimization
    public Participant getHighestSkilledPlayer() {
        return members.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(Participant::getSkillLevel))
                .orElse(null);
    }

    // Helper to identify player to swap in in skill optimization
    public Participant getLowestSkilledPlayer() {
        return members.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(Participant::getSkillLevel))
                .orElse(null);
    }


    //Display the formed
    public void displayTeamDetails(){
        System.out.println("\nTeam Name: " + this.getTeamName() + " (Members: " + this.getMembers().size() + ")");
        System.out.println("-----------------------------------------------------------");

        //header per team
        System.out.printf("%-15s | %-5s | %-10s | %-10s | %-5s | %s\n ",
                "Name", "ID", "Game", "Role", "Skill- Level", "Personality-Type");

        System.out.println("-----------------------------------------------------");

        //Iterate through each team to be displayed
        for(Participant p : this.getMembers()){
            System.out.println(p.toDisplayString());
        }
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
        for (Participant p : this.members) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }





}