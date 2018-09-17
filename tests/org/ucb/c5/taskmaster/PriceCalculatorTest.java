package org.ucb.c5.taskmaster;


import java.util.HashMap;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;
import org.ucb.bio134.taskmaster.PriceCalculator;
import org.ucb.bio134.taskmaster.model.Tip;
import org.ucb.c5.semiprotocol.model.Container;
import org.ucb.c5.semiprotocol.model.Reagent;


/**
 * Tests PriceCalculator correctness.  It counts up one of each
 * consumable and asserts the price.
 * 
 * If you do not agree with the assert value in this test, that
 * is OK.  Not all tests are valid, though I expect this one is.
 * 
 * @author J. Christopher Anderson
 */
public class PriceCalculatorTest {

    private static PriceCalculator calc;
    
    public PriceCalculatorTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
        calc = new PriceCalculator();
        calc.initiate();
    }

    @Test 
    public void testPriceCalculator() throws Exception {
        //Construct some example data
        Map<Reagent, Double> reagentCount = new HashMap<>();
        for(Reagent rgt : Reagent.values()) {
            reagentCount.put(rgt, 1.0);
        }
        
        Map<Container, Integer> tubeCount = new HashMap<>();
        for(Container acon : Container.values()) {
            tubeCount.put(acon, 1);
        }
        
        Map<Tip, Integer> tipCount = new HashMap<>();
        for(Tip atip : Tip.values()) {
            tipCount.put(atip, 1);
        }
        
        double price = calc.run(reagentCount, tubeCount, tipCount);
//        System.out.println(price);
        
         
        //Report result
        SummarizeTaskMasterTests.report.append("PriceCalculatorTest:").append(price).append("\n");

        
        assertEquals(price, 28.346, 0.1);
    }
}
