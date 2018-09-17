package org.ucb.c5.taskmaster;


import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
import org.ucb.c5.constructionfile.SerializeConstructionFile;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.optimization.EditLibraryFactory;
import org.ucb.c5.optimization.EditLibraryToConstructionFiles;
import org.ucb.c5.optimization.model.EditLibrary;
import org.ucb.c5.semiprotocol.model.Semiprotocol;


/**
 * This tests TaskDesigner on the single-part edit optimization
 * experiment. This is the most common experiment to be done in the wetlab
 * and thus the most important of these tests. 
 * 
 * This code, and the code in the org.ucb.c5.optimization
 * packages generates the List of ConstructionFile for making 48 promoter
 * variants of a plasmid called pAPAP1. It scans 6 different promoter strengths
 * for all 8 CDS in a plasmid.
 * 
 * This scenario will greatly benefit from mastermixes, thoughful arraying,
 * and multichannel pipetting.
 * 
 * @author J. Christopher Anderson
 */
public class OptimizationTest {

    private static EditLibraryToConstructionFiles etc;
    private static EditLibraryFactory factory;
    private static TaskDesigner designer;
    private static SemiprotocolPriceSimulator sim;
    
    public OptimizationTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
        etc = new EditLibraryToConstructionFiles();
        etc.initiate();
        
        factory = new EditLibraryFactory();
        factory.initiate();
        
        designer = new TaskDesigner();
        designer.initiate();
        
        sim = new SemiprotocolPriceSimulator();
        sim.initiate();
    }

    @Test //(timeout = 30000)
    public void testGGEditingLibrary() throws Exception {
        //Create the ConstructionFiles
        EditLibrary library = factory.run();
        List<ConstructionFile> cfs = etc.run(library, "pAPAP1");
        
        //Run the designer
        Semiprotocol[] semis = designer.run(cfs);
        
        //Price out the designed protocols
        double totalCost = 0.0;
        
        for(Semiprotocol protocol : semis) {
            double price = sim.run(protocol);
            totalCost += price;
        }
        
                System.out.println(semis[0].toString());
        System.out.println(semis[1].toString());
        System.out.println(semis[2].toString());
        
        //Report result
        SummarizeTaskMasterTests.report.append("OptimizationTest:").append(totalCost).append("\n<><><><><>\n");
        
        //Serialize the Semiprotocols in the report
        for(int i=0; i<semis.length; i++) {
            SummarizeTaskMasterTests.report.append("\n++++semiprotocol:").append(i).append("\n").append(semis[i].toString());
        }
        
        SummarizeTaskMasterTests.report.append("\n<><><><><>\n");
        
        //The price should be under $10,000
        assertTrue(totalCost < 10000.00);
        
        //The number of protocols should be 3
        assertEquals(semis.length, 3);
    }
}
