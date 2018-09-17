package org.ucb.c5.taskmaster;


import java.io.File;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ucb.bio134.taskmaster.PriceCalculator;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
import org.ucb.bio134.taskmaster.model.Tip;
import org.ucb.c5.constructionfile.ParseConstructionFile;
import org.ucb.c5.constructionfile.model.Acquisition;
import org.ucb.c5.constructionfile.model.Antibiotic;
import org.ucb.c5.constructionfile.model.Cleanup;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.constructionfile.model.Digestion;
import org.ucb.c5.constructionfile.model.Enzyme;
import org.ucb.c5.constructionfile.model.Ligation;
import org.ucb.c5.constructionfile.model.PCR;
import org.ucb.c5.constructionfile.model.Step;
import org.ucb.c5.constructionfile.model.Transformation;
import org.ucb.c5.semiprotocol.ParseSemiprotocol;
import org.ucb.c5.semiprotocol.model.*;
import org.ucb.c5.utils.FileUtils;


/**
 * Put your name if you contribute to this class !
 * 
 * @author PG
 * @author Rudra Mehta
 * @author Matthew Sit
 * @author Jerry Li
 * @author Manraj Gill
 */
public class CorrectnessBatteryTest {

    private static PriceCalculator pc;
    private static SemiprotocolPriceSimulator sim;

    public CorrectnessBatteryTest() {}

    @Before
    public void setUpClass() throws Exception {
        pc = new PriceCalculator();
        pc.initiate();
        
        sim = new SemiprotocolPriceSimulator();
        sim.initiate();
    }

    /**
     * Init data as in PriceCalculator.main
     * The price is $102.061
     *
     * @author Rudra Mehta
     */
    @Test 
    public void simplePriceCalculatorTest() throws Exception {
        Map<Reagent, Double> reagentCount = new HashMap<>();
        reagentCount.put(Reagent.BsaI, 32.0);
        reagentCount.put(Reagent.T4_DNA_Ligase_Buffer_10x, 32.0);
        reagentCount.put(Reagent.T4_DNA_ligase, 16.0);

        Map<Container, Integer> tubeCount = new HashMap<>();
        tubeCount.put(Container.pcr_strip, 3);
        tubeCount.put(Container.pcr_tube, 8);
        tubeCount.put(Container.pcr_plate_96, 1);
        tubeCount.put(Container.eppendorf_1p5mL, 3);
        tubeCount.put(Container.eppendorf_2mL, 2);

        Map<Tip, Integer> tipCount = new HashMap<>();
        tipCount.put(Tip.P20, 86);
        tipCount.put(Tip.P200, 23);
        tipCount.put(Tip.P1000, 6);

        double price = pc.run(reagentCount, tubeCount, tipCount); // PG: reagentCount should have fixed signature. 
        assertEquals(102.061, price, .01);
    }
    
    /**
     * Init data as in SemiprotocolPriceSimulator.main
     * The price is $1.83
     *
     * @author Rudra Mehta
     */
    @Test 
    public void simpleSemiprotocolPriceSimulatorTest() throws Exception {
        //Read in the example semiprotocol
        String text = FileUtils.readResourceFile("semiprotocol/data/alibaba_semiprotocol.txt");
        ParseSemiprotocol parser = new ParseSemiprotocol();
        parser.initiate();
        Semiprotocol protocol = parser.run(text);
        SemiprotocolPriceSimulator sim = new SemiprotocolPriceSimulator();
        sim.initiate();
        double price = sim.run(protocol);
        assertEquals(1.83, price, .01);
    }

