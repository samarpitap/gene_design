package org.ucb.c5.composition;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.ucb.c5.composition.model.Composition;
import org.ucb.c5.composition.model.Host;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.composition.model.Construct;
import org.ucb.c5.sequtils.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Meital
 * @author Joana
 * @author Laura
 * @author Bozhie
 */
public class Team4Test {

    private static CompositionToDNA c2d;

    public Team4Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        c2d = new CompositionToDNA();
        c2d.initiate();
    }

    /**
     * Truism test to make sure the returned Composition is DNA and successfully
     * translates the input protein to some DNA sequence (not necessarily the
     * correct one)
     *
     * @author Bozhie
     * @throws Exception
     */
    @Test
    public void testmRNALength() throws Exception {
        String promoter = "GTACCAGTTACGCAGTAGCTAGCATGCTAGCTATGCGTAGTCGATCTAGCTGATCGTAGCTGCTG";
        String terminator = "AGTCGTAGTCGATGCATTTACGATCGCTAGCCCTGTGTGAAAACGTAGCTGTGCTAGTCG";
        String protein = "MKSGKW";
        List<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> mRNA = dna.getmRNAs();
        for (Transcript t : mRNA) {
            String[] codons = t.getCodons();
            assertEquals(codons.length, protein.length());
        }
    }

    /**
     * Checks if avoids forbidden sequences during long polymeric peptide runs
     *
     * @author Bozhie
     * @throws Exception
     */
