package org.ucb.c5.composition;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.ucb.c5.composition.model.Composition;
import org.ucb.c5.composition.model.Host;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.composition.model.Construct;
import org.ucb.c5.sequtils.RevComp;
import org.ucb.c5.sequtils.Translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Siddharth Gampa
 * @author Lucas Waldburger
 * @author Tong Zhang
 * @author Jerry Li (jtli)
 * @author Tristan Wasley
 */
public class Team1Test {

    private static CompositionToDNA c2d;

    public Team1Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        c2d = new CompositionToDNA();
        c2d.initiate();
    }

    private boolean forbiddenSequenceChecker(String dnaSeq) {
        return (dnaSeq.contains("AAAAAAAA")
                || dnaSeq.contains("TTTTTTTT")
                || dnaSeq.contains("CCCCCCCC")
                || dnaSeq.contains("GGGGGGGG")
                || dnaSeq.contains("ATATATAT")
                || dnaSeq.contains("CAATTG")
                || dnaSeq.contains("GAATTC")
                || dnaSeq.contains("GGATCC")
                || dnaSeq.contains("AGATCT")
                || dnaSeq.contains("ACTAGT")
                || dnaSeq.contains("TCTAGA")
                || dnaSeq.contains("GGTCTC")
                || dnaSeq.contains("CGTCTC")
                || dnaSeq.contains("CACCTGC")
                || dnaSeq.contains("CTGCAG")
                || dnaSeq.contains("CTCGAG")
                || dnaSeq.contains("GCGGCCGC")
                || dnaSeq.contains("AAGCTT"));
    }

    @Test
    public void testTruismTZ() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] codons = protein2dna.get(0).getCodons();
        assertEquals(protein.length(), codons.length);
    }

    //Test the GC content of a DNA sequence (globally)
    @Test
    public void testGlobalGCcontentTZ() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        Double GCcontent = 0.0;
        for (String codon : proteinsDnas) {
            for (int i = 0; i < 3; i++) {
                if (codon.charAt(i) == 'G' || codon.charAt(i) == 'C') {
                    GCcontent += 1;
                }
            }
        }
        Double GCfreq = GCcontent / (protein.length() * 3);
        assertTrue(GCfreq > 0.4);
        assertTrue(GCfreq < 0.6);
    }

    @Test
    public void testForbiddenSeqTZ() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein1 = "MQLEFGSRSTSSRGLRLLQLEKL";
        String protein2 = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        String protein3 = "MIIRDENYFTDKYELTRTHSEVLEAVKVVKPGKTLDLGCGNGRNSLYLAANGYDVDAWDKNAMSIANVERIKSIENLDNLHTRVVDLNNLTFDRQYDFILSTVVLMFLEAKTIPGLIANMQRCTKPGGYNLIVAAMDTADYPCTVGFPFAFKEGELRRYYEGWERVKYNEDVGELHRTDANGNRIKLRFATMLARKK";
        String protein4 = "MQVDLLGSAQSAHALHLFHQHSPLVHCMTNDVVQTFTANTLLALGASPAMVIETEEASQFAAIASALLINVGTLTQPRAQAMRAAVEQAKSSQTPWTLDPVAVGALDYRRHFCHELLSFKPAAIRGNASEIMALAGIANGGRGVDTTDAAANAIPAAQTLARETGAIVVVTGEMDYVTDGHRIIGIHGGDPLMTKVVGTGCALSAVVAACCALPGDTLENVASACHWMKQAGERAVARSEGPGSFVPHFLDALWQLTQEVQA";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        proteins.add(protein3);
        proteins.add(protein4);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        for (int i = 0; i < protein2dna.size(); i++) {
            String[] proteinsDnas = protein2dna.get(i).getCodons();
            String testProteinDna = "";
            for (String codon : proteinsDnas) {
                testProteinDna += codon;
            }

            RevComp revcomp; // Define and create an object to get a complementary seq
            revcomp = new RevComp();
            revcomp.initiate();

            String rc = revcomp.run(testProteinDna);
            String combined = testProteinDna + "x" + rc;
            combined = combined.toUpperCase();

            assertFalse(combined.contains("AAAAAAAA"));
            assertFalse(combined.contains("TTTTTTTT"));
            assertFalse(combined.contains("CCCCCCCC"));
            assertFalse(combined.contains("GGGGGGGG"));
            assertFalse(combined.contains("ATATATAT"));
            assertFalse(combined.contains("CAATTG"));
            assertFalse(combined.contains("GAATTC"));
            assertFalse(combined.contains("GGATCC"));
            assertFalse(combined.contains("AGATCT"));
            assertFalse(combined.contains("ACTAGT"));
            assertFalse(combined.contains("TCTAGA"));
            assertFalse(combined.contains("GGTCTC"));
            assertFalse(combined.contains("CGTCTC"));
            assertFalse(combined.contains("CACCTGC"));
            assertFalse(combined.contains("CTGCAG"));
            assertFalse(combined.contains("CTCGAG"));
            assertFalse(combined.contains("GCGGCCGC"));
            assertFalse(combined.contains("AAGCTT"));
        }
    }

    //Test if different construct can be translated back to the original protein sequence
    @Test
    public void proteinTranslationTestTZ() throws Exception {
        Translate translate = new Translate();
        translate.initiate();
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein1 = "MRYIRQLCCVSLLCLSGSAVAANVRLQVEGLSGQLEKNVRAQLSTIESDEVTPDRRFRARVDDAIREGLKALGYYQPTIEFDLRPPPKKGRQVLIAKVTPGVPVLIGGTDVVLRGGARTDKDYLKLLDTRPAIGTVLNQGDYENFKKSLTSIALRKGYFDSEFTKAQLGIALGLHKAFWDIDYNSGERYRFGHVTFEGSQIRDEYLQNLVPFKEGDEYESKDLAELNRRLSATGWFNSVVVAPQFDKARETKVLPLTGVVSPRTENTIETGVGYSTDVGPRVKATWKKPWMNSYGHSLTTSTSISAPEQTLDFSYKMPLLKNPLEQYYLVQGGFKRTDLNDTESDSTTLVASRYWDLSSGWQRAINLRWSLDHFTQGEITNTTMLFYPGVMISRTRSRGGLMPTWGDSQRYSIDYSNTAWGSDVDFSVFQAQNVWIRTLYDRHRFVTRGTLGWIETGDFDKVPPDLRFFAGGDRSIRGYKYKSIAPKYANGDLKGASKLITGSLEYQYNVTGKWWGAVFVDSGEAVSDIRRSDFKTGTGVGVRWESPVGPIKLDFAVPVADKDEHGLQFYIGLGPEL";
        String protein2 = "MQILFNDQAMQCAAGQTVHELLEQLDQRQAGAALAINQQIVPREQWAQHIVQDGDQILLFQVIAGG";
        String protein3 = "MIIRDENYFTDKYELTRTHSEVLEAVKVVKPGKTLDLGCGNGRNSLYLAANGYDVDAWDKNAMSIANVERIKSIENLDNLHTRVVDLNNLTFDRQYDFILSTVVLMFLEAKTIPGLIANMQRCTKPGGYNLIVAAMDTADYPCTVGFPFAFKEGELRRYYEGWERVKYNEDVGELHRTDANGNRIKLRFATMLARKK";
        String protein4 = "MQVDLLGSAQSAHALHLFHQHSPLVHCMTNDVVQTFTANTLLALGASPAMVIETEEASQFAAIASALLINVGTLTQPRAQAMRAAVEQAKSSQTPWTLDPVAVGALDYRRHFCHELLSFKPAAIRGNASEIMALAGIANGGRGVDTTDAAANAIPAAQTLARETGAIVVVTGEMDYVTDGHRIIGIHGGDPLMTKVVGTGCALSAVVAACCALPGDTLENVASACHWMKQAGERAVARSEGPGSFVPHFLDALWQLTQEVQA";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        proteins.add(protein3);
        proteins.add(protein4);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        for (int i = 0; i < protein2dna.size(); i++) {
            String[] proteinsDnas = protein2dna.get(i).getCodons();
            for (int j = 0; j < proteinsDnas.length; j++) {
                String codon = proteinsDnas[i];
                assertEquals(protein2dna.get(i).getPeptide().charAt(i), translate.run(codon).charAt(0));
            }
        }
    }

    @Test
    /**
     * this tests if running the construct more than once returns the same
     * output
     */
    public void testNonRandomnessSG() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        List<String[]> dnas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Construct dna = c2d.run(comp);
            List<Transcript> protein2dna = dna.getmRNAs();
            dnas.add(protein2dna.get(0).getCodons());
        }

        assertTrue(Arrays.equals(dnas.get(0), dnas.get(1)));

    }

    /**
     * This tests if gc content is between the recommended 40 to 60%. Also tests
     * if the returned string is a valid dna(i.e. only ATCG)
     *
     * @author Jerry Li (jtli)
     */
