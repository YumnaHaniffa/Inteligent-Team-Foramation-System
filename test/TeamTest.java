import com.gameclub.team.model.Participant;
import com.gameclub.team.model.Team;

public class TeamTest {

    public static void main(String[] args) {

        Team team = new Team("Team1");

        // Create sample participants
        Participant p1 = new Participant("P001", "Bani", "bani@uni.edu",
                "FIFA", 5, "Supporter", 56.0, "Thinker");
        Participant p2 = new Participant("P002", "Ali", "ali@uni.edu",
                "Valorant", 7, "Leader", 95.0, "Leader");
        Participant p3 = new Participant("P002","Ali","a@uni.edu","FIFA",7,"Leader",95.0,"Leader");


        // Test for addPlayer()
        team.addPlayers(p1);
        runTest("Add valid participant", team.getMembers().size(), 1);
        team.addPlayers(p2);
        runTest("Add second participant", team.getMembers().size(), 2);
        team.addPlayers(p3);
        runTest("Add second participant", team.getMembers().size(), 2);
        team.addPlayers(null);
        runTest("Add null participant", team.getMembers().size(), 2);

        //Tests for getGameCount()
        System.out.println();
        System.out.println("GameCount FIFA | Expected: 2 | Actual: " + team.getGameCount("FIFA"));
        System.out.println("GameCount Valorant | Expected: 1 | Actual: " + team.getGameCount("Valorant"));
        System.out.println("GameCount Cricket | Expected: 0 | Actual: " + team.getGameCount("Cricket"));
        System.out.println("GameCount null | Expected: 0 | Actual: " + team.getGameCount(null));

        //Test for getAverageSkill
        System.out.println("AverageSkill | Expected: 6.33.0 | Actual: " + team.getAverageSkill());

        Team emptyTeam = new Team("Empty");
        System.out.println("AverageSkill Empty | Expected: 0.0 | Actual: " + emptyTeam.getAverageSkill());

        Team singleTeam = new Team("Single");
        Participant p4 = new Participant("P004","John","j@uni.edu","Basketball",10,"Defender",70.0,"Balanced");
        singleTeam.addPlayers(p4);
        System.out.println("AverageSkill Single | Expected: 10.0 | Actual: " + singleTeam.getAverageSkill());

        //Test for getUniqueRoleCount
        System.out.println("UniqueRoleCount | Expected: 3 | Actual: " + team.getUniqueRoleCount());

        Team roleTeam = new Team("RoleTeam");
        roleTeam.addPlayers(p1); // Supporter
        roleTeam.addPlayers(p2); // Leader

        roleTeam.addPlayers(new Participant("P005","Mary","m@uni.edu","FIFA",4,"Supporter",50.0,"Thinker"));
        System.out.println("UniqueRoleCount | Expected: 2 | Actual: " + roleTeam.getUniqueRoleCount());



    }

    private static void runTest(String field, Object actual, Object expected) {
        System.out.println(field + " | Expected: " + expected + " | Actual: " + actual +
                " | " + (actual.equals(expected) ? "PASS" : "FAIL"));
    }


}