//    @Test
    public void avoidForbiddenSequence() throws Exception {
        SequenceChecker c = new SequenceChecker();
        c.initiate();
        String promoter = "GTACCAGTTACGCAGTAGCTAGCATGCTAGCTATGCGTAGTCGATCTAGCTGATCGTAGCTGCTG";
        String terminator = "AGTCGTAGTCGATGCATTTACGATCGCTAGCCCTGTGTGAAAACGTAGCTGTGCTAGTCG";
        String Crap = "FFFFFFFFFFFFFFKKKKKKKKKKKKKKKKKERERERERERERGGGGGGGGGGGGGEFEFEFEF";
        List<String> proteins = new ArrayList<>();
        proteins.add(Crap);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> mRNA = dna.getmRNAs();
        for (Transcript t : mRNA) {
            String[] codons = t.getCodons();
            StringBuilder cds = new StringBuilder();
            for (String codon : codons) {
                cds.append(codon);
            }
            assertTrue(c.run(cds.toString()));
        }
    }

    /**
     * Tests if GC content is between 40%-60%
     *
     * @author Bozhie
     * @throws Exception
     */
    @Test
    public void gcContentinRange() throws Exception {
        String promoter = "GTACCAGTTACGCAGTAGCTAGCATGCTAGCTATGCGTAGTCGATCTAGCTGATCGTAGCTGCTG";
        String terminator = "AGTCGTAGTCGATGCATTTACGATCGCTAGCCCTGTGTGAAAACGTAGCTGTGCTAGTCG";
        List<String> proteins = new ArrayList<>();
        String PaIPDS = "MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIVDWEKIIKLQSKDGSFLTSPASTAAVFMRTGNKKCLEFLNFVLKKFGNHVPCHYPLDLFERLWAVDTVERLGIDHHFKEEIKDALDYVYSHWDERGIGWARENPIPDIDDTAMGLRILRLHGYNVSSDVLKTFRDENGEFFCFLGQTQRGVTDMLNVNRCSHVAFPGETIMQEAKLCTERYLRNALEDVGAFDKWALKKNIRGEVEYALKYPWHRSMPRLEARSYIEHYGPNDVWLGKTMYMMPYISNLKYLELAKLDFNHVQSLHQKELRDLRRWWKSSGLSELKFTRERVTEIYFSAASFIFEPEFATCRDVYTKISIFTVILDDLYDAHGTLDNLELFSEGVKRWDLSLVDRMPQDMKICFTVLYNTVNEIAVEGRKRQGRDVLGYIRNVLEILLAAHTKEAEWSAARYVPSFDEYIENASVSISLGTLVLISVLFTGEILTDDVLSKIGRGSRFLQLMGLTGRLVNDTKTYEAERGQGEVASAVQCYMKEHPEISEEEALKHVYTVMENALDELNREFVNNRDVPDSCRRLVFETARIMQLFYMEGDGLTLSHEMEIKEHVKNCLFQPVA";
        String crtE = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVEGERDVVGAAMREGALAPGKRIRPMLLLLTARDLGCAVSHDGLLDLACAVEMVHAASLILDDMPCMDDAKLRRGRPTIHSHYGEHVAILAAVALLSKAFGVIADADGLTPLAKNRAVSELSNAIGMQGLVQGQFKDLSEGDKPRSAEAILMTNHFKTSTLFCASMQMASIVANASSEARDCLHRFSLDLGQAFQLLDDLTDGMTDTGKDSNQDAGKSTLVNLLGPRAVEERLRQHLQLASEHLSAACQHGHATQHFIQAWFDKKLAAVS";
        proteins.add(PaIPDS);
        proteins.add(crtE);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct dna = c2d.run(comp);
        List<Transcript> mRNA = dna.getmRNAs();
        for (Transcript t : mRNA) {
            String[] codons = t.getCodons();
            StringBuilder cds = new StringBuilder();
            for (String codon : codons) {
                cds.append(codon);
            }
            Double gc = 0.0;
            for (int i = 0; i < cds.toString().length(); i++) {
                char curr = cds.toString().charAt(i);
                if (curr == 'G' || curr == 'C') {
                    gc++;
                }
            }
            Double gcPerc = gc / cds.toString().length();
            boolean withinRange = (gcPerc <= 0.6) && (gcPerc >= 0.4);
            assertTrue(withinRange);
        }
    }

    @Test
    public void testRun4() throws Exception {
        assertTrue(true);
    }

    /**
     * Tests if output translates to correct amino acids
     *
     * @author Laura Taylor
     */
    @Test
    public void testAASequence() throws Exception {
        //using promoter, terminator, and first protein from CompositionToDNA main class
        String Promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaaccggcacggaactcgctcgggctggccccggtgcattttttaaatacccgcgagaaatagagttgatcgtcaaaaccaacattgcgaccgacggtggcgataggcatccgggtggtgctcaaaagcagcttcgcctggctgatacgttggtcctcgcgccagcttaagacgctaatccctaactgctggcggaaaagatgtgacagacgcgacggcgacaagcaaacatgctgtgcgacgctggcgatatcaaaattgctgtctgccaggtgatcgctgatgtactgacaagcctcgcgtacccgattatccatcggtggatggagcgactcgttaatcgcttccatgcgccgcagtaacaattgctcaagcagatttatcgccagcagctccgaatagcgcccttccccttgcccggcgttaatgatttgcccaaacaggtcgctgaaatgcggctggtgcgcttcatccgggcgaaagaaccccgtattggcaaatattgacggccagttaagccattcatgccagtaggcgcgcggacgaaagtaaacccactggtgataccattcgcgagcctccggatgacgaccgtagtgatgaatctctcctggcgggaacagcaaaatatcacccggtcggcaaacaaattctcgtccctgatttttcaccaccccctgaccgcgaatggtgagattgagaatataacctttcattcccagcggtcggtcgataaaaaaatcgagataaccgttggcctcaatcggcgttaaacccgccaccagatgggcattaaacgagtatcccggcagcaggggatcattttgcgcttcagccatacttttcatactcccgccattcagagaagaaaccaattgtccatattgcatcagacattgccgtcactgcgtcttttactggctcttctcgctaaccaaaccggtaaccccgcttattaaaagcattctgtaacaaagcgggaccaaagccatgacaaaaacgcgtaacaaaagtgtctataatcacggcagaaaagtccacattgattatttgcacggcgtcacactttgctatgccatagcatttttatccataagattagcggatcctacctgacgctttttatcgcaactctctactgtttctccatacccgtttttttgggctagc";
        String Terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCCGAACTCAGAAGTGAAACGCCGTAGCGCCGATGGTAGTGTGGGGTCTCCCCATGCGAGAGTAGGGAACTGCCAGGCATCAAATAAAACGAAAGGCTCAGTCGAAAGACTGGGCCTT";
        ArrayList<String> protein = new ArrayList<String>();
        protein.add("MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIVDWEKIIKLQSKDGSFLTSPASTAAVFMRTGNKKCLEFLNFVLKKFGNHVPCHYPLDLFERLWAVDTVERLGIDHHFKEEIKDALDYVYSHWDERGIGWARENPIPDIDDTAMGLRILRLHGYNVSSDVLKTFRDENGEFFCFLGQTQRGVTDMLNVNRCSHVAFPGETIMQEAKLCTERYLRNALEDVGAFDKWALKKNIRGEVEYALKYPWHRSMPRLEARSYIEHYGPNDVWLGKTMYMMPYISNLKYLELAKLDFNHVQSLHQKELRDLRRWWKSSGLSELKFTRERVTEIYFSAASFIFEPEFATCRDVYTKISIFTVILDDLYDAHGTLDNLELFSEGVKRWDLSLVDRMPQDMKICFTVLYNTVNEIAVEGRKRQGRDVLGYIRNVLEILLAAHTKEAEWSAARYVPSFDEYIENASVSISLGTLVLISVLFTGEILTDDVLSKIGRGSRFLQLMGLTGRLVNDTKTYEAERGQGEVASAVQCYMKEHPEISEEEALKHVYTVMENALDELNREFVNNRDVPDSCRRLVFETARIMQLFYMEGDGLTLSHEMEIKEHVKNCLFQPVA");
        Composition testComp = new Composition(Host.Ecoli, Promoter, protein, Terminator);
        Construct output = c2d.run(testComp);
        Translate translate = new Translate();
        translate.initiate();
        String[] outputCodons = output.getmRNAs().get(0).getCodons();
        String outputCodonString = "";
        for (String out : outputCodons) {
            outputCodonString += out;
        }
        String peptideString = translate.run(outputCodonString);
        assertEquals(protein.get(0), peptideString);
    }
}
