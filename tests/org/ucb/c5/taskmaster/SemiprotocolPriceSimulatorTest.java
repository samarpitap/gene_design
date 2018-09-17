package org.ucb.c5.taskmaster;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.c5.semiprotocol.ParseSemiprotocol;
import org.ucb.c5.semiprotocol.model.Semiprotocol;

/**
 * Tests SemiprotocolPriceSimulator correctness.  It creates a mastermix
 * and asserts the price based on PriceCalculator
 * 
 * If you do not agree with the assert value in this test, that
 * is OK.  Not all tests are valid, though I expect this one is.
 * 
 * @author J. Christopher Anderson
 */
public class SemiprotocolPriceSimulatorTest {
    private static SemiprotocolPriceSimulator calc;
    
    public SemiprotocolPriceSimulatorTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
        calc = new SemiprotocolPriceSimulator();
        calc.initiate();
    }
    
    @Test 
    public void testSemiprotocolPriceSimulator() throws Exception {
        String text = "addContainer	eppendorf_1p5mL	pcr_mm	tube_rack/A1	TRUE\n" +
"dispense	water	pcr_mm	352	\n" +
"dispense	Q5_Polymerase_Buffer_5x	pcr_mm	110	\n" +
"dispense	dNTPs_2mM	pcr_mm	55	\n" +
"dispense	Q5_polymerase	pcr_mm	5.5	\n" +
"addContainer	pcr_plate_96	pcr_plate	deck/A1	TRUE\n" +
"transfer	pcr_mm	pcr_plate/A1	47.5	\n" +
"transfer	pcr_mm	pcr_plate/A2	47.5	\n" +
"transfer	pcr_mm	pcr_plate/A3	47.5	\n" +
"transfer	pcr_mm	pcr_plate/A4	47.5	";
        ParseSemiprotocol parser = new ParseSemiprotocol();
        parser.initiate();
        Semiprotocol protocol = parser.run(text);
        SemiprotocolPriceSimulator sim = new SemiprotocolPriceSimulator();
        sim.initiate();
        double price = sim.run(protocol);
        
        //Report result
        SummarizeTaskMasterTests.report.append("SemiprotocolPriceSimulatorTest:").append(price).append("\n");

        
//        System.out.println("$" + price);
        
        assertEquals(price, 32.04, 0.1);
    }
}
