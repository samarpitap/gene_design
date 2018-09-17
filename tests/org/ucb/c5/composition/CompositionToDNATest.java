package org.ucb.c5.composition;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.ucb.c5.composition.model.Composition;
import org.ucb.c5.composition.model.Host;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.composition.model.Construct;
import org.ucb.c5.sequtils.RevComp;
import org.ucb.c5.sequtils.Translate;

/**
 * @author pawel
 * @author Tong Z.
 */
public class CompositionToDNATest {

    private static CompositionToDNA c2d;
    private static SequenceChecker checker;

    public CompositionToDNATest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        c2d = new CompositionToDNA();
        c2d.initiate();
        checker = new SequenceChecker();
        checker.initiate();
    }

    /**
     * Test a truism about class CompositionToDNA.
     */
    @Test
    public void startCodonTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        String testProteinDna = proteinsDnas[0];
        assertEquals("ATG", testProteinDna.subSequence(0, 3));
    }

    /**
     * Test a truism about class CompositionToDNA.
     */
    @Test
    public void proteinsNumberTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein1 = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        String protein2 = "MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRS";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein1);
        proteins.add(protein2);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        assertEquals(2, protein2dna.size());
    }

    /**
     * Test a truism that protein reverse translated can be translated back to
     * original sequence Tests transcript run method
     *
     * @author Nicholas Hsu
     */
    @Test
    public void proteinConsistencyTestNH() throws Exception {
        Translate translate = new Translate();
        translate.initiate();
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        for (int i = 0; i < protein.length(); i++) {
            String codon = proteinsDnas[i];
            assertEquals(protein.charAt(i), translate.run(codon).charAt(0));
        }
    }

    /**
     * Test an edge case about class CompositionToDNA.
     */
    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKXXXXXXXXAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
    }

    /**
     * Test an edge case about class CompositionToDNA.
     */
    @Test(expected = IllegalArgumentException.class)
    public void noArgumentTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
    }

    /**
     * Perform a correctness test on CompositionToDNA class
     */
    @Test
    public void illigalDnaSeqTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
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
    }

    /**
     * Ensures the DNA produced actually translates back to the correct protein
     *
     * @author Samson Mataraso
     */
    @Test
    public void Translate() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "LLPVEMVRYDIMQMTVCAKKHVYPHLTRDAQLLADRRLDFIRTARQ";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
        StringBuilder buildDNA = new StringBuilder();
        for (String codon : proteinsDnas) {
            buildDNA.append(codon);
        }
        String DNA = buildDNA.toString();
        Translate translator = new Translate();
        translator.initiate();
        assertEquals(translator.run(DNA), "LLPVEMVRYDIMQMTVCAKKHVYPHLTRDAQLLADRRLDFIRTARQ");
    }

    /**
     * Ensures there are no forbidden sequences given a peptide that is likely
     * to generate them on a naive approach
     *
     * @author Samson Mataraso
     */
    @Test
    public void forbiddenSequenceTest() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MKKKKKKKKKCAKKHVHLTRDKKKKKDIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);

        List<Transcript> protein2dna = dna.getmRNAs();
        String[] proteinsDnas = protein2dna.get(0).getCodons();
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
    }

    //Test the runtime (TZ)
    @Test(timeout = 30000)
    public void runTimeTestTZ() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
    }
}
