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

    private Map<String, List<Codon>> aminoAcidToCodon;
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
        String data2 = FileUtils.readResourceFile("composition/data/coli_genes.txt");

        aminoAcidToCodon = new HashMap<>();
        parserForCodonTable(data);

        //initialize the hairpin Counter
        hairpinCounter = new HairpinCounter();
        hairpinCounter.initiate();

        //initialize the sequence Checker
        sequenceChecker = new SequenceChecker();
        sequenceChecker.initiate();


    }

    class Codon {

        private double CAI;
        private String codon;

        public Codon (String seq, double cai) {
            CAI = cai;
            codon = seq;
        }

        public double getCAI() {
            return CAI;
        }

        public String getCodon() {
            return codon;
        }
    }

    private void parserForCodonTable(String filedata) {
        String[] amino = filedata.split("\\r|\\r?\\n");

        for (int a = 0; a < amino.length; a++) {
            String[] parts = amino[a].split("\\s");

            List<Codon> codons = new ArrayList<>();
            for (int i = 1; i < parts.length; i += 2) {
                Codon cod = new Codon(parts[i], Double.parseDouble(parts[i+1]));
                codons.add(cod);
            }

            aminoAcidToCodon.put(parts[0], codons);

        }
    }

    //change return to a map sorted by highest valid to lowest valid
    private void sequenceGenerator (String peptWindow, List<String> choices) throws Exception{
        String firstAA = peptWindow.substring(0,1);
        String secondAA = peptWindow.substring(1, 2);
        String thirdAA = peptWindow.substring(2);


        for (Codon codon1: aminoAcidToCodon.get(firstAA)) {
            for (Codon codon2: aminoAcidToCodon.get(secondAA)) {
                for (Codon codon3: aminoAcidToCodon.get(thirdAA)) {
                    String seq = codon1.getCodon()+codon2.getCodon()+codon3.getCodon();

                    if (validSequence(seq)) {
                        choices.add(seq);
                    }
                }
            }
        }


    }

    private boolean validSequence (String seq) throws Exception{
        boolean valid = false;
        seq = seq.toUpperCase();

        if (sequenceChecker.run(seq)) {
            if (hairpinCounter.run(seq) == 0.00) {
                valid = true;
            }
        }

        return valid;
    }

    private double gcContentFreq(String seq) {
        int gCtracker = 0;

        seq = seq.toUpperCase();

        for (int i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == 'G' || seq.charAt(i) == 'C') { gCtracker++; }
        }

        double gcFreq = gCtracker/seq.length();

        return gcFreq;
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

            //has all possible choices for window sequence
            List<String> choices = new ArrayList<>();
            sequenceGenerator(peptWindow, choices);

            String bestCodon = choices.get(0).substring(3,6);
            double bestGCContent = gcContentFreq(choices.get(0));
            double thresholdGC = 0.6*peptide.length()/9;
            
            for (String sequence : choices) {

                double gc_seq = gcContentFreq(sequence);

                if (bestGCContent > thresholdGC) {
                    break;
                }
                if (gc_seq > bestGCContent ) {
                    bestCodon = sequence.substring(3,6);
                    bestGCContent = gc_seq;

                    if (i > 1 && codons[i-1] == sequence.substring(0,3)) { break; }
                }
            }


            codons[i-1] = bestCodon;
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
