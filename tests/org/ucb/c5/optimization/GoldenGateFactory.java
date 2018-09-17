package org.ucb.c5.optimization;

import java.util.ArrayList;
import java.util.List;
import org.ucb.c5.constructionfile.SerializeConstructionFile;
import org.ucb.c5.constructionfile.model.*;

/**
 * Designs a Construction file for one Golden Gate experiment provided the names 
 * of the oligos for 3 fragments, the name of the prototype plasmid, and the 
 * name of the product
 * 
 * @author J. Christopher Anderson
 */
public class GoldenGateFactory {
    
    public void initiate() {
        
    }
    
    public ConstructionFile run(String back1for, String back1rev,
                                String back2for, String back2rev,
                                String partfor, String partrev, String partTemplate,
                                String prototypePlasmid, String pdtPlasmid) throws Exception{
        
        //Hard-code a ConstructionFile describing a GG Experiment
        List<Step> steps = new ArrayList<>();
        
        //>Construction of single-edit product
        String pdtName = pdtPlasmid;
        
        ////acquire oligos and templates
        steps.add(new Acquisition(back1for));
        steps.add(new Acquisition(back1rev));
        steps.add(new Acquisition(back2for));
        steps.add(new Acquisition(back2rev));
        steps.add(new Acquisition(partfor));
        steps.add(new Acquisition(partrev));
        steps.add(new Acquisition(partTemplate));
        steps.add(new Acquisition(prototypePlasmid));
        
        //Do 3 PCRS
        steps.add(new PCR(back1for, back1rev, prototypePlasmid, "back1"));
        steps.add(new PCR(back2for, back2rev, prototypePlasmid, "back2"));
        steps.add(new PCR(partfor, partrev, partTemplate, "part"));
        
        //cleanup the PCRs
        steps.add(new Cleanup("back1", "back1_cp"));
        steps.add(new Cleanup("back2", "back2_cp"));
        steps.add(new Cleanup("part", "part_cp"));
        
        //Assembly the 3 fragments
        List<String> frags = new ArrayList<>();
        frags.add("back1");
        frags.add("back2");
        frags.add("part");
        steps.add(new Assembly(frags, Enzyme.BsmBI, "gg"));
        
        //transform lig	(DH10B, Spec)
        steps.add(new Transformation("gg", "Mach1", Antibiotic.Kan, pdtName));
        
        //Instantiate the Construction File and return
        ConstructionFile constf = new ConstructionFile(steps);
        return constf;
    }
    
    public static void main(String[] args) throws Exception {
        GoldenGateFactory factory = new GoldenGateFactory();
        factory.initiate();
        ConstructionFile constf = factory.run("o1", "o2", "o3", "o4", "o5", "o6", "p20N32", "pProto", "pMutant");
        
        SerializeConstructionFile serilaizer = new SerializeConstructionFile();
        serilaizer.initiate();
        System.out.println(serilaizer.run(constf));
    }
}
