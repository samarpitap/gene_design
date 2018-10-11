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
    private void sequenceGenerator (String peptWindow, List<String> choices, String codon1) throws Exception{
        String secondAA = peptWindow.substring(1, 2);
        String thirdAA = peptWindow.substring(2);


        for (Codon codon2: aminoAcidToCodon.get(secondAA)) {
            for (Codon codon3: aminoAcidToCodon.get(thirdAA)) {
                String seq = codon1+codon2.getCodon()+codon3.getCodon();
                choices.add(seq);
            }
        }



    }

    private double gcContentFreq(String seq) {
        double gCtracker = 0.0;

        seq = seq.toUpperCase();

        for (int i = 0; i < seq.length(); i++) {
            if (seq.charAt(i) == 'G' || seq.charAt(i) == 'C') { gCtracker++; }
        }

        double gcFreq = gCtracker/(double)(seq.length());

        return gcFreq;
    }


    public Transcript run(String peptide, Set<RBSOption> ignores) throws Exception {
       //OLD Code:
       //Choose codons for each amino acid
        if (peptide.length() == 0) {
            throw new IllegalArgumentException("Peptide cannot be empty string");
        }

        for (int i = 0; i < peptide.length(); i++) {
            String aa = Character.toString(peptide.charAt(i));
            if (!aminoAcidToCodon.containsKey(aa)) {
                throw new IllegalArgumentException("Peptide has illegal amino acids");
            }
        }

        String testingPeptide = "W"+peptide+"WW";
        String[] codons = new String[testingPeptide.length()];
        codons[0] = "TGG";
//        codons[0] = "ATG"; //for M


        for (int i = 1; i < testingPeptide.length()-1; i++) {
            String peptWindow = testingPeptide.substring(i-1, i+2);

            //has all possible choices for window sequence
            List<String> choices = new ArrayList<>();
            sequenceGenerator(peptWindow, choices, codons[i-1]);


            String upstream = "";
            for (int c = 0; c < i; c++) {
                upstream += codons[c];
            }

            if (upstream.length() > 6) {
                upstream = upstream.substring(upstream.length()-6);
            }
//            if (i < 2) {
//                upstream = "TGGATG";
//            } else if (i >= 2) {
//                for (int c = 0; c < i; c++) {
//                    upstream += codons[c];
//                }
//
//                if (upstream.length() > 6) {
//                    upstream = upstream.substring(upstream.length()-6);
//                }
//            }

            List<String> forbiddenSeq = new ArrayList<>();
            for (String seq : choices) {
                String check = upstream+seq.substring(3);
                boolean wtf = sequenceChecker.run(check);
                if (!(sequenceChecker.run(check))){
                    forbiddenSeq.add(seq);
                }

                if (hairpinCounter.run(check) != 0) {
                    forbiddenSeq.add(seq);
                }

            }

            if (forbiddenSeq.size() < choices.size()) {
                for (String rmv : forbiddenSeq) {
                    choices.remove(rmv);
                }
            }

            String bestCodon = choices.get(0).substring(3,6);
//            double lowestHairpin = hairpinCounter.run(upstream+choices.get(0).substring(3));
//            for (String seq: choices) {
//                String codon = seq.substring(3,6);
//                String check = upstream + seq.substring(3);
//                double hpin = hairpinCounter.run(check);
//
//                if (hpin < lowestHairpin) {
//                    bestCodon = codon;
//                    lowestHairpin = hpin;
//                }
//            }

            codons[i] = bestCodon;
        }

        String[] actualCodons = new String[peptide.length()];
        for (int i=0; i < actualCodons.length; i++) {
            actualCodons[i] = codons[i+1];
        }

        codons = actualCodons;


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
