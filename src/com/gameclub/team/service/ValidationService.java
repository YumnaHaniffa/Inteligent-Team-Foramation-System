package com.gameclub.team.service;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

//Exception Handling and Validating team formation
public class ValidationService implements ValidationServiceInt {

    //Validate participant details -> Id, Name and email
    private String file_path;

    public boolean idExists(String filePath, String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean isFirstRow = true;

            while ((line = br.readLine()) != null) {

                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                String[] fields = line.split(",");

                if (fields.length > 0) {
                    String existingId = fields[0].trim();
                    if (existingId.equalsIgnoreCase(id)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file" + e.getMessage());
        }
        return false;

    }


    public String validate_id(String inputId) {

        String id_pattern = "^[A-Z]\\d{3}$";

        if (inputId == null || inputId.isEmpty()) {
            throw new IllegalArgumentException("The participant id cannot be empty "); //as the input length is not 4
        }
        //check if id follows the "P001" pattern
        if (!id_pattern.matches(id_pattern)) {
            throw new IllegalArgumentException("Invalid ID format (must be 1 letter + 3 digits, e.g., P001)");
        }
        //check if the id already exists
        if (idExists(file_path, inputId)) {
            throw new IllegalArgumentException("The participant id already exists");
        }

        return inputId;

    }

    public void validate_name(String inputName) {
        if (inputName == null || inputName.trim().isEmpty()) {
            throw new IllegalArgumentException("The participant name cannot be empty");
        }
        if (!inputName.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces)");
        }

    }


    //Validate email
    public String validate_email(String inputEmail) {
        String email_pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (inputEmail.isEmpty()) {
            throw new IllegalArgumentException("The participant email cannot be empty");
        }
        if (!email_pattern.matches(email_pattern)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return inputEmail;
    }


    // The validations for Survey data-> personality queries , interest and role selection
    // rate for each question be in (1-5) scale
    // the selecting of role should be in scale(1-5)
    //for the interest scale(1-5)
    public int validateScore(String rawInput, int min, int max) {
        int score;
        //Check if the input is an integer
        try {
            score = Integer.parseInt(rawInput.trim());
        } catch (NumberFormatException e) {
            throw new InputMismatchException("Input must be a whole number");

        }
        //Check if the input is within the scale of (1-5)
        if (score < min || score > max) {
            throw new InputMismatchException("Input must be between" + min + " and" + max);
        }
        return score;
    }

    public boolean validateSkillLevel(int inputSkillLevel) {
        return inputSkillLevel >= 1 && inputSkillLevel <= 10;
    }

    public boolean validateNormalizedScore(double score) {
        return score >= 50.0 && score <= 100;

    }


    //Team formation constraint handling

    //4. Check for the constraints
    // The algorithm checks a constraint, and if it fails, it fixes the failure, and then checks all constraints again until every rule is met.
    //-> IMPORTANT -> games that exceed the count by more than one
    //-> more than one game group exceed th cap

    //CHECK GAME CAP//
    public List<Map<String, Object>> checkGameCap(List<Team> teams, int gameMax) {

        //create list to store teams that failed the validation with the information
        List<Map<String, Object>> failedTeams = new ArrayList<>();


        //Create a map to store count of players for each game
        for (Team team : teams) {
            HashMap<String, Integer> gameCountMap = new HashMap<>(); // how many players for each game in each team

            //count the players to each game of the current team
            for (Participant player : team.getMembers()) {
                String gameName = player.getPreferredGame();
                gameCountMap.put(gameName, gameCountMap.getOrDefault(gameName, 0) + 1);
            }

            //Compare the count for the most common game against the defined cap
            // consider other limitations
            for (Map.Entry<String, Integer> entry : gameCountMap.entrySet()) {
                String gameName = entry.getKey();
                int count = entry.getValue();

                if (count > gameMax) {
                    Map<String, Object> failure = new HashMap<>();
                    failure.put("teamName", team.getTeamName());
                    failure.put("Team", team);
                    failure.put("gameName", gameName);
                    failure.put("count", count);
                    failedTeams.add(failure); // can add information on how each team violates the rule

                    break; //move to the next team

                }

            }
        }
        return failedTeams;

    }
    //IF FAILS -> what happens to the stored teams
    //1. Identify the player to be removed -> this is the player with the lowest rank(to reduce the impact of avg skill) in the falling team

    public void fixGameCapFailure(List<Map<String, Object>> failedTeams, List<Team> teams, int gameMax) {
        for (Map<String, Object> failedT : new ArrayList<>(failedTeams)) {
            Team failedTeam = (Team) failedT.get("team"); // get the current team from the filed teams
            String violatingGame = (String) failedT.get("violatingGame");

            //find the suitable player to be removed
            Participant playerToRemove = failedTeam.lowestRankedPlayerByGame(violatingGame);

            //Identify the target player to be swapped
            Participant playerToSwap = null;
            Team swapTeam = null;

            for (Team passedteam : teams) {

                if (!passedteam.equals(failedTeam)) {
                    double threshold;

                    if (playerToSwap != null) {
                        threshold = playerToSwap.getCompositeScore();
                    } else {
                        threshold = Double.MAX_VALUE;
                    }
                    Participant candidate = passedteam.FindBestSwapPlayer(violatingGame, threshold);
                    //to ensure the swap doesn't violate the cap on the passed teams

                    if (candidate != null) {

                        long countInSwapTeam = 0;
                        for (Participant p : passedteam.getMembers()) {
                            if (p.getPreferredGame().equals(playerToRemove.getPreferredGame())) {
                                countInSwapTeam++;
                            }

                        }
                        playerToSwap = candidate;
                        swapTeam = passedteam;
                        break;
                    }

                }

            }
            //Perform Swap
            if (playerToSwap != null) {
                failedTeams.remove(playerToRemove);
                failedTeam.addPlayers(playerToSwap);

                swapTeam.removePlayer(playerToSwap);
                swapTeam.addPlayers(playerToRemove);

                failedTeams.remove(failedT);

                //debugging statement
                System.out.println(
                        "SWAP: " + playerToRemove.getName() + " (" + violatingGame +
                                ") swapped from " + failedTeam.getTeamName() +
                                " with " + playerToSwap.getName() + " (" + playerToSwap.getPreferredGame() +
                                ") from " + swapTeam.getTeamName()
                );

            } else {
                // No suitable swap found
                System.err.println(
                        "CRITICAL: Could not fix Game Cap failure on team " +
                                failedTeam.getTeamName()
                );
            }
        }
        List<Map<String, Object>> remainingViolations = checkGameCap(teams, gameMax);

        if (!remainingViolations.isEmpty()) {
            System.err.println("Some teams still violate the game cap after attempted fixes:");
            for (Map<String, Object> violation : remainingViolations) {
                Team team = (Team) violation.get("team");
                String game = (String) violation.get("violatingGame");
                System.err.println("Team " + team.getTeamName() + " exceeds cap for game: " + game);
            }
        } else {
            System.out.println("All game cap violations resolved successfully.");
        }

    }

    //Check PersonalityMix
    public List<Map<String, Object>> checkPersonalityMix(List<Team> teams) {

        //store teams that fail in personality mix
        List<Map<String, Object>> personaFailedTeams = new ArrayList<>();

        //count how many unique personalities in each time
        for (Team team : teams) {
            int leaderCount = team.getPersonalityCount("Leader");
            int thinkerCount = team.getPersonalityCount("Thinker");
            int balancedCount = team.getPersonalityCount("Balanced");

            //see if all have personalities
            int total = leaderCount + thinkerCount + balancedCount;
            int actualSize = team.getMembers().size();

            String failureType = null;
            //critical failure
            if (leaderCount == 0) {
                failureType = "No_leaders";
            } else if (leaderCount > 1) {
                failureType = "too_many_leaders";
            } else if (thinkerCount == 0 || thinkerCount > 2) {
                failureType = "imbalance_thinker";

            } else if (total != actualSize) {
                failureType = "unclassified_personality";
            }
            if (failureType != null) {
                //give reasons for personality failure
                Map<String, Object> failure = new HashMap<>();// LATER HOW TO ADD THE TYPE
                failure.put("teamName", team.getTeamName());
                failure.put("reason", failureType);
                failure.put("leaderCount", leaderCount);
                failure.put("thinkerCount", thinkerCount);
                failure.put("balancedCount", balancedCount);
                personaFailedTeams.add(failure);
            }

        }
        return personaFailedTeams;


    }


    public void fixPersonalityFailure(List<Map<String, Object>> failedTeams, List<Team> teams) {

        //give priority to fixing "Leader"
        for (Map<String, Object> failure : failedTeams) {
            Team failingTeam = (Team) failure.get("team");
            String reason = (String) failure.get("reason");

            switch (reason) {
                case "No_leaders": {
                    // Remove the lowest-ranked 'Balanced' or 'Thinker' player from the failing team
                    Participant playerToRemove = failingTeam.personality_lowestRankedPlayer("Balanced");
                    if (playerToRemove == null) {
                        playerToRemove = failingTeam.personality_lowestRankedPlayer("Thinker");
                    }
                    if (playerToRemove == null) {
                        System.out.println("No suitable player to remove from " + failingTeam.getTeamName());
                        continue;
                    }
                    Participant playerToSwap = null;
                    Team swapTeam = null;
                    for (Team passedteam : teams) {
                        if (!passedteam.equals(failingTeam) && passedteam.getPersonalityCount("Leader") > 1) {
                            Participant candidate = passedteam.personality_lowestRankedPlayer("Leader");

                            if (candidate != null) {
                                playerToSwap = candidate;
                                swapTeam = passedteam;
                                break;
                            }
                        }
                    }
                    if (playerToSwap != null && swapTeam != null) {
                        failingTeam.removePlayer(playerToRemove);
                        swapTeam.removePlayer(playerToSwap);

                        failingTeam.addPlayers(playerToSwap);
                        swapTeam.addPlayers(playerToRemove);

                        System.out.println("SWAP: " + playerToSwap.getName() + " (Leader) moved to " +
                                failingTeam.getTeamName() + ", " + playerToRemove.getName() + " moved to " + swapTeam.getTeamName());


                    } else {
                        System.err.println("CRITICAL: Could not fix missing Leader in team" + failingTeam.getTeamName());
                    }
                    break;
                }

                case "too_many_leaders": {
                    // Remove the lowest-ranked 'Balanced' or 'Thinker' player from the failing team
                    Participant playerToRemove = failingTeam.personality_lowestRankedPlayer("Leader");

                    Participant playerToSwap = null;
                    Team swapTeam = null;

                    for (Team passedteam : teams) {
                        if (!passedteam.equals(failingTeam)) {
                            if (passedteam.getPersonalityCount("Balanced") > 0) {
                                playerToSwap = passedteam.personality_lowestRankedPlayer("Balanced");
                                swapTeam = passedteam;
                                break;
                            } else if (passedteam.getPersonalityCount("Thinker") < 2) {
                                playerToSwap = passedteam.personality_lowestRankedPlayer("Thinker");
                                swapTeam = passedteam;
                                break;
                            }
                        }
                    }
                    if (playerToSwap != null && swapTeam != null) {
                        failingTeam.removePlayer(playerToRemove);
                        swapTeam.removePlayer(playerToSwap);

                        failingTeam.addPlayers(playerToSwap);
                        swapTeam.addPlayers(playerToRemove);

                        System.out.println("SWAP: " + playerToSwap.getName() + " (Leader) moved to " +
                                failingTeam.getTeamName() + ", " + playerToRemove.getName() + " moved to " + swapTeam.getTeamName());


                    } else {
                        System.err.println("CRITICAL: Could not fix missing Leader in team" + failingTeam.getTeamName());
                    }
                    break;
                }
                case "invalid_thinker_count": {
                    // Fix by swapping excess Thinkers or adding one if missing
                    int thinkCount = failingTeam.getPersonalityCount("Thinker");
                    if (thinkCount < 2) {
                        Participant playerToRemove = failingTeam.personality_lowestRankedPlayer("Thinker");

                        Participant playerToSwap = null;
                        Team swapTeam = null;

                        for (Team passedteam : teams) {
                            if (!passedteam.equals(failingTeam)) {
                                if (passedteam.getPersonalityCount("Balanced") > 0) {
                                    playerToSwap = passedteam.personality_lowestRankedPlayer("Balanced");
                                    swapTeam = passedteam;
                                    break;
                                }
                            }
                        }

                        if (playerToSwap != null && swapTeam != null) {
                            failingTeam.removePlayer(playerToRemove);
                            swapTeam.removePlayer(playerToSwap);

                            failingTeam.addPlayers(playerToSwap);
                            swapTeam.addPlayers(playerToRemove);

                            System.out.println("SWAP: " + playerToSwap.getName() + " (Leader) moved to " +
                                    failingTeam.getTeamName() + ", " + playerToRemove.getName() + " moved to " + swapTeam.getTeamName());


                        } else {
                            System.err.println("CRITICAL: Could not fix missing Leader in team" + failingTeam.getTeamName());
                        }

                    } else if (thinkCount < 1) {
                        Participant playerToRemove = failingTeam.personality_lowestRankedPlayer("Balanced");

                        Participant playerToSwap = null;
                        Team swapTeam = null;

                        for (Team passedteam : teams) {
                            if (!passedteam.equals(failingTeam) && passedteam.getPersonalityCount("Thinker") > 1) {
                                playerToSwap = passedteam.personality_lowestRankedPlayer("Ba");
                                swapTeam = passedteam;
                                break;
                            }
                        }
                        if (playerToSwap != null && swapTeam != null) {
                            failingTeam.removePlayer(playerToRemove);
                            swapTeam.removePlayer(playerToSwap);

                            failingTeam.addPlayers(playerToSwap);
                            swapTeam.addPlayers(playerToRemove);

                            System.out.println("SWAP: " + playerToSwap.getName() + " (Leader) moved to " +
                                    failingTeam.getTeamName() + ", " + playerToRemove.getName() + " moved to " + swapTeam.getTeamName());


                        } else {
                            System.err.println("CRITICAL: Could not fix missing Leader in team" + failingTeam.getTeamName());
                        }
                        break;
                    }
                }

                default:
                    System.err.println("Unhandled personality failure type: " + reason);

            }
        }

        //revalidate personality mix after all the swaps
        List<Map<String, Object>> remainingIssues = checkPersonalityMix(teams);
        if (!remainingIssues.isEmpty()) {
            System.err.println("Some teams still violate personality mix after attempted fixes:");
            for (Map<String, Object> issue : remainingIssues) {
                Team team = (Team) issue.get("team");
                String reason = (String) issue.get("reason");
                System.err.println("Team " + team.getTeamName() + " failed due to: " + reason);
            }
        } else {
            System.out.println("All personality mix violations resolved successfully.");
        }
    }


    //b. Check for role diversity -
//    - Calculate average skill per team and store in averageSkills
//- Compute overall average skill
//- Compare each team’s average to the overall average
//- If deviation > threshold, add to result list
//- Return list of violations

    //IF FAILS ->

    public List<Map<String, Object>> checkSkillBalance(List<Team> teams, double skillThreshold) {

        List<Map<String, Object>> failedTeams = new ArrayList<>();
        //Calculate the average skill score  for every final team
        List<Double> averageSkills = new ArrayList<>();

        double totalSkillAllTeams = 0;
        int totalPlayers = 0;

        //1. Calculate average skill per team
        for (Team team : teams) {
            int teamSkill = 0;
            List<Participant> members = team.getMembers();
            for (Participant player : team.getMembers()) {
                teamSkill += player.getSkillLevel();
            }
            double avg;
            if (members.size() > 0) {
                avg = (double) teamSkill / members.size();
            } else {
                avg = 0;
            }
            averageSkills.add(avg);
            totalSkillAllTeams += teamSkill;
            totalPlayers += members.size();
        }
        //2. Compute overall avg skill
        double overallAvg;
        if (totalPlayers > 0) {
            overallAvg = totalSkillAllTeams / totalPlayers;
        } else {
            overallAvg = 0;
        }
        for (int i = 0; i < teams.size(); i++) {
            double teamAvg = averageSkills.get(i);
            double deviation = Math.abs(teamAvg - overallAvg);

            if (deviation > skillThreshold) {
                Map<String, Object> failure = new HashMap<>();
                failure.put("team", teams.get(i));
                failure.put("averageSkill", teamAvg);
                failure.put("deviation", deviation);
                failure.put("reason", "skill_imbalance");
                failedTeams.add(failure);
            }
        }
        return failedTeams;
    }

    //Fix the skill imbalance
    //1. Identify imbalanced teams -> checkSkillBalance
    //2. Sort teams based on avg skill
    //swap players accordingly
    public void fixSkillBalance(List<Team> teams, double skillThreshold) {
        List<Map<String, Object>> failedTeams = checkSkillBalance(teams, skillThreshold);

        if (failedTeams.isEmpty()) {
            System.out.println("No skill imbalance detected.");
            return;
        }
        // Step 1: Compute average skill for each team
        Map<Team, Double> teamAverages = new HashMap<>();
        double totalSkill = 0;
        int totalPlayers = 0;

        for (Team team : teams) {
            int teamSkill = team.getTotalSkill();
            int size = team.getMembers().size();
            double avg = (size > 0) ? (double) teamSkill / size : 0;
            teamAverages.put(team, avg);
            totalSkill += teamSkill;
            totalPlayers += size;
        }
        double overallAvg;
        if (totalPlayers > 0) {
            overallAvg = (double) totalSkill / totalPlayers;
        } else {
            overallAvg = 0;
        }

        // Step 2: Sort teams by average skill
        List<Team> sortedTeams = new ArrayList<>(teamAverages.keySet());
        sortedTeams.sort(Comparator.comparingDouble(teamAverages::get));

        int i = 0;
        int j = sortedTeams.size() - 1;

        while (i < j) {
            Team lowTeam = sortedTeams.get(i);
            Team highTeam = sortedTeams.get(j);

            double lowAvg = teamAverages.get(lowTeam);
            double highAvg = teamAverages.get(highTeam);

            if ((highAvg - lowAvg) <= skillThreshold) {
                break; // Already balanced
            }

            Participant highPlayer = highTeam.getHighestHighestSkilledPlayer();
            Participant lowPlayer = lowTeam.getLowestHighestSkilledPlayer();

            if (highPlayer == null || lowPlayer == null) {
                i++;
                j--;
                continue;
            }

            double newHighAvg = (highTeam.getTotalSkill() - highPlayer.getSkillLevel() + lowPlayer.getSkillLevel()) / (double) highTeam.getMembers().size();
            double newLowAvg = (lowTeam.getTotalSkill() - lowPlayer.getSkillLevel() + highPlayer.getSkillLevel()) / (double) lowTeam.getMembers().size();

            double newDeviation = Math.abs(newHighAvg - overallAvg) + Math.abs(newLowAvg - overallAvg);
            double currentDeviation = Math.abs(highAvg - overallAvg) + Math.abs(lowAvg - overallAvg);

            if (newDeviation < currentDeviation) {
                // Perform swap
                highTeam.removePlayer(highPlayer);
                lowTeam.removePlayer(lowPlayer);

                highTeam.addPlayers(lowPlayer);
                lowTeam.addPlayers(highPlayer);

                System.out.println("SWAP: " + highPlayer.getName() + " (Skill " + highPlayer.getSkillLevel() + ") from " +
                        highTeam.getTeamName() + " swapped with " + lowPlayer.getName() + " (Skill " + lowPlayer.getSkillLevel() + ") from " +
                        lowTeam.getTeamName());

                // Update averages
                teamAverages.put(highTeam, newHighAvg);
                teamAverages.put(lowTeam, newLowAvg);
            }

            i++;
            j--;

        }

        //revalidate personality mix after all the swaps
        List<Map<String, Object>> remaining = checkSkillBalance(teams, skillThreshold);
        if (!remaining.isEmpty()) {
            System.err.println("Some teams still violate skill balance after attempted fixes:");
            for (Map<String, Object> issue : remaining) {
                Team team = (Team) issue.get("team");
                double avg = (double) issue.get("averageSkill");
                double dev = (double) issue.get("deviation");
                System.err.println("Team " + team.getTeamName() + " avg skill = " + avg + " (deviation = " + dev + ")");
            }
        } else {
            System.out.println("All skill balance violations violations resolved successfully.");
        }


    }
}