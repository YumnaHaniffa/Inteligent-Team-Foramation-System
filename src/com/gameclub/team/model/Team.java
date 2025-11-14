package com.gameclub.team.model;
import java.util.ArrayList;
import java.util.List;

//Represents a collection of members
public class Team {


    private int teamSize;
    private String teamId;

    //participant list
    private List<Participant> members;

    public Team() {
        members = new ArrayList<>(); // what creating a team object a list will be created to add the members
         }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }
    //add participants
    public void addPlayers(Participant p) {
        members.add(p);

    }
    //get the participants
    public List<Participant> getMembers() {
        return members;
    }
    public Team getTeam() {
        return this;
    }
    public void addPlayers(){
        Participant p = new Participant();
        members.add(p);
    }

    //Implement the method for lowest ranked player
    public Participant lowestRankedPlayerByGame(String game){
        Participant lowestPlayer = null;
        for(Participant p : members){
            if(p.getPreferredGame().equalsIgnoreCase(game)){
                if(lowestPlayer == null ||p.getCompositeScore() <lowestPlayer.getCompositeScore()){
                    lowestPlayer = p;

                }
            }
        }
        return lowestPlayer;
    }

    //Find the best player to be swapped
    public Participant FindBestSwapPlayer (String violatingGame, int targetScore) {
        // Initialize the best candidate
        Participant bestCandidate = null;

        //Initiate the minimum difference -> what do need the min difference
        double min_diff = Double.MAX_VALUE;

        // loop through the list of players who are not in failed teams
        for(Participant player : members ){
            //check if the player selected is not interested in the game
            if(!player.getPreferredGame().equalsIgnoreCase(violatingGame)){
                //calculate the absolute difference between the players score and the target
                double currentDifference = Math.abs(player.getCompositeScore()-targetScore);

                //if this player is better  match than the current best
                if(currentDifference < min_diff){

                    //update the minimum difference
                    min_diff = currentDifference;

                    //set this palyer as the new best candidate
                    bestCandidate = player;
                }
            }
        }
        return bestCandidate;
    }




}
