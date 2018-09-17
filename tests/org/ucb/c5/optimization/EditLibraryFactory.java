package org.ucb.c5.optimization;

import org.ucb.c5.optimization.model.EditLibrary;
import org.ucb.c5.optimization.model.PromoterStrength;

/**
 * Constructs a hard-coded instance of EditLibrary
 * 
 * @author J. Christopher Anderson
 */
public class EditLibraryFactory {
    private final int numBins = 8;  //8 gene construct
    private final int numOptions = PromoterStrength.values().length; //6 promoters: OFF, SLOW, LOW, MED, HIGH, UBER
    
    public void initiate() throws Exception {

    }

    public EditLibrary run() throws Exception {
        String[][] partTemplates = new String[numBins][numOptions];
        for(int bin=0; bin<numBins; bin++) {
            for(int op=0; op<numOptions; op++) {
                String strength = PromoterStrength.values()[op].toString();
                partTemplates[bin][op] = "bin" + bin + "_" +strength;
            }
        }
        
        String[] partForOligos = new String[numBins];
        String[] partRevOligos = new String[numBins];
        String[] back1ForOligos = new String[numBins];
        String[] back1RevOligos = new String[numBins];
        String[] back2ForOligos = new String[numBins];
        String[] back2RevOligos = new String[numBins];
        for(int bin=0; bin<numBins; bin++) {
            back1ForOligos[bin] = "back1For" + bin;
            back1RevOligos[bin] = "back1Rev" + bin;
            back2ForOligos[bin] = "back2For" + bin;
            back2RevOligos[bin] = "back2Rev" + bin;
            partForOligos[bin] = "partFor" + bin;
            partRevOligos[bin] = "partRev" + bin;
        }
        
        return new EditLibrary(partTemplates, partForOligos, partRevOligos, back1ForOligos, back1RevOligos, back2ForOligos, back2RevOligos);
    }
    
    public static void main(String[] args) throws Exception {
        EditLibraryFactory factory = new EditLibraryFactory();
        factory.initiate();
        EditLibrary lib = factory.run();
        
        System.out.println("done");
    }

}
