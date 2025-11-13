package com.gameclub.team.controller;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

import java.util.List;

//Shows what the team formation class does
public interface TeamFormationInt {

    //The participants will be sorted based on the composite score
    public List<Participant> sortParticipants(List<Participant> listOfParticipants);

    //The sorted participants  will be distributed using the snake-draft
    public List<Team> formTeams(List<Participant> listOfParticipants, int numTeams);
}
