package com.gameclub.team.controller;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;
import com.gameclub.team.service.FileService;

import java.util.ArrayList;
import java.util.List;

public class OrganizerController {
    //Requirement to upload file data
    private String filePath;
    public List<Participant> uploadParticipantData(String file_path) {

        this.filePath = file_path;
        FileService fileService = new FileService(file_path);
        return fileService.loadParticipants();
    }

    //Initialize team formation
    public List<Team> initiateTeamFormation(List<Participant> participants, int teamSize){
        System.out.println("\n================================================");
        System.out.println(" INITIATING TEAM FORMATION PROCESS ");
        System.out.println("=============================================");

        //Define the required constraints
        final int game_max = 2;
        final double skill_threshold = 1.5;

        TeamBuilder teamBuilder = new TeamBuilder();
        TeamValidator teamValidator = new TeamValidator();

        //1. Sort the participants based on composite score
        List<Participant> sortedParticipants = teamBuilder.sortParticipants(participants);
        System.out.println("Participants Sorted by Composite Score.");

        //2. Form the teams using the ground logic - snake draft
        List<Team> teams = teamBuilder.formTeams(sortedParticipants, teamSize);
        System.out.println("Initial Teams Formed");

        //3. Apply the constraints to validate the formed teams
        System.out.println("\n--- Applying Team Validation and Optimization ---"); // for debugging purposes

        //Game cap
        System.out.println("Checking Game Cap ...");
        teamValidator.fixGameCapFailure(teamValidator.checkGameCap(teams,game_max),teams,game_max);

        //personality mix
        System.out.println("Checking personality mix ...");
        teamValidator.fixPersonalityFailure(teamValidator.checkPersonalityMix(teams),teams);

        //role diversity
        System.out.println("Checking role diversity...");
        teamValidator.fixRoleDiversity(teams);

        //Ensuring skill balance
        System.out.println("Checking Skill Balance");
        teamValidator.fixSkillBalance(teams,skill_threshold);

        System.out.println("Checking Team Validation Completed");
        return teams;


    }


}
