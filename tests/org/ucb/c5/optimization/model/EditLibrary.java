package org.ucb.c5.optimization.model;

/**
 *
 * @author J. Christopher Anderson
 */
public class EditLibrary {
    private final String[][] partTemplates;   //length bin x option, template plasmids
    private final String[] partForOligos; //length bin
    private final String[] partRevOligos; //length bin
    
    private final String[] back1ForOligos; //length bin
    private final String[] back1RevOligos; //length bin
    
    private final String[] back2ForOligos; //length bin
    private final String[] back2RevOligos; //length bin

    public EditLibrary(String[][] partTemplates, String[] partForOligos, String[] partRevOligos, String[] back1ForOligos, String[] back1RevOligos, String[] back2ForOligos, String[] back2RevOligos) {
        this.partTemplates = partTemplates;
        this.partForOligos = partForOligos;
        this.partRevOligos = partRevOligos;
        this.back1ForOligos = back1ForOligos;
        this.back1RevOligos = back1RevOligos;
        this.back2ForOligos = back2ForOligos;
        this.back2RevOligos = back2RevOligos;
    }

    public String[][] getPartTemplates() {
        return partTemplates;
    }

    public String[] getPartForOligos() {
        return partForOligos;
    }

    public String[] getPartRevOligos() {
        return partRevOligos;
    }

    public String[] getBack1ForOligos() {
        return back1ForOligos;
    }

    public String[] getBack1RevOligos() {
        return back1RevOligos;
    }

    public String[] getBack2ForOligos() {
        return back2ForOligos;
    }

    public String[] getBack2RevOligos() {
        return back2RevOligos;
    }
}