//    @Test
    public void testGCContentJL() throws Exception {
        String promoter = "ttt";
        String terminator = "TGC";
        String protein1 = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";
        String protein2 = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKKKKKK";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        //Instantiate and run this algorithm
        Construct dna = c2d.run(comp);
        String seq = dna.toSeq();

        double ACount = 0;
        double CCount = 0;
        double GCount = 0;
        double TCount = 0;
        for (int i = 0; i < seq.length(); i++) {
            char BP = seq.charAt(i);
            if (BP == 'A' || BP == 'a') {
                ACount++;
            } else if (BP == 'C' || BP == 'c') {
                CCount++;
            } else if (BP == 'G' || BP == 'g') {
                GCount++;
            } else if (BP == 'T' || BP == 't') {
                TCount++;
            } else {
                throw new IllegalArgumentException("passed in a non-base pair");
            }
        }
        double gcPercentage = (CCount + GCount) / seq.length();
        assertTrue(gcPercentage >= 0.4);
        assertTrue(gcPercentage <= 0.6);

    }

    /**
     * This tests if the output string increases by three characters if an extra
     * amino acid is added to the protein chain, as it should
     *
     * @author Jerry Li (jtli)
     */
//    @Test
    public void testIncBy3JL() throws Exception {
        String promoter = "ttt";
        String terminator = "TGC";
        String protein1 = "KKKSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSFFF";
        String protein2 = "FFNSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKKKKKK";
        String protein3 = "FFFSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKKKKFFN";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        //Instantiate and run this algorithm
        Construct dna = c2d.run(comp);
        String seq = dna.toSeq();

        ArrayList<String> proteins2 = new ArrayList<>();
        proteins2.add(protein1);
        proteins2.add(protein3);
        Composition comp2 = new Composition(Host.Ecoli, promoter, proteins2, terminator);

        //Instantiate and run this algorithm
        Construct dna2 = c2d.run(comp2);
        //Compile the Construct to a sequence
        String seq2 = dna2.toSeq();

        assertTrue(seq2.length() - seq.length() == 3);

    }

    /**
     * This tests if the entire DNA sequence chain produces a forbidden
     * sequence. Many of the proteins try to create a forbidden sequence with
     * the rbs options given.
     *
     * @author Jerry Li (jtli), edited to account for differing implementations
     * of sequence checker: Samson Mataraso
     */
