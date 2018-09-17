package org.ucb.c5.optimization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ucb.c5.constructionfile.model.ConstructionFile;
import org.ucb.c5.optimization.model.EditLibrary;
import org.ucb.c5.optimization.model.PromoterStrength;
import org.ucb.c5.utils.FileUtils;

/**
 *
 * @author J. Christopher Anderson
 */
public class EditLibraryToConstructionFiles {
    private GoldenGateFactory ggFactory;
    
    public void initiate() throws Exception {
        ggFactory = new GoldenGateFactory();
        ggFactory.initiate();
    }
    
    public List<ConstructionFile> run(EditLibrary lib, String prototype) throws Exception {
        List<ConstructionFile> out = new ArrayList<>();
        String[][] templates = lib.getPartTemplates();
        
        for(int bin=0; bin<templates.length; bin++) {
            for(int op = 0; op < templates[bin].length; op++) {
                String back1for = lib.getBack1ForOligos()[bin];
                String back1rev = lib.getBack1RevOligos()[bin];
                
                String back2for = lib.getBack2ForOligos()[bin];
                String back2rev = lib.getBack2RevOligos()[bin];
                
                String partfor = lib.getPartForOligos()[bin];
                String partrev = lib.getPartRevOligos()[bin];
                
                String partTemplate = templates[bin][op];
                String pdtPlasmid = prototype + "_bin" + bin + PromoterStrength.values()[op].toString();
                
                ConstructionFile constf = ggFactory.run(back1for, back1rev, back2for, back2rev, partfor, partrev, partTemplate, prototype, pdtPlasmid);
                out.add(constf);
            }
        }
        return out;
    }
    public static void main(String[] args) throws Exception {
        EditLibraryToConstructionFiles etc = new EditLibraryToConstructionFiles();
        etc.initiate();
        EditLibraryFactory factory = new EditLibraryFactory();
        factory.initiate();
        
        EditLibrary library = factory.run();
        List<ConstructionFile> cfs = etc.run(library, "pAPAP1");
        
        //Save to new director
        File dir = new File("ETC_ouput");
        if(dir.exists()) {
            for(File afile : dir.listFiles()) {
                afile.delete();
            }
        } else {
            dir.mkdir();
        }
        
        System.out.println("done");
    }
}
