package org.ucb.c5.taskmaster;


import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
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
import org.ucb.c5.semiprotocol.model.Semiprotocol;


/**
 * Tests TaskDesigner on a single Construction File
 * describing the CRISPR EIPCR experiment
 * 
 * Uses the Hard-coded CRISPR Construction File code
 * present in ConstructionFile, and in slides
 * 
 * @author J. Christopher Anderson
 */
public class BasicConversionTest {

    private static TaskDesigner designer;
    private static SemiprotocolPriceSimulator sim;
    
    public BasicConversionTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
        designer = new TaskDesigner();
        designer.initiate();
        
        sim = new SemiprotocolPriceSimulator();
        sim.initiate();
    }

    @Test 
    public void testBasicConversionTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();
        
        //Hard-code a ConstructionFile describing a CRISPR Experiment
        List<Step> steps = new ArrayList<>();
        
        //>Construction of pTarg-amilGFP1
        String pdtName = "pTarg-amilGFP";
        
        //acquire oligo ca4238,ca4239
        steps.add((Step) new Acquisition("ca4238"));
        steps.add((Step) new Acquisition("ca4239"));
        steps.add((Step) new Acquisition("pTargetF"));
        
        //pcr ca4238,ca4239 on pTargetF	(3927 bp, ipcr)
        steps.add((Step) new PCR("ca4238", "ca4239", "pTargetF", "ipcr"));
        
        //cleanup ipcr	(pcr)
        steps.add((Step) new Cleanup("ipcr", "pcr"));
        
        //digest pcr with SpeI,DpnI	(spedig)
        List<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add((Step) new Digestion("pcr", enzymes, "spedig"));
        
        //cleanup spedig	(dig)
        steps.add((Step)  new Cleanup("spedig", "dig"));
        
        //ligate dig	(lig)
        List<String> digs = new ArrayList<>();
        digs.add("dig");
        steps.add((Step) new Ligation(digs, "lig"));
        
        //transform lig	(DH10B, Spec)
        steps.add((Step) new Transformation("lig", "DH10B", Antibiotic.Spec, pdtName));
        
        //Instantiate the Construction File
        ConstructionFile constf = new ConstructionFile(steps);
        cfs.add(constf);
        
        //Run the designer
        Semiprotocol[] semis = designer.run(cfs);
        
        //Price out the designed protocols
        double totalCost = 0.0;
        
        for(Semiprotocol protocol : semis) {
            double price = sim.run(protocol);
            totalCost += price;
        }
        
        //Report result
        SummarizeTaskMasterTests.report.append("BasicConversionTest:").append(totalCost).append("\n");

        System.out.println(semis[0].toString());
        System.out.println(semis[1].toString());
        System.out.println(semis[2].toString());
        
//        System.out.println("$"+totalCost);
        //The price should be under $100
        assertTrue(totalCost < 100.00);
    }
}
