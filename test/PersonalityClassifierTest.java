import com.gameclub.team.service.PersonalityClassifier;

public class PersonalityClassifierTest {

    public static void main(String[] args) {
        PersonalityClassifier classifier = new PersonalityClassifier();

        // Test cases
        runTestP(classifier, 90, "Leader");
        runTestP(classifier, 70, "Balanced");
        runTestP(classifier, 50, "Thinker");
        runTestP(classifier, 30, "Invalid");

        // Boundary values
        runTestP(classifier, 69, "Thinker");
        runTestP(classifier, 70, "Balanced");
        runTestP(classifier, 100, "Leader");
    }
    private static void runTestP(PersonalityClassifier classifier, double score, String expected) {
        String result = classifier.classify(score);
        System.out.println("Input: " + score +
                " | Expected: " + expected +
                " | Actual: " + result +
                " | " + (result.equals(expected) ? "PASS" : "FAIL"));
    }


}