//    @Test
    public void testForbiddenSeqJL() throws Exception {
        SequenceChecker2 checker = new SequenceChecker2();
        checker.initiate();
        String promoter = "ttt";
        String terminator = "TGC";
        //protein 1-3 are just edge cases for my particular code
        String protein1 = "KKKSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSFFF";
        String protein2 = "FFNSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKKKKKK";
        String protein3 = "FFFSSSSSSSSSSSSSSSSSSSSSSSSKKKKKKKKKKSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSKKKKFFN";
        //protein 4 is an attempt to bait a forbidden sequence with the rbs ydja, which could yield forbidden sequence CAATTG ((first six amino acid has only 1 letter off with the cds for the rbs option ydja))
        String protein4 = "LDALELKFNSSFNKSNFKSNFKSNKFNKSNKFNKSNKFNSKNFKNSKFNK";
        //attempt to bait forbidden seq GAATTC with the rbs deoc. (has only 1 letter off)
        String protein5 = "FTDLKALNQKVAASSSFKNQQLLKKKNNNFFF";
        //attempt to bait forbidden seq GAATTC with the rbs pal.(has only 1 letter off)
        String protein6 = "FQLNKVLKNQLKNQFDTVSSSSSSSSSKKKKKKKKSKSKKSKSKSKSKSKSKSKKSKSKSKKSKSKSNN";
        //attempt to bait forbidden seq AGATCT with the rbs csra.(has only 1 letter off)
        String protein7 = "SLILTRFKFKFKFKFKFKFKFKFKFKKFKFKFKFKFKKFKFKFKSSSS";
        //attempt to bait forbidden seq AGATCT with the rbs hfq.(has only 1 letter off)
        String protein8 = "SAKGQSLILTRKFKFKFKKFKFKFKSNSNSNSN";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        proteins.add(protein5);
        proteins.add(protein7);
        proteins.add(protein8);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        //Instantiate and run this algorithm
        Construct dna = c2d.run(comp);
        String seq = dna.toSeq();

        ArrayList<String> proteins2 = new ArrayList<>();
        //protein7 added many times to increase likelihood of choosing an rbs with a forbidden sequence with it
        proteins2.add(protein7);
        proteins2.add(protein7);
        proteins2.add(protein7);
        proteins2.add(protein7);
        proteins2.add(protein3);
        proteins2.add(protein4);
        proteins2.add(protein5);
        proteins2.add(protein6);
        Composition comp2 = new Composition(Host.Ecoli, promoter, proteins2, terminator);

        //Instantiate and run this algorithm
        Construct dna2 = c2d.run(comp2);
        String seq2 = dna2.toSeq();
        assertFalse(forbiddenSequenceChecker(seq));
        assertFalse(forbiddenSequenceChecker(seq2));

    }

    /**
     * This tests if the program works with both upper and lower case protein
     * sequences. It will have two identical inputs, besides case, and see if
     * the resulting outputs are equal. Also a good test for consistency. Also
     * happens to test if unavoidable bad gc content will not throw an error
     *
     * @author Jerry Li (jtli)
     */
