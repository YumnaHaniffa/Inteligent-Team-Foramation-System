package com.gameclub.team.thread;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

public final class BestSwapInfo {
    //need a simple data class to store the information about a beneficial swap before it is applied
    // Data class to hold the details of the single best swap found during

    public final String teamAName;
    public final String teamBName;
    public final String participantXName;
    public final String participantYName;
    public double improvementScore;



    public BestSwapInfo(String tA, String tB, String pX, String pY, double score) {
        this.teamAName = tA;
        this.teamBName = tB;
        this.participantXName = pX;
        this.participantYName = pY;
        this.improvementScore = score;
    }
}
