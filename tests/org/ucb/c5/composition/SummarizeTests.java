package org.ucb.c5.composition;

import java.util.List;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Creates a report after running all the JUnit tests
 *
 * @author J. Christopher Anderson
 */
public class SummarizeTests {

    public static void main(String[] args) {
        //Create a StringBuilder to hold the summary text
        StringBuilder summary = new StringBuilder();

        //Run each test file
        JUnitCore junit = new JUnitCore();

        summary.append(">CompositionToDNATest\n");
        Result result = junit.run(CompositionToDNATest.class);
        summarize(result, summary);

        summary.append(">Team1Test\n");
        result = junit.run(Team1Test.class);
        summarize(result, summary);

        summary.append(">Team2Test\n");
        result = junit.run(Team2Test.class);
        summarize(result, summary);

        summary.append(">Team3Test\n");
        result = junit.run(Team3Test.class);
        summarize(result, summary);

        summary.append(">Team4Test\n");
        result = junit.run(Team4Test.class);
        summarize(result, summary);

        System.out.println(summary.toString());
    }

    private static void summarize(Result result, StringBuilder sb) {
        long runtime = result.getRunTime();
        sb.append("runtime: ").append(runtime).append("\nfailures: \n");

        List<Failure> failures = result.getFailures();
        for (Failure fail : failures) {
            String[] splitted = fail.getTestHeader().split("\\(");
            sb.append("\t").append(splitted[0]).append("\n");
        }
        sb.append("\n");
    }
}
