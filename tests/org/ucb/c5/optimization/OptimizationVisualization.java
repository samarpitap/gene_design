package org.ucb.c5.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.andersonlab.pipetteaid.controller.Controller;
import org.andersonlab.pipetteaid.view.View;
import org.junit.BeforeClass;
import org.ucb.bio134.taskmaster.SemiprotocolPriceSimulator;
import org.ucb.bio134.taskmaster.TaskDesigner;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.optimization.model.EditLibrary;
import org.ucb.c5.semiprotocol.model.Semiprotocol;

/**
 * 
 * This Function creates the example Optimization experiment,
 * runs your TaskDesigner to get Semiprotocols, then instantiates
 * the GUI with its solution.
 * 
 * This may or may not work with your code, don't be concerned about
 * your grade wrt this working.
 *
 * @author J. Christopher Anderson
 */
public class OptimizationVisualization {
    private EditLibraryToConstructionFiles etc;
    private EditLibraryFactory factory;
    private TaskDesigner designer;
    private SemiprotocolPriceSimulator sim;
    
    public void initiate() throws Exception {
        etc = new EditLibraryToConstructionFiles();
        etc.initiate();
        
        factory = new EditLibraryFactory();
        factory.initiate();
        
        designer = new TaskDesigner();
        designer.initiate();
        
        sim = new SemiprotocolPriceSimulator();
        sim.initiate();
    }
    
    public void run() throws Exception {
        //Create the ConstructionFiles
        EditLibrary library = factory.run();
        List<ConstructionFile> cfs = etc.run(library, "pAPAP1");
        
        //Run the designer
        Semiprotocol[] semis = designer.run(cfs);
        Semiprotocol protocol = semis[0];
        
        //Create the View
        View view = new View(protocol);
        
        //Create the Controller and initiate the GUI
        Controller controller = new Controller(protocol, view);
    }
    public static void main(String[] args) throws Exception {
        OptimizationVisualization vis = new OptimizationVisualization();
        vis.initiate();
        vis.run();
    }
}
