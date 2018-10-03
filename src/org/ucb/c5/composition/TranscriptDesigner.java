package org.ucb.c5.composition;

import org.ucb.c5.composition.model.RBSOption;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.sequtils.HairpinCounter;
import org.ucb.c5.utils.FileUtils;

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
    private SequenceChecker sequenceChecker;

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

        //initialize the sequence Checker
        sequenceChecker = new SequenceChecker();
        sequenceChecker.initiate();


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

    //change return to a map sorted by highest valid to lowest valid
    private void sequenceGenerator (String peptWindow, List<String> choices) throws Exception{
        String firstAA = peptWindow.substring(0,1);
        String secondAA = peptWindow.substring(1, 2);
        String thirdAA = peptWindow.substring(2);


        for (String[] codon1: aminoAcidToCodon.get(firstAA)) {
            for (String[] codon2: aminoAcidToCodon.get(secondAA)) {
                for (String[] codon3: aminoAcidToCodon.get(thirdAA)) {
                    String seq = codon1[0]+codon2[0]+codon3[0];
//                    int totalCAI = Integer.parseInt(codon1[1])+ Integer.parseInt(codon2[1])+ Integer.parseInt(codon3[1]);
                    if (sequenceChecker.run(seq) == false) {
                        continue;
                    }

                    if (hairpinCounter.run(seq) <= 10.0) {
                        choices.add(seq);
                    }
                }
            }
        }


    }

    public Transcript run(String peptide, Set<RBSOption> ignores) throws Exception {
       //OLD Code:
       //Choose codons for each amino acid
        String[] codons = new String[peptide.length()];
        String testingPeptide = "W"+peptide+"*WW";

        //How to access codon table: Map --> List --> tuple[Codon, CAI]
//        System.out.println(aminoAcidToCodon.get("W").get(0)[0]);
//        System.out.println(aminoAcidToCodon.get("W").get(0)[1]);

        for (int i = 1; i <= peptide.length(); i++) {
            String peptWindow = testingPeptide.substring(i-1, i+2);
            String bestCodonWindow;

            //has all possible choices for window sequence
            List<String> choices = new ArrayList<>();
            sequenceGenerator(peptWindow, choices);

            bestCodonWindow = choices.get(0);

            codons[i-1] = bestCodonWindow.substring(3,6);
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
