package org.ucb.c5.taskmaster;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
import org.ucb.c5.constructionfile.model.*;
import org.ucb.c5.semiprotocol.model.LabOp;
import org.ucb.c5.semiprotocol.model.Semiprotocol;
import org.ucb.c5.semiprotocol.model.Task;

/**
 * Examines the plausibility of the Semiprotocols
 * designed by TaskDesigner for a very simple
 * two-PCR ConstructionFile
 * 
 * modeled after basicConversionTest.java
 *
 * @author Laura Taylor
 * @author J. Christopher Anderson (added comments)
 */
public class BasicSemiprotocolTest {

    private static TaskDesigner td;
    private static SemiprotocolPriceSimulator sps;

    @BeforeClass
    public static void init() throws Exception {
        td = new TaskDesigner();
        td.initiate();
        sps = new SemiprotocolPriceSimulator();
        sps.initiate();
    }

    @Test
    public void testPCR() throws Exception {
        //Construct 2 PCR steps
        ArrayList<Step> steps = new ArrayList<>();
        String oligo1 = "oligo1";
        String oligo2 = "oligo2";
        String template = "template";
        String product = "pcr1Product";

        Step pcr1 = new PCR(oligo1, oligo2, template, product);
        steps.add(pcr1);

        String diffOligo1 = "diffOligo1";
        String diffOligo2 = "diffOligo2";
        String diffTemplate = "diffTemplate";
        String diffProduct = "pcr2Product";

        Step pcr2 = new PCR(diffOligo1, diffOligo2, diffTemplate, diffProduct);
        steps.add(pcr2);

        //Run TaskDesigner on the 2-PCR-step ConstructionFile
        ConstructionFile cf = new ConstructionFile(steps);
        List<ConstructionFile> cfiles = new ArrayList<ConstructionFile>();
        cfiles.add(cf);
        Semiprotocol[] results = td.run(cfiles);

        //Calculate the prices of each semiprotocol
        Double pcrPrice = 0.0;
        pcrPrice = sps.run(results[0]);

        Double digestPrice = 0.0;
        digestPrice = sps.run(results[1]);//should be zero since there are no digestions

        Double ligationPrice = 0.0;
        ligationPrice = sps.run(results[2]);//should be zero since there are no ligations

        //Count up addContainer, transfer, and dispense steps
        List<Task> pcrSemiprotocolSteps = results[0].getSteps();
        int addContainerCount = 0;
        int transferCount = 0;
        int dispenseCount = 0;
        for (Task t : pcrSemiprotocolSteps) {
            LabOp taskType = t.getOperation();
            switch (taskType) {
                case addContainer:
                    addContainerCount += 1;
                    break;
                case transfer:
                    transferCount += 1;
                    break;
                case dispense:
                    dispenseCount += 1;
                    break;
            }
        }
        
        //two transfers of water for the oligo dilution (or two dispenses)
        //two more transfers (or two dispenses) of water + two transfers for the final oligo dilution
        //1 transfer or dispense of water for the template dilution + 1 transfer of the template
        //1 transfer for the master mix
        //2 transfers for the oligos + 1 transfer for the template
        //2 add containers for the oligos
        //2 add containers for the second oligos dilution
        //1 add container for template dilution
        //1 container for pcr
        //x2 for everything for the two pcrs
        //minimum: 12 addContainer, 8 transfers, 1 dispense (water is either dispensed into a designated water tube or into the dilution tubes)

        assertTrue(digestPrice == 0.0);
        assertTrue(ligationPrice == 0.0);
        assertFalse(pcrPrice == 0.0);
        assertTrue(addContainerCount >= 12);
        assertTrue(transferCount >= 8);
        assertTrue(dispenseCount >= 1);
    }
}
