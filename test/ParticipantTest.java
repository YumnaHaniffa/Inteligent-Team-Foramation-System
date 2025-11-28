import com.gameclub.team.model.Participant;

public class ParticipantTest {

    public static void main(String[] args) {
        //Create a sample participant
        Participant p = new Participant("P002", "Sandy", "sandy@university.edu", "FIFA", 5, "Supporter", 56.0, "Thinker");

        //Run tests
        runTest_pa("ID", p.getPlayerId(), "P002");
        runTest_pa("Name", p.getName(), "Sandy");
        runTest_pa("Email", p.getEmail(), "sandy@university.edu");
        runTest_pa("PreferredGame", p.getPreferredGame(), "FIFA");
        runTest_pa("SkillLevel", String.valueOf(p.getSkillLevel()), "5");
        runTest_pa("PreferredRole", p.getPreferredRole(), "Supporter");
        runTest_pa("PersonalityScore", String.valueOf(p.getNormalizedScore()), "56.0");
        runTest_pa("PersonalityType", p.getPersonalityType(), "Thinker");

        // Composite score check
        double expectedComposite = 56.0 + 5;
        runTest_pa("CompositeScore", String.valueOf(p.getCompositeScore()), String.valueOf(expectedComposite));
    }
        private static void runTest_pa(String field, String actual, String expected) {
            System.out.println(field + " | Expected: " + expected + " | Actual: " + actual +
                    " | " + (actual.equals(expected) ? "PASS" : "FAIL"));
        }

}
