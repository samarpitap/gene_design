package org.ucb.c5.composition;

import org.ucb.c5.composition.model.RBSOption;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.sequtils.HairpinCounter;
import org.ucb.c5.utils.FileUtils;
import org.ucb.c5.utils.TSVParser;

import java.util.*;


/**
 * This reverse translates a protein sequence to a DNA and chooses an RBS. It
 * uses the highest CAI codon for each amino acid in the specified Host.
 *
 * @author J. Christopher Anderson
 */
public class TranscriptDesigner {

    private Map<String, List<String[]>> aminoAcidToCodon;
    private RBSChooser rbsChooser;
    private HairpinCounter hairpinCounter;

    public void initiate() throws Exception {
        //Initialize the RBSChooser
        rbsChooser = new RBSChooser();
        rbsChooser.initiate();

        //Construct a map between each amino acid and it's codons
        //ordered by highest-CAI for E coli
        String data = FileUtils.readResourceFile("composition/data/codon_table.txt");
        aminoAcidToCodon = new HashMap<>();
        parserforCodonTable(data);

        //initialize the hairpin Counter
        hairpinCounter = new HairpinCounter();
        hairpinCounter.initiate();


    }

    private void parserforCodonTable(String filedata) {
        String[] amino = filedata.split("\\r|\\r?\\n");

        for (int a = 0; a < amino.length; a++) {
            String[] parts = amino[a].split("\\s");

            List<String[]> codons = new ArrayList<>();
            for (int i = 1; i < parts.length; i += 2) {
                String[] codon = {parts[i], parts[i+1]};
                codons.add(codon);
            }

            aminoAcidToCodon.put(parts[0], codons);

        }
    }

    public Transcript run(String peptide, Set<RBSOption> ignores) throws Exception {
       //OLD Code:
       //Choose codons for each amino acid
        String[] codons = new String[peptide.length()];
        String testingPeptide = "M"+peptide+"*WW";

        for (int i = 1; i < peptide.length()+1; i++) {
            String peptWindow = testingPeptide.substring(i-1, i+1);
            String bestCodonWindow;










        }



        //Choose an RBS
        StringBuilder cds = new StringBuilder();
        for(int i=0; i<codons.length; i++) {
            cds.append(codons[i]);
        }
        RBSOption selectedRBS = rbsChooser.run(cds.toString(), ignores);

        //Construct the Transcript and return it
        Transcript out = new Transcript(selectedRBS, peptide, codons);
        return out;

    }
}
