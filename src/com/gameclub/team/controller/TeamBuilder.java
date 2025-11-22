package com.gameclub.team.controller;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

import java.sql.ClientInfoStatus;
import java.util.*;

//Input data-> team size N
//      participant in a arraylist ->

//Algorithm
//1. The composite score = personality score +skill level is calculated for each participant -> done in Participant class
public class TeamBuilder{

//2. The participants will be sorted based on the composite score
    public List<Participant> sortParticipants(List<Participant> listOfParticipants) {
        //sort using Comparator, descending order by composite score
        listOfParticipants.sort(Comparator.comparingDouble(Participant::getCompositeScore).reversed());
        return  listOfParticipants;
    }

    public int calculateNumberOfTeams(List<Participant> listOfParticipants,int teamSize) {
        return (int)  Math.ceil((double)listOfParticipants.size()/teamSize);
    }


//3. The sorted participants  will be distributed using the snake-draft

    //a. initializes the required number of empty teams based on number of participants and team size
    public List<Team> formTeams(List<Participant> listOfParticipants,  int teamSize) {

        int numberOfTeams = calculateNumberOfTeams(listOfParticipants,teamSize);
        List<Team> teams = new ArrayList<>(numberOfTeams);
        //Initialize the teams list
        Map<Integer,Integer> teamCounts = new HashMap<>();
        //initialize the empty team objects
        for (int j = 0; j < numberOfTeams; j++) {
            teams.add(new Team("Team" +(j+1))); //Initialize each team
            teamCounts.put(j,0);   //Initialize team count
        }

        int teamIndex = 0;
        int direction= 1;

        // get the current player at index i
        //check size of team before assigning so that participants are not assigned to already filled teams
        for (Participant currentPlayer : listOfParticipants) {
            while(teamCounts.get(teamIndex) >=  teamSize) {
                if(direction==1) {
                    teamIndex++;
                    if(teamIndex == numberOfTeams) {
                        direction = -1;
                        teamIndex = numberOfTeams -1 ;
                    }

                }else{
                    teamIndex--;
                    if(teamIndex < 0) {
                        direction = 1;
                        teamIndex =0;
                    }
                }
            }
            //b. Forward distribution round

            // assign the players for each team from the highest rank accordingly
            teams.get(teamIndex).addPlayers(currentPlayer);
            teamCounts.put(teamIndex,teamCounts.get(teamIndex)+1);

            //d. Backwards distribution round
            //assign the players for each team moving backwards, start from the team that last assigned

            if(direction==1) {
                teamIndex++;
                if(teamIndex == numberOfTeams) {
                    direction = -1;
                    teamIndex = numberOfTeams -1 ;
                }
            }else {
                teamIndex--;
                if(teamIndex < 0) {
                    direction = 1;
                    teamIndex =0;
                }
            }

        }
        return teams;

    }
    public static  void displayTeams(List<Team> teams) {
        if (teams == null || teams.isEmpty()){
            System.out.println("No teams were formed or teams list is empty.");
            return;
        }
        System.out.println("\\n==========================================");
        System.out.println(" FINAL TEAMS LIST");
        System.out.println("\\n==========================================");


        for (Team currentTeam : teams) {
            currentTeam.displayTeamDetails();

        }
        System.out.println("Team formation process completed.");

    }


}










































