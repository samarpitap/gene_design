package org.ucb.c5.taskmaster;

import org.ucb.c5.composition.*;
import java.util.List;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.ucb.c5.utils.FileUtils;

/**
 *
 * @author J. Christopher Anderson
 */
public class SummarizeTaskMasterTests {
    
    public static StringBuilder report = new StringBuilder();

    public static void main(String[] args) {
        //Create a StringBuilder to hold the summary text
        StringBuilder summary = new StringBuilder();
        
        //Run each test file
        JUnitCore junit = new JUnitCore();
        
        //SemiprotocolPriceSimulatorTest
        summary.append(">PriceCalculatorTest\n");
        Result result = junit.run(PriceCalculatorTest.class);
        summarize(result, summary);
        
        summary.append(">SemiprotocolPriceSimulatorTest\n");
        result = junit.run(SemiprotocolPriceSimulatorTest.class);
        summarize(result, summary);
        
        summary.append(">BasicConversionTest\n");
        result = junit.run(BasicConversionTest.class);
        summarize(result, summary);
        
        summary.append(">MastermixTest\n");
        result = junit.run(MastermixTest.class);
        summarize(result, summary);
        
        summary.append(">OptimizationTest\n");
        result = junit.run(OptimizationTest.class);
        summarize(result, summary);
        
        summary.append(">BasicSemiprotocolTest\n");
        result = junit.run(BasicSemiprotocolTest.class);
        summarize(result, summary);
        
        summary.append(">CorrectnessBatteryTest\n");
        result = junit.run(CorrectnessBatteryTest.class);
        summarize(result, summary);
        
        //Put the test results into the report
        report.append(summary);
        System.out.println(report.toString());
        FileUtils.writeFile(report.toString(), "TaskDesigner_Report.txt");
    }
    
    private static void summarize(Result result, StringBuilder sb) {
        long runtime = result.getRunTime();
        sb.append("runtime: ").append(runtime).append("\nfailures: \n");
        
        List<Failure> failures = result.getFailures();
        for(Failure fail : failures) {
            String[] splitted = fail.getTestHeader().split("\\(");
            sb.append("\t").append(splitted[0]).append("\n");
        }
        sb.append("\n");
    }
}
