
package org.ucb.c5.composition;


import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.ucb.c5.composition.model.Host;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.composition.model.Construct;
import org.ucb.c5.composition.model.Composition;
import org.ucb.c5.sequtils.HairpinCounter;
import org.ucb.c5.sequtils.RevComp;
import org.ucb.c5.sequtils.Translate;


/**
 * @author Matthew Sit
 * @author Diego Alcantar
 * @author Nicholas Hsu
 * @author Joe Chen
 */
public class Team3Test {

    private static CompositionToDNA c2d;

    @BeforeClass
    public static void setUpClass() throws Exception {
        c2d = new CompositionToDNA();
        c2d.initiate();
    }

    @Test
    /**
     * Test if it starts with sort codon
     * Joe Chen
     */
    public void testTruismTeam3() throws Exception {
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String peptide    = "MPPLEYMAWYILLIYWMPLE";
        ArrayList<String> list = new ArrayList<>();
        list.add(peptide);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas  = protein2dna.get(0).getCodons();
        String testProteinDna = proteinsDnas[0];
        assertEquals( "ATG", testProteinDna.subSequence(0, 3) );
    }

    /**
     * Test a truism that protein reverse translated can be translated back to original sequence
     * Tests transcript run method
     * @author Nicholas Hsu
     */
    // Reverse translate

    // Forbidden Sequences

