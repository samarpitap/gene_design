package org.ucb.c5.taskmaster;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
import org.ucb.c5.constructionfile.model.*;
import org.ucb.c5.semiprotocol.model.Semiprotocol;

/**
 * A test of TaskDesigner that creates 24 CRISPR ConstructionFiles and designs
 * the Semiprotocols for their simultaneous execution.
 *
 * Each construction file differs only by the name of the forward oligo and name
 * of the product plasmid. This scenario can be minimized for all cost metrics
 * through reuse, multichannel pipetting, and mastermixes
 *
 * @author J. Christopher Anderson
 */
public class MastermixTest {

    private static TaskDesigner designer;
    private static SemiprotocolPriceSimulator sim;

    public MastermixTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        designer = new TaskDesigner();
        designer.initiate();

        sim = new SemiprotocolPriceSimulator();
        sim.initiate();
    }

    @Test
    public void testMastermixTest() throws Exception {
        //Create the ConstructionFiles
        List<ConstructionFile> cfs = new ArrayList<>();

        //Hard-code 24 ConstructionFiles describing similar CRISPR Experiments
        for (int i = 0; i < 24; i++) {
            ConstructionFile constf = createCRISPR(i);
            cfs.add(constf);
        }

        //Run the designer
        Semiprotocol[] semis = designer.run(cfs);

        //Price out the designed protocols
        double totalCost = 0.0;

        for (Semiprotocol protocol : semis) {
            double price = sim.run(protocol);
            totalCost += price;
        }
        
                System.out.println(semis[0].toString());
        System.out.println(semis[1].toString());
        System.out.println(semis[2].toString());
        
        //Report result
        SummarizeTaskMasterTests.report.append("MastermixTest:").append(totalCost).append("\n");
        
        //The price should be under $2400
        assertTrue(totalCost < 2400.00);
    }

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
}
