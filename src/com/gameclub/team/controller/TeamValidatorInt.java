package com.gameclub.team.controller;

import com.gameclub.team.model.Team;

import java.util.List;
import java.util.Map;

public interface TeamValidatorInt {

    //=======================================================================================================================//
    public List<Map<String, Object>> checkGameCap(List<Team> teams, int gameMax);
    public void fixGameCapFailure(List<Map<String, Object>> failedTeams, List<Team> teams, int gameMax);
    public List<Map<String, Object>> checkPersonalityMix(List<Team> teams);
    public void fixPersonalityFailure(List<Map<String, Object>> failedTeams, List<Team> teams);
    public List<Map<String,Object>> checkRoleDiversity(List<Team> teams);
    public void fixRoleDiversity(List<Team> teams);
    public List<Map<String, Object>> checkSkillBalance(List<Team> teams, double skillThreshold);
    public void fixSkillBalance(List<Team> teams, double skillThreshold);





}