    @Test
    public void proteinConsistencyTestNH() throws Exception {
        Translate translate = new Translate();
        translate.initiate();
//        System.out.println("An example of a simple truism test");
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas  = protein2dna.get(0).getCodons();
        for (int i = 0; i < protein.length(); i++) {
            String codon = proteinsDnas[i];
            assertEquals(protein.charAt(i), translate.run(codon).charAt(0));
        }
    }
    /*
    Diego Alcantar
    Truism test to make sure resulting sequence only contains A,T,G, or C
     */
    @Test
    public void testOnlyATGC() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> list = new ArrayList<>();
        list.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas  = protein2dna.get(0).getCodons();
        String testProteinDna = proteinsDnas[0];
        Boolean onlyATGC = true;
        for (int i = 0; i < testProteinDna.length(); i++) {
            if (testProteinDna.charAt(i) != 'A' || testProteinDna.charAt(i) != 'G' || testProteinDna.charAt(i) != 'T' || testProteinDna.charAt(i) != 'C') {
                onlyATGC = false;
                break;
            }
        }
        assertEquals(onlyATGC, true);
    }

    @Test
    /* Diego Alcantar
       Tests to make sure there are no internal stop codons
     */
    public void testNoInternalStopCodons() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> list = new ArrayList<String>();
        list.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas  = protein2dna.get(0).getCodons();
        String testProteinDna = proteinsDnas[0];
        ArrayList<String> stop_codons = new ArrayList<String>(Arrays.asList("TAG", "TAA", "TGA"));
        boolean noStop = true;
        for (String stop_codon: stop_codons) {
            if (testProteinDna.contains(stop_codon)) {
                noStop = false;
                break;
            }
        }
        assertTrue(noStop);
    }


    /**
     * Test and see if there are forbidden sequences
     * Joe Chen
     */
    @Test
    public void testTruismTeam3_2() throws Exception {
        //Add all the forbidden sequences
        List<String> forbidden = new ArrayList<>();
        forbidden.add("AAAAAAAA"); //poly(A)
        forbidden.add("TTTTTTTT"); //poly(T)
        forbidden.add("CCCCCCCC"); //poly(C)
        forbidden.add("GGGGGGGG"); //poly(G)
        forbidden.add("ATATATAT"); //poly(AT)
        forbidden.add("CAATTG");   //MfeI
        forbidden.add("GAATTC");   //EcoRI
        forbidden.add("GGATCC");   //BamHI
        forbidden.add("AGATCT");   //BglII
        forbidden.add("ACTAGT");   //SpeI
        forbidden.add("TCTAGA");   //XbaI
        forbidden.add("GGTCTC");   //BsaI
        forbidden.add("CGTCTC");   //BsmBI
        forbidden.add("CACCTGC");  //AarI
        forbidden.add("CTGCAG");   //PstI
        forbidden.add("CTCGAG");   //XhoI
        forbidden.add("GCGGCCGC"); //NotI
        forbidden.add("AAGCTT");   //HindIII
        // No forbidden sequences
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String peptide = "MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIVDWEKIIKLQSKDGSFLTSPASTAAVFMRTGNKKCLEFLNFVLKKFGNHVPCHYPLDLFERLWAVDTVERLGIDHHFKEEIKDALDYVYSHWDERGIGWARENPIPDIDDTAMGLRILRLHGYNVSSDVLKTFRDENGEFFCFLGQTQRGVTDMLNVNRCSHVAFPGETIMQEAKLCTERYLRNALEDVGAFDKWALKKNIRGEVEYALKYPWHRSMPRLEARSYIEHYGPNDVWLGKTMYMMPYISNLKYLELAKLDFNHVQSLHQKELRDLRRWWKSSGLSELKFTRERVTEIYFSAASFIFEPEFATCRDVYTKISIFTVILDDLYDAHGTLDNLELFSEGVKRWDLSLVDRMPQDMKICFTVLYNTVNEIAVEGRKRQGRDVLGYIRNVLEILLAAHTKEAEWSAARYVPSFDEYIENASVSISLGTLVLISVLFTGEILTDDVLSKIGRGSRFLQLMGLTGRLVNDTKTYEAERGQGEVASAVQCYMKEHPEISEEEALKHVYTVMENALDELNREFVNNRDVPDSCRRLVFETARIMQLFYMEGDGLTLSHEMEIKEHVKNCLFQPVA";
        ArrayList<String> list = new ArrayList<>();
        list.add(peptide);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        StringBuilder dna_seq = new StringBuilder();
        for (String codon : proteinsDnas) {
            dna_seq.append(codon);
        }
        RevComp revcomp = new RevComp();
        revcomp.initiate();
        String seq = dna_seq.toString();
        String rc = revcomp.run(seq);
        String combined = seq + "x" + rc;
        combined = combined.toUpperCase();
        boolean result = true;
        for(String site : forbidden) {
            if(combined.contains(site)) {
                result = false;
            }
        }
        assertTrue(result);
    }
    @Test
    /**
     * Check if the dna sequence encodes for the same protein
     * Joe Chen
     */
    public void testTruismTeam3_3() throws Exception{
        String peptide    = "MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIV";
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        ArrayList<String> list = new ArrayList<>();
        list.add(peptide);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        Translate translate = new Translate();
        translate.initiate();
        StringBuilder check = new StringBuilder();
        for (String codon : proteinsDnas) {
            check.append(translate.run(codon));
        }
        String result = check.toString();
        assertEquals(result, peptide);
    }

    @Test
    /**
     * Testing local GC content
     * Joe Chen
     */
    public void testGCcontent() throws Exception {
        String peptide    = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVEGERDVVGAAMREGALAPGKRIRPMLLLLTARDLGCAVSHDGLLDLACAVEMVHAASLILDDMPCMDDAKLRRGRPTIHSHYGEHVAILAAVALLSKAFGVIADADGLTPLAKNRAVSELSNAIGMQGLVQGQFKDLSEGDKPRSAEAILMTNHFKTSTLFCASMQMASIVANASSEARDCLHRFSLDLGQAFQLLDDLTDGMTDTGKDSNQDAGKSTLVNLLGPRAVEERLRQHLQLASEHLSAACQHGHATQHFIQAWFDKKLAAVS";
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        ArrayList<String> list = new ArrayList<>();
        list.add(peptide);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        StringBuilder check = new StringBuilder();
        for (String codon : proteinsDnas) {
            check.append(codon);
        }
        String seq = check.toString();
        boolean result = true;
        for (int i = 0; i + 20 < seq.length(); i = i + 3) {
            String local = seq.substring(i, i + 21);
            double GCcounts = 0.0;
            for (int j = 0; j < 21; j++) {
                if (local.charAt(j) == 'G' || local.charAt(j) == 'C') {
                    GCcounts += 1;
                }
            }
            double GCratio = GCcounts / 21;
            result = (GCratio >= 0.35) && (GCratio <= 0.65);
            if (result == false) {
                result = false;
            }
        }
        assertTrue(result);
    }

    @Test
    /**
     * Testing local secondary structure not above a certain threshold
     * Joe Chen
     */
    public void testSecondary() throws Exception {
        HairpinCounter counter = new HairpinCounter();
        counter.initiate();
        String peptide    = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVEGERDVVGAAMREGALAPGKRIRPMLLLLTARDLGCAVSHDGLLDLACAVEMVHAASLILDDMPCMDDAKLRRGRPTIHSHYGEHVAILAAVALLSKAFGVIADADGLTPLAKNRAVSELSNAIGMQGLVQGQFKDLSEGDKPRSAEAILMTNHFKTSTLFCASMQMASIVANASSEARDCLHRFSLDLGQAFQLLDDLTDGMTDTGKDSNQDAGKSTLVNLLGPRAVEERLRQHLQLASEHLSAACQHGHATQHFIQAWFDKKLAAVS";
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        ArrayList<String> list = new ArrayList<>();
        list.add(peptide);
        Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        StringBuilder check = new StringBuilder();
        for (String codon : proteinsDnas) {
            check.append(codon);
        }
        String seq = check.toString();
        int threshold = 190000;
        double score = counter.run(seq);
        assertTrue(score < threshold);
    }

    /**
     *
     * @author Nicholas Hsu
     */
    @Test
    public void noForbiddenSequenceTestNH() throws Exception {
        SequenceChecker checker = new SequenceChecker();
        checker.initiate();
//        System.out.println("An example of a simple correctness test");
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVEGERDVVGAAMREGALAPGKRIRPMLLLLTARDLGCAVSHDGLLDLACAVEMVHAASLILDDMPCMDDAKLRRGRPTIHSHYGEHVAILAAVALLSKAFGVIADADGLTPLAKNRAVSELSNAIGMQGLVQGQFKDLSEGDKPRSAEAILMTNHFKTSTLFCASMQMASIVANASSEARDCLHRFSLDLGQAFQLLDDLTDGMTDTGKDSNQDAGKSTLVNLLGPRAVEERLRQHLQLASEHLSAACQHGHATQHFIQAWFDKKLAAVS";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> mRNAs = dna.getmRNAs();
        String[] codons  = mRNAs.get(0).getCodons();
        String cds = "";
        for (int i = 0; i < codons.length; i += 1) {
            cds = cds + codons[i];
        }
        assertTrue(checker.run(cds));
    }
    
    @Test (expected=IllegalArgumentException.class)
    /**
     * Matthew Sit
     *
     * Try many, many random sequences. Some are bound to be tricky.
     * Test all for truism.
     *
     */
    public void testRandom() throws Exception {
        String promoter   = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String peptide = "M";
        RevComp2 rev = new RevComp2();
        rev.initiate();
        SequenceChecker2 seq = new SequenceChecker2();
        seq.initiate();
        Translate2 trans = new Translate2();
        trans.initiate();

        String proteinChoices = "ACDEFGHIKLMNPQRSTVWY";
        int incorrect = 0;

        for (int j = 0; j < 100000; j++) {
            peptide = "M";
            double len = Math.random() * 50 + 1;
            for (int i = 0; i < len; i++) {
                peptide += proteinChoices.charAt((int) (Math.random() * proteinChoices.length()));
            }
            ArrayList<String> list = new ArrayList<>();
            list.add(peptide);

            Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
            Construct dna = c2d.run(comp);
            List<Transcript> protein2dna = dna.getmRNAs();
            String[] proteinsDnas = protein2dna.get(0).getCodons();
            String testProteinDna = proteinsDnas[0];

            String result = "";
            for (String s : proteinsDnas)
                result += s;
            if (!testProteinDna.subSequence(0, 3).equals("ATG"))
                // Start codon still in place
                incorrect++;
            else if (!peptide.equals(trans.run(result)))
                // Truism: reverses back to original
                incorrect++;
            else if (!seq.run(result))
                // Truism: check for forbidden sequences
                incorrect++;
        }
        assertEquals(incorrect, 0);
    }

    class RevComp2 {
        Map<Character, String> baseToRC;

        public void initiate() {
            baseToRC = new HashMap<>();

            baseToRC.put('A', "T");
            baseToRC.put('T', "A");
            baseToRC.put('C', "G");
            baseToRC.put('G', "C");
            baseToRC.put('a', "t");
            baseToRC.put('t', "a");
            baseToRC.put('c', "g");
            baseToRC.put('g', "c");
            baseToRC.put('B', "V");
            baseToRC.put('D', "H");
            baseToRC.put('H', "D");
            baseToRC.put('K', "M");
            baseToRC.put('N', "N");
            baseToRC.put('R', "Y");
            baseToRC.put('S', "S");
            baseToRC.put('V', "B");
            baseToRC.put('W', "W");
            baseToRC.put('Y', "R");
            baseToRC.put('b', "v");
            baseToRC.put('d', "h");
            baseToRC.put('h', "d");
            baseToRC.put('k', "m");
            baseToRC.put('n', "n");
            baseToRC.put('r', "y");
            baseToRC.put('s', "s");
            baseToRC.put('v', "b");
            baseToRC.put('w', "w");
            baseToRC.put('y', "r");
        }

        /**
         * Calculates the reverse complement of a DNA sequence preserving the case
         * of each character
         *
         * @param dna the DNA sequence that should be reverse complemented
         * @return the reverse complement of dna
         */
        public String run(String dna) {
            StringBuilder sb = new StringBuilder();
            for (int i = dna.length() - 1; i >= 0; i--) {
                char achar = dna.charAt(i);
                sb.append(baseToRC.get(achar));
            }

            return sb.toString();
        }
    }

    class Translate2 {

        private Map<String, String> GeneticCode;

        public void initiate() {
            GeneticCode = new HashMap<>();

            GeneticCode.put("TCA", "S");
            GeneticCode.put("TCC", "S");
            GeneticCode.put("TCG", "S");
            GeneticCode.put("TCT", "S");
            GeneticCode.put("TTC", "F");
            GeneticCode.put("TTT", "F");
            GeneticCode.put("TTA", "L");
            GeneticCode.put("TTG", "L");
            GeneticCode.put("TAC", "Y");
            GeneticCode.put("TAT", "Y");
            GeneticCode.put("TAA", "*");
            GeneticCode.put("TAG", "*");
            GeneticCode.put("TGC", "C");
            GeneticCode.put("TGT", "C");
            GeneticCode.put("TGA", "*");
            GeneticCode.put("TGG", "W");
            GeneticCode.put("CTA", "L");
            GeneticCode.put("CTC", "L");
            GeneticCode.put("CTG", "L");
            GeneticCode.put("CTT", "L");
            GeneticCode.put("CCA", "P");
            GeneticCode.put("CCC", "P");
            GeneticCode.put("CCG", "P");
            GeneticCode.put("CCT", "P");
            GeneticCode.put("CAC", "H");
            GeneticCode.put("CAT", "H");
            GeneticCode.put("CAA", "Q");
            GeneticCode.put("CAG", "Q");
            GeneticCode.put("CGA", "R");
            GeneticCode.put("CGC", "R");
            GeneticCode.put("CGG", "R");
            GeneticCode.put("CGT", "R");
            GeneticCode.put("ATA", "I");
            GeneticCode.put("ATC", "I");
            GeneticCode.put("ATT", "I");
            GeneticCode.put("ATG", "M");
            GeneticCode.put("ACA", "T");
            GeneticCode.put("ACC", "T");
            GeneticCode.put("ACG", "T");
            GeneticCode.put("ACT", "T");
            GeneticCode.put("AAC", "N");
            GeneticCode.put("AAT", "N");
            GeneticCode.put("AAA", "K");
            GeneticCode.put("AAG", "K");
            GeneticCode.put("AGC", "S");
            GeneticCode.put("AGT", "S");
            GeneticCode.put("AGA", "R");
            GeneticCode.put("AGG", "R");
            GeneticCode.put("GTA", "V");
            GeneticCode.put("GTC", "V");
            GeneticCode.put("GTG", "V");
            GeneticCode.put("GTT", "V");
            GeneticCode.put("GCA", "A");
            GeneticCode.put("GCC", "A");
            GeneticCode.put("GCG", "A");
            GeneticCode.put("GCT", "A");
            GeneticCode.put("GAC", "D");
            GeneticCode.put("GAT", "D");
            GeneticCode.put("GAA", "E");
            GeneticCode.put("GAG", "E");
            GeneticCode.put("GGA", "G");
            GeneticCode.put("GGC", "G");
            GeneticCode.put("GGG", "G");
            GeneticCode.put("GGT", "G");
        }

        /**
         * Inputs a DNA sequence and outputs the 0 frame encoded protein
         *
         * @param seq the input DNA sequence
         * @return the encoded protein
         */
        public String run(String seq) {
            String dnaseq = seq.toUpperCase();
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < dnaseq.length(); i += 3) {
                String codon = dnaseq.substring(i, i + 3);
                out.append(GeneticCode.get(codon));
            }
            return out.toString();
        }
    }

    class SequenceChecker2 {

        private RevComp2 revcomp;
        private List<String> forbidden;

        public void initiate() {
            revcomp = new RevComp2();
            revcomp.initiate();

            //Populate forbidden sequences
            forbidden = new ArrayList<>();
            forbidden.add("AAAAAAAA"); //poly(A)
            forbidden.add("TTTTTTTT"); //poly(T)
            forbidden.add("CCCCCCCC"); //poly(C)
            forbidden.add("GGGGGGGG"); //poly(G)
            forbidden.add("ATATATAT"); //poly(AT)
            forbidden.add("CAATTG");   //MfeI
            forbidden.add("GAATTC");   //EcoRI
            forbidden.add("GGATCC");   //BamHI
            forbidden.add("AGATCT");   //BglII
            forbidden.add("ACTAGT");   //SpeI
            forbidden.add("TCTAGA");   //XbaI
            forbidden.add("GGTCTC");   //BsaI
            forbidden.add("CGTCTC");   //BsmBI
            forbidden.add("CACCTGC");  //AarI
            forbidden.add("CTGCAG");   //PstI
            forbidden.add("CTCGAG");   //XhoI
            forbidden.add("GCGGCCGC"); //NotI
            forbidden.add("AAGCTT");   //HindIII
        }

        /**
         * Checks a DNA sequence for forbidden Strings
         *
         * @param dnaseq
         * @return true if passes; false if contains a forbidden sequence
         */
        public boolean run(String dnaseq) {
            String rc = revcomp.run(dnaseq);
            String combined = dnaseq + "x" + rc;
            combined = combined.toUpperCase();

            for (String site : forbidden) {
                if (combined.contains(site)) {
                    return false;
                }
            }
            return true;
        }
    }
}