    /**
     * Read protocol_1.txt file from hwprotocols/test1 dir
     * Calculate the price. 
     * 1uL of enzyme costs $2.0
     * 1. PCR requires 0.5uL of Q5Polymerase
     * 2. Digestion requires: 0.5uL SpeI and 0.5uL of DpnI
     * 3. Ligation  requires: 0.5uL T4DNALigase 
     * In total we use 2uL of enzymes adding up to 2x$2=$4.0
     * The price of the protocol should be more than $4.0
     * but not more than about $8.0
     *
     * @author Rudra Mehta
     */
    @Test 
    public void singleProtocolFile() throws Exception {
        ParseConstructionFile parser = new ParseConstructionFile();
        parser.initiate();

        //Read in the bag of construction files
        List<ConstructionFile> cfiles = new ArrayList<>();
        String data = FileUtils.readResourceFile("taskmaster/data/singleProtocolFileTest/protocol_1.txt");
        ConstructionFile constf = parser.run(data);
        cfiles.add(constf);

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();

        Semiprotocol[] semis = designer.run(cfiles);

        //Price out the designed protocols
        double totalCost = 0.0;
        SemiprotocolPriceSimulator sim = new SemiprotocolPriceSimulator();
        sim.initiate();

        for(Semiprotocol protocol : semis) {
            double price = sim.run(protocol);
            totalCost += price;
        }

        assertTrue( totalCost > 4.0 );
        assertTrue( totalCost < 8.0 );
  
    }

    /**
     * Confirms correctness of the hardcoded prices of the Tips, Reagents and Containers
     *
     * @author Manraj Gill
     */
    @Test
    public void dataInputTest() throws Exception {
        Map<Reagent, Double> reagentCount = new HashMap<>();
        reagentCount.put(Reagent.water, 1.);
        reagentCount.put(Reagent.phusion, 1.);
        reagentCount.put(Reagent.DpnI, 1.);
        reagentCount.put(Reagent.Q5_polymerase, 1.);
        reagentCount.put(Reagent.BamHI, 1.);
        reagentCount.put(Reagent.BglII, 1.);
        reagentCount.put(Reagent.BsaI, 1.);
        reagentCount.put(Reagent.BsmBI, 1.);
        reagentCount.put(Reagent.T4_DNA_ligase, 1.);
        reagentCount.put(Reagent.SpeI, 1.);
        reagentCount.put(Reagent.XhoI, 1.);
        reagentCount.put(Reagent.XbaI, 1.);
        reagentCount.put(Reagent.PstI, 1.);
        reagentCount.put(Reagent.Hindiii, 1.);
        reagentCount.put(Reagent.T4_DNA_Ligase_Buffer_10x, 1.);
        reagentCount.put(Reagent.NEB_Buffer_2_10x, 1.);
        reagentCount.put(Reagent.NEB_Buffer_3_10x, 1.);
        reagentCount.put(Reagent.NEB_Buffer_4_10x, 1.);
        reagentCount.put(Reagent.Q5_Polymerase_Buffer_5x, 1.);
        reagentCount.put(Reagent.dNTPs_2mM, 1.);
        reagentCount.put(Reagent.zymo_10b, 1.);
        reagentCount.put(Reagent.Zymo_5a, 1.);
        reagentCount.put(Reagent.JM109, 1.);
        reagentCount.put(Reagent.DH10B, 1.);
        reagentCount.put(Reagent.MC1061, 1.);
        reagentCount.put(Reagent.Ec100D_pir116, 1.);
        reagentCount.put(Reagent.Ec100D_pir_plus, 1.);
        reagentCount.put(Reagent.lb_agar_50ug_ml_kan, 1.);
        reagentCount.put(Reagent.lb_agar_100ug_ml_amp, 1.);
        reagentCount.put(Reagent.lb_agar_100ug_ml_specto, 1.);
        reagentCount.put(Reagent.lb_agar_100ug_ml_cm, 1.);
        reagentCount.put(Reagent.lb_agar_noAB, 1.);
        reagentCount.put(Reagent.arabinose_10p, 1.);
        reagentCount.put(Reagent.lb_specto, 1.);
        reagentCount.put(Reagent.lb_amp, 1.);
        reagentCount.put(Reagent.lb_kan, 1.);
        reagentCount.put(Reagent.lb_cam, 1.);
        reagentCount.put(Reagent.lb, 1.);

        Map<Container, Integer> tubeCount = new HashMap<>();
        tubeCount.put(Container.pcr_strip, 1);
        tubeCount.put(Container.pcr_tube, 1);
        tubeCount.put(Container.pcr_plate_96, 1);
        tubeCount.put(Container.eppendorf_1p5mL, 1);
        tubeCount.put(Container.eppendorf_2mL, 1);

        Map<Tip, Integer> tipCount = new HashMap<>();
        tipCount.put(Tip.P20, 1);
        tipCount.put(Tip.P200, 1);
        tipCount.put(Tip.P1000, 1);

        double price = pc.run(reagentCount, tubeCount, tipCount);
        assertEquals(28.346, price, 0.01);
    }

