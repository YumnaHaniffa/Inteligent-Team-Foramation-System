package com.gameclub.team.controller;

import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

import javax.xml.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

public class TeamValidator implements TeamValidatorInt {


    //Team Validation Criteria as bellow,
    //1. Check Maximum number of players for a respective game
    //2. Check Personality Mix
    //3. Check Role Diversity
    //4. Check Skill balance


    //1. Check Maximum number of players for a respective game
    //=======================================================================================================================================//

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

    //2. Check Personality Mix
    //==================================================================================================================================//

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

    //=================================================================================================//
    //3. Check Role Diversity

    //b. Check for role diversity -
//    - Calculate average skill per team and store in averageSkills
//- Compute overall average skill
//- Compare each team’s average to the overall average
//- If deviation > threshold, add to result list
//- Return list of violations


    public List<Map<String, Object>> checkRoleDiversity(List<Team> teams) {

        List<Map<String, Object>> failedTeams = new ArrayList<>();
        for (Team team : teams) {
            //get the role count per team
            Map<String, Object> roleCount = new HashMap<>();
            for (Participant player : team.getMembers()) {
                roleCount.put(player.getPreferredRole(), roleCount.getOrDefault(player.getPreferredRole(), 0));

            }
            int uniqueRoles = roleCount.size();
            int size = team.getMembers().size();

            int requiredRoles;
            if (size > 5) {
                requiredRoles = 4;
            } else {
                requiredRoles = 3;
            }
            if (uniqueRoles < requiredRoles) {
                Map<String, Object> failure = new HashMap<>();
                failure.put("teamName", team.getTeamName());
                failure.put("uniqueRoles", uniqueRoles);
                failure.put("requiredRoles", requiredRoles);
                failure.put("reason", "role_diversity_violation");
                failure.put("missingCount", requiredRoles - uniqueRoles);

                failedTeams.add(failure);

            }

        }
        return failedTeams;
    }


    public void fixRoleDiversity(List<Team> teams) {

        List<Map<String,Object>> failedTeams = checkRoleDiversity(teams);

        if (!failedTeams.isEmpty()) {
            System.out.println("No role diversity issues detected.");
            return;
        }
        for (Map<String,Object> failedT : failedTeams) {
            Team failing_team = (Team) failedT.get("team");
            int requiredRole =  (Integer) failedT.get("requiredRole");
            int current = (Integer) failedT.get("uniqueRoles");

            //The existing role set
            Set<String> currentRoles = new HashSet<>();
            for (Participant player : failing_team.getMembers()) {
                currentRoles.add(player.getPreferredRole());
            }

            //Identify missing roles
            Set<String> missingRoles = new HashSet<>();
            List<String> allPossibleRoles = Arrays.asList("Strategist", "Attacker", "Defender", "Supporter", "Coordinator");
            for (String role : allPossibleRoles) {
                if (currentRoles.contains(role)) {
                    missingRoles.add(role);
                }
            }
            int neededRoles = requiredRole - current;
            List<String> required_missingList = missingRoles.stream().limit(neededRoles).toList();

            boolean fixed = false;

            //Find the swap from other teams
            for(Team swapTeam : teams) {
                if(swapTeam.equals(failing_team)) {
                    continue;
                }
                for (String neededRole : required_missingList) {

                    Participant swap_player = swapTeam.personality_lowestRankedPlayer(neededRole);
                    if(swap_player == null) {
                        continue;
                    }
                    //remove the lowest rank redundant role from failing team
                    Participant removeFromFailing = failing_team.personality_lowestRankedPlayer(neededRole);
                    if(removeFromFailing == null) {
                        continue;
                    }
                    //Perform the swap
                    failing_team.removePlayer(removeFromFailing);
                    swapTeam.removePlayer(swap_player);

                    failing_team.addPlayers(swap_player);
                    swapTeam.addPlayers(removeFromFailing);

                    System.out.println("ROLE FIX SWAP: " +
                            swap_player.getName() + " (" + swap_player.getPreferredRole() + ") moved to " + failing_team.getTeamName() +
                            ", swapped with " + removeFromFailing.getName() + " from " + swapTeam.getTeamName());

                    fixed = true;
                    break;
                }
                if(!fixed){
                    System.err.print("CRITICAL: Could not fix role diversity for team "+ failing_team.getTeamName());
                }

            }

            //revalidate personality mix after all the swaps
            List<Map<String, Object>> remainingIssues = checkRoleDiversity(teams);
            if (!remainingIssues.isEmpty()) {
                System.err.println("Some teams still role diversity after attempted fixes:");
                for (Map<String, Object> issue : remainingIssues) {
                    Team team = (Team) issue.get("team");
                    String reason = (String) issue.get("reason");
                    System.err.println("Team " + team.getTeamName() + " failed due to: " + reason);
                }
            } else {
                System.out.println("All role diversity violations resolved successfully.");
            }




        }
    }

















    //4. Check Skill Balance


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





















