//    @Test
    public void testUpperAndLowerCaseJL() throws Exception {
        String promoter = "ttt";
        String terminator = "TGC";
        String protein1 = "WWWWSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSFFF";
        //all k's, so bad gc content is unavoidable, but still shouldn't throw an error
        String protein2 = "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkKKKKKKkkkkkkkkkkkkkkkkkkkkkkkkkkKKKKKKKKKKKKKKKkkkkkkkkkkkkkkkkkkkkkkk";
        String protein3 = "ldalelkfnsvwwwwwwwwwwwwwqfly";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        proteins.add(protein3);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        //Instantiate and run this algorithm
        Construct dna = c2d.run(comp);
        String seq = dna.toSeq();

        protein1 = protein1.toLowerCase();
        protein2 = protein2.toLowerCase();
        protein3 = protein3.toUpperCase();
        promoter = promoter.toUpperCase();
        terminator = terminator.toLowerCase();

        ArrayList<String> proteins2 = new ArrayList<>();
        proteins2.add(protein1);
        proteins2.add(protein2);
        proteins2.add(protein3);
        Composition comp2 = new Composition(Host.Ecoli, promoter, proteins2, terminator);

        Construct dna2 = c2d.run(comp2);
        String seq2 = dna2.toSeq();

        //makes both sequences uppercase, since it isn't wrong if the cases are different if passed in different cases
        seq = seq.toUpperCase();
        seq2 = seq2.toUpperCase();
        assertTrue(seq2.equals(seq));

    }

    private class SequenceChecker2 {

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

    private class RevComp2 {

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
         * Calculates the reverse complement of a DNA sequence preserving the
         * case of each character
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

    @Test
    public void testTruismLW() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] codons = protein2dna.get(0).getCodons();
        assertEquals("ATG", codons[0]);
    }

    @Test
    public void testCodonsLW() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] codons = protein2dna.get(0).getCodons();

        int count = 0;
        for (String codon : codons) {
            if (codon.length() != 3) {
                count++;
            }
        }
        assertEquals(0, count);
    }

    @Test
    public void testHiddenStopCodonsTW() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        Construct dna = c2d.run(comp);
        List<Transcript> protein2dna = dna.getmRNAs();
        String[] codons = protein2dna.get(0).getCodons();
        for (int i = 0; i < codons.length - 1; i++) {
            if (i == 0) {
                continue;
            } else {
                if (codons[i].equals("TAG") || codons[i].equals("TAA") || codons[i].equals("TGA")) {
                    throw new Exception("Hidden Stop Codon Found");
                }
            }
        }
    }

}