    /**
     * Init data as in MastermixTest
     * Count the number of AddContainer operations with flag "false"
     * This is the number of DNA acquisitions that you have done
     * The answer is 26:
     * A. 24 different oligo1
     * B. Same oligo2 for all the files
     * C. same pTargetF for all the files
     * @author Matthew Sit
     */
    @Test 
    public void numAcqusitionsMastermixTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 24 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 24; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        int falseFlagCount = 0;

        Semiprotocol protocol = semis[0];
        List<Task> steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.addContainer && !((AddContainer) t).isNew())
                falseFlagCount++;
        }

        assertEquals(26, falseFlagCount);  // 25 for oligos plus one for template
    }

    /**
     * Init data as in MastermixTest
     *
     * Here we want to make sure people are using pcr_plate_96 when it's cost efficient
     *
     * @author Samson Mataraso
     */
    @Test
    public void numContainersTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 24 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 24; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        int pcr_tubes = 0;

        for (Semiprotocol protocol : semis) {
            List<Task> steps = protocol.getSteps();
            for (Task t : steps) {
                if (t.getOperation() == LabOp.addContainer && ((AddContainer)t).isNew()) {
                    if(((AddContainer)t).getTubetype().equals(Container.pcr_tube)) {
                        pcr_tubes++;
                    }
                }
            }
        }

        assertEquals(true, pcr_tubes < 72);
    }


    /**
     * Init data as in MastermixTest
     * Count the number of AddContainer operiations with flag "true" ...
     * ... in ligations Semiprotocol.
     * Mastermix is done is a single Eppendorf tube so in the test take only these 
     * into account. 
     * If optimization is correct the answer is 1
     * @author Matthew Sit
     */
    @Test 
    public void ligationsMastermixTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 24 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 24; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        int trueFlagCount = 0;

        Semiprotocol protocol = semis[2];
        List<Task> steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.addContainer && ((AddContainer) t).isNew())
                trueFlagCount++;
        }

        assertEquals(1, trueFlagCount);
    }
    
    /**
     * Tests to see if handles large volumes properly
     * You should have more than 1 eppendorf tube for the ligation mastermix,
     * since a 2mL eppendorf supports about 200 reactions, give or take.
     * @author Jerry Li
     */
    @Test 
    public void ligationsOverflowTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 2000 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 2000; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        int eppendorfCount = 0;

        Semiprotocol protocol = semis[2];
        List<Task> steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.addContainer && ((AddContainer) t).isNew()){
                if(((AddContainer)t).getTubetype().equals(Container.eppendorf_1p5mL)) {
                        eppendorfCount++;
                    }
                else if(((AddContainer)t).getTubetype().equals(Container.eppendorf_2mL)) {
                        eppendorfCount++;
                    }
            }
        }
        
        assertTrue(5<eppendorfCount);
    }
    
    /**
     * Tests to see if handles large volumes properly
     * You should never put more volume than the max capacity of an object.
     * Since it's hard to try to tell what the destination is,
     * this test will just check if you are trying to put more than
     * 1900uL into any one container.
     * @author Jerry Li
     */
    @Test 
    public void OverflowTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 2000 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 2000; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        boolean overflow = false;

        Semiprotocol protocol = semis[2];
        List<Task> steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.transfer){
                if (((Transfer)t).getVolume()>1900){
                    overflow = true;
                }
            }
            if (t.getOperation() == LabOp.dispense){
                if (((Dispense)t).getVolume()>1900){
                    overflow = true;
                }
            }
        }
        protocol = semis[1];
        steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.transfer){
                if (((Transfer)t).getVolume()>1900){
                    overflow = true;
                }
            }
            if (t.getOperation() == LabOp.dispense){
                if (((Dispense)t).getVolume()>1900){
                    overflow = true;
                }
            }
        }
        protocol = semis[0];
        steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.transfer){
                if (((Transfer)t).getVolume()>1900){
                    overflow = true;
                }
            }
            if (t.getOperation() == LabOp.dispense){
                if (((Dispense)t).getVolume()>1900){
                    overflow = true;
                }
            }
        }
        
        assertFalse(overflow);
    }
    
    
    /**
     * Tests to see if any optimization has occurred
     * There should be some price improvement if similar CRISPR experiments are run
     * @author Jerry Li
     */
    public void optimized() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 10 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 10; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }
        
        List<ConstructionFile> cfs2 = new ArrayList<>();

        //Hard-code 1 ConstructionFile describing a CRISPR Experiment
        for (int i = 0; i < 1; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs2.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis10 = designer.run(cfs);
        Semiprotocol[] semis1 = designer.run(cfs2);

        SemiprotocolPriceSimulator sim = new SemiprotocolPriceSimulator();
        sim.initiate();
        
        double price10 = 0.0;
        price10+=sim.run(semis10[0]);
        price10+=sim.run(semis10[1]);
        price10+=sim.run(semis10[2]);
        double normalizedPrice10=price10/10.0;
        
        double price1 = 0.0;
        price1+=sim.run(semis1[0]);
        price1+=sim.run(semis1[1]);
        price1+=sim.run(semis1[2]);
        
        
        assertTrue(normalizedPrice10<price1);
    }
    
    /**
     * Tests to see if PCR plates are used when they should
     * If there is exactly 96 pcrs, 96 digests, and 96 ligations to do,
     * optimally, you should use 3 PCR plates instead of a ton of
     * pcr tubes
     * @author Jerry Li
     */
    @Test 
    public void pcrPlateUsage() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 96 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 96; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        int pcrCount = 0;

        Semiprotocol protocol = semis[0];
        List<Task> steps = protocol.getSteps();
        for (Task t : steps) {
            if (t.getOperation() == LabOp.addContainer && ((AddContainer) t).isNew())
                if(((AddContainer)t).getTubetype().equals(Container.pcr_plate_96)) {
                        pcrCount++;
                    }
        }

        assertEquals(1, pcrCount);  // 1 for pcr
        
        int pcrCount2 = 0;
        Semiprotocol protocol2 = semis[1];
        List<Task> steps2 = protocol2.getSteps();
        for (Task t : steps2) {
            if (t.getOperation() == LabOp.addContainer && ((AddContainer) t).isNew())
                if(((AddContainer)t).getTubetype().equals(Container.pcr_plate_96)) {
                        pcrCount2++;
                    }
        }
        
        assertEquals(1, pcrCount2);  // 1 for digest
        
        int pcrCount3 = 0;
        Semiprotocol protocol3 = semis[2];
        List<Task> steps3 = protocol3.getSteps();
        for (Task t : steps3) {
            if (t.getOperation() == LabOp.addContainer && ((AddContainer) t).isNew())
                if(((AddContainer)t).getTubetype().equals(Container.pcr_plate_96)) {
                        pcrCount3++;
                    }
        }
        
        assertEquals(1, pcrCount3);  // 1 for ligation
    }
    
    /**
     * Tests to see if the algorithm is deterministic
     * if input the exact same construction files, the output must be the same
     * @author Jerry Li
     */
    @Test 
    public void testDeterministic() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 76 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 76; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }
        
        List<ConstructionFile> cfs2 = new ArrayList<>();

        //Hard-code 76 ConstructionFile describing a CRISPR Experiment
        for (int i = 0; i < 76; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs2.add(constf);
        }

        //Run the designer, thrice on essentially the same input, as the createCRIPR is deterministic
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis1 = designer.run(cfs);
        Semiprotocol[] semis2 = designer.run(cfs2);
        Semiprotocol[] semis3 = designer.run(cfs);

        //makes sure they are all the same
        assertEquals(semis1[0].toString(),semis2[0].toString());
        assertEquals(semis1[1].toString(),semis2[1].toString());
        assertEquals(semis1[2].toString(),semis2[2].toString());
        assertEquals(semis3[0].toString(),semis2[0].toString());
        assertEquals(semis3[1].toString(),semis2[1].toString());
        assertEquals(semis3[2].toString(),semis2[2].toString());
        assertEquals(semis1[0].toString(),semis3[0].toString());
        assertEquals(semis1[1].toString(),semis3[1].toString());
        assertEquals(semis1[2].toString(),semis3[2].toString());
    }

    /** @author JCA (skeleton code)
     */
    private ConstructionFile createCRISPR(int i) {
        List<Step> steps = new ArrayList<>();
        //>Construction of pTarg-amilGFP1
        String pdtName = "pTarg-amil" + i;

        String forOligo = "ca000" + i;

        //acquire oligo ca4238,ca4239
        steps.add(new Acquisition(forOligo));
        steps.add(new Acquisition("ca4239"));
        steps.add(new Acquisition("pTargetF"));

        //pcr ca4238,ca4239 on pTargetF	(3927 bp, ipcr)
        steps.add(new PCR(forOligo, "ca4239", "pTargetF", "ipcr"));

        //cleanup ipcr	(pcr)
        steps.add(new Cleanup("ipcr", "pcr"));

        //digest pcr with SpeI,DpnI	(spedig)
        List<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add(new Digestion("pcr", enzymes, "spedig"));

        //cleanup spedig	(dig)
        steps.add(new Cleanup("spedig", "dig"));

        //ligate dig	(lig)
        List<String> digs = new ArrayList<>();
        digs.add("dig");
        steps.add(new Ligation(digs, "lig"));

        //transform lig	(DH10B, Spec)
        steps.add(new Transformation("lig", "DH10B", Antibiotic.Spec, pdtName));

        //Instantiate the Construction File
        ConstructionFile constf = new ConstructionFile(steps);
        return constf;
    }

    /**
     *
     * Tests to make sure you're returning at least the min cost of the reaction,
     * which is calculated as the cost of just the reagents (no containers or tips).
     *
     * @author Katherine Bigelow
     * @throws Exception
     */
    @Test
    public void absoluteMinCostTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();
        //Hard-code 24 ConstructionFiles describing similar CRISPR Experiments
        ConstructionFile constf = createCRISPR(0);
        cfs.add(constf);

        //Run the designer
        TaskDesigner designer = new TaskDesigner();
        designer.initiate();
        Semiprotocol[] semis = designer.run(cfs);

        double totalCost = 0.0;

        for(Semiprotocol protocol : semis) {
            double price = sim.run(protocol);
            totalCost += price;
        }
        ArrayList<Task>temp_semi = new ArrayList<>();
        //do one pcr, one ligation, one digestion
        temp_semi.add(new Dispense(Reagent.dNTPs_2mM, "null", 5.0));
        temp_semi.add(new Dispense(Reagent.Q5_polymerase, "null", 0.5));

        temp_semi.add(new Dispense(Reagent.SpeI, "null", 0.5));
        temp_semi.add(new Dispense(Reagent.DpnI, "null", 0.5));

        temp_semi.add(new Dispense(Reagent.T4_DNA_ligase, "null", 0.5));

        double price = sim.run(new Semiprotocol(temp_semi));
        assertTrue(totalCost >= price);

    }
}
