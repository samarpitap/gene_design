package org.ucb.c5.composition;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.ucb.c5.composition.model.Composition;
import org.ucb.c5.composition.model.Host;
import org.ucb.c5.composition.model.Transcript;
import org.ucb.c5.composition.model.Construct;

import org.ucb.c5.sequtils.Translate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abhinav Koppu
 * @author Bryant Luong
 * @author David Mai
 * @author Manraj Gill
 * @author Katherine Bigelow
 * @author Rudra Mehta
 */
public class Team2Test {

    private static CompositionToDNA c2d;

    public Team2Test() {

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        c2d = new CompositionToDNA();
        c2d.initiate();
    }

    @Test
    /**
     * Truism test about the length of the derived DNA sequence being thrice
     * that of the input protein sequence
     *
     * @author Manraj Gill
     */
    public void testTruism3x() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct construct = c2d.run(comp);

        List<Transcript> mRNAs = construct.getmRNAs();
        String[] dnaSeqCodons = mRNAs.get(0).getCodons();
        String dnaSeq = "";
        for (int i = 0; i < dnaSeqCodons.length; i += 1) {
            dnaSeq = dnaSeq + dnaSeqCodons[i];
        }
//        System.out.println("A truism test checking that len(dnaSeq) = 3 x len(proteinSeq)");
        assertEquals(3, dnaSeq.length() / protein.length());
    }

    @Test
    /**
     * @author David Mai
     *
     * Tests whether outputed sequence returns the original peptide sequence.
     * Promoter, terminator, and protein are taken from CompToDNA
     */
    public void testSimpleConversion() throws Exception {
        String Promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaaccggcacggaactcgctcgggctggccccggtgcattttttaaatacccgcgagaaatagagttgatcgtcaaaaccaacattgcgaccgacggtggcgataggcatccgggtggtgctcaaaagcagcttcgcctggctgatacgttggtcctcgcgccagcttaagacgctaatccctaactgctggcggaaaagatgtgacagacgcgacggcgacaagcaaacatgctgtgcgacgctggcgatatcaaaattgctgtctgccaggtgatcgctgatgtactgacaagcctcgcgtacccgattatccatcggtggatggagcgactcgttaatcgcttccatgcgccgcagtaacaattgctcaagcagatttatcgccagcagctccgaatagcgcccttccccttgcccggcgttaatgatttgcccaaacaggtcgctgaaatgcggctggtgcgcttcatccgggcgaaagaaccccgtattggcaaatattgacggccagttaagccattcatgccagtaggcgcgcggacgaaagtaaacccactggtgataccattcgcgagcctccggatgacgaccgtagtgatgaatctctcctggcgggaacagcaaaatatcacccggtcggcaaacaaattctcgtccctgatttttcaccaccccctgaccgcgaatggtgagattgagaatataacctttcattcccagcggtcggtcgataaaaaaatcgagataaccgttggcctcaatcggcgttaaacccgccaccagatgggcattaaacgagtatcccggcagcaggggatcattttgcgcttcagccatacttttcatactcccgccattcagagaagaaaccaattgtccatattgcatcagacattgccgtcactgcgtcttttactggctcttctcgctaaccaaaccggtaaccccgcttattaaaagcattctgtaacaaagcgggaccaaagccatgacaaaaacgcgtaacaaaagtgtctataatcacggcagaaaagtccacattgattatttgcacggcgtcacactttgctatgccatagcatttttatccataagattagcggatcctacctgacgctttttatcgcaactctctactgtttctccatacccgtttttttgggctagc";
        String Terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCCGAACTCAGAAGTGAAACGCCGTAGCGCCGATGGTAGTGTGGGGTCTCCCCATGCGAGAGTAGGGAACTGCCAGGCATCAAATAAAACGAAAGGCTCAGTCGAAAGACTGGGCCTT";
        ArrayList<String> protein = new ArrayList<String>();
        protein.add("MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIVDWEKIIKLQSKDGSFLTSPASTAAVFMRTGNKKCLEFLNFVLKKFGNHVPCHYPLDLFERLWAVDTVERLGIDHHFKEEIKDALDYVYSHWDERGIGWARENPIPDIDDTAMGLRILRLHGYNVSSDVLKTFRDENGEFFCFLGQTQRGVTDMLNVNRCSHVAFPGETIMQEAKLCTERYLRNALEDVGAFDKWALKKNIRGEVEYALKYPWHRSMPRLEARSYIEHYGPNDVWLGKTMYMMPYISNLKYLELAKLDFNHVQSLHQKELRDLRRWWKSSGLSELKFTRERVTEIYFSAASFIFEPEFATCRDVYTKISIFTVILDDLYDAHGTLDNLELFSEGVKRWDLSLVDRMPQDMKICFTVLYNTVNEIAVEGRKRQGRDVLGYIRNVLEILLAAHTKEAEWSAARYVPSFDEYIENASVSISLGTLVLISVLFTGEILTDDVLSKIGRGSRFLQLMGLTGRLVNDTKTYEAERGQGEVASAVQCYMKEHPEISEEEALKHVYTVMENALDELNREFVNNRDVPDSCRRLVFETARIMQLFYMEGDGLTLSHEMEIKEHVKNCLFQPVA");
        Composition testComp = new Composition(Host.Ecoli, Promoter, protein, Terminator);
        Construct output = c2d.run(testComp);
        Translate translate = new Translate();
        translate.initiate(); // Bugfix by PG - translate object not initiated !
        String[] outputCodons = output.getmRNAs().get(0).getCodons();
        String outputCodonString = "";
        for (String out : outputCodons) {
            outputCodonString += out;
        }
        String peptideString = translate.run(outputCodonString);
        assertEquals(protein.get(0), peptideString);
    }

    @Test
    /**
     * @author David Mai
     *
     */
    public void testCustomConversion() throws Exception {
        String promoter = "ttatgacaacttgacggctac";
        String terminator = "TGCCTGGCGGCAGTAGCGCGG";
        ArrayList<String> protein = new ArrayList<String>();
        protein.add("QLKDGSWGEGFYFLAVDWEKI");
        Composition testComp = new Composition(Host.Ecoli, promoter, protein, terminator);
        Construct output = c2d.run(testComp);
        Translate translate = new Translate();
        translate.initiate(); // Bugfix by PG - translate object not initiated !
        String[] outputCodons = output.getmRNAs().get(0).getCodons();
        String outputCodonString = "";
        for (String out : outputCodons) {
            outputCodonString += out;
        }
        String peptideString = translate.run(outputCodonString);
        assertEquals(protein.get(0), peptideString);
    }

    @Test
    /**
     * @author David Mai
     *
     * Test to see whether there are forbidden sequences in returned sequence.
     */
    public void testForbiddenSequences() throws Exception {
        SequenceChecker seq_police = new SequenceChecker();
        seq_police.initiate();
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVEGERDVVGAAMREGALAPGKRIRPMLLLLTARDLGCAVSHDGLLDLAC";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition testcomp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct output = c2d.run(testcomp);
        List<Transcript> mRNAs = output.getmRNAs();
        String[] codons = mRNAs.get(0).getCodons();
        String cds = "";
        for (int i = 0; i < codons.length; i += 1) {
            cds = cds + codons[i];
        }
        assertTrue(seq_police.run(cds));
    }

    @Test
    /**
     * Tests whether the returned sequence contains any forbidden sequences.
     *
     * @author Katherine Bigelow, edited: Samson Mataraso
     */
    public void testForbidden() throws Exception {
        String promoter = "c";
        String terminator = "g";
        String protein = "MYPFIRTARMTVCAKKHVHLTRDAAEQLLADIDRRLDQLLPVE";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct construct = c2d.run(comp);

        List<Transcript> mRNAs = construct.getmRNAs();
        String[] dnaSeqCodons = mRNAs.get(0).getCodons();
        String dnaSeq = "";
        for (int i = 0; i < dnaSeqCodons.length; i += 1) {
            dnaSeq = dnaSeq + dnaSeqCodons[i];
        }

        assertFalse(dnaSeq.contains("AAAAAAAA")
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
                || dnaSeq.contains("AAGCTT")
        );
    }

    @Test
    /**
     * Reverse-translates a specific protein sequence. Then checks that for
     * every window of 60 amino acids, the local GC content is less than 80% and
     * greater than 20%.
     *
     * @author Rudra Mehta
     */
    public void testLocalGC() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String protein = "MTSIFHFAIIFMLILQIRIQLSEESEFLVDRSKNGLIHVPKDLSQKTTILNISQNYISELWTSDILSLSKLRILIISHNRIQYLDISVFKFNQELEYLDLSHNKLVKISCHPTVNLKHLDLSFNAFDALPICKEFGNMSQLKFLGLSTTHLEKSSVLPIAHLNISKVLLVLGETYGEKEDPEGLQDFNTESLHIVFPTNKEFHFILDVSVKTVANLELSNIKCVLEDNKCSYFLSILAKLQTNPKLSNLTLNNIETTWNSFIRILQLVWHTTVWYFSISNVKLQGQLDFRDFDYSGTSLKALSIHQVVSDVFGFPQSYIYEIFSNMNIKNFTVSGTRMVHMLCPSKISPFLHLDFSNNLLTDTVFENCGHLTELETLILQMNQLKELSKIAEMTTQMKSLQQLDISQNSVSYDEKKGDCSWTKSLLSLNMSSNILTDTIFRCLPPRIKVLDLHSNKIKSIPKQVVKLEALQELNVAFNSLTDLPGCGSFSSLSVLIIDHNSVSHPSADFFQSCQKMRSIKAGDNPFQCTCELGEFVKNIDQVSSEVLEGWPDSYKCDYPESYRGTLLKDFHMSELSCNITLLIVTIVATMLVLAVTVTSLCSYLDLPWYLRMVCQWTQTRRRARNIPLEELQRNLQFHAFISYSGHDSFWVKNELLPNLEKEGMQICLHERNFVPGKSIVENIITCIEKSYKSIFVLSPNFVQSEWCHYELYFAHHNLFHLELKNSFITNPKEEDVLRDWNSGSPSYCNWTGVTCGGREIIGLNLSGLGLTGSISPSIGRFNNLIHIDLSSNRLVGPIPTTLSNLSSSLESLHLFSNLLSGDIPSQLGSLVNLKSLKLGDNELNGTIPETFGNLVNLQMLALASCRLTGLIPSRFGRLVQLQTLILQDNELEGPIPAEIGNCTSLALFAAAFNRLNGSLPAELNRLKNLQTLNLGDNSFSGEIPSQLGDLVSIQYLNLIGNQLQGLIPKRLTELANLQTLDLSSNNLTGVIHEEFWRMNQLEFLVLAKNRLSGSLPKTICSNNTSLKQLFLSETQLSGEIPAEISNCQSLKLLDLSNNTLTGQIPDSLFQLVELTNLYLNNNSLEGTLSSSISNLTNLQEFTLYHNNLEGKVPKEIGFLGKLEIMYLYENRFSGEMPVEIGNCTRLQEIDWYGNRLSGEIPSSIGRLKDLTRLHLRENELVGNIPASLGNCHQMTVIDLADNQLSGSIPSSFGFLTALELFMIYNNSLQGNLPDSLINLKNLTRINFSSNKFNGSISPLCGSSSYLSFDVTENGFEGDIPLELGKSTNLDRLRLGKNQFTGRIPRTFGKISELSLLDISRNSLSGIIPVELGLCKKLTHIDLNNNYLSGVIPTWLGKLPLLGELKLSSNKFVGSLPTEIFSLTNILTLFLDGNSLNGSIPQEIGNLQALNALNLEENQLSGPLPSTIGKLSKLFELRLSRNALTGEIPVEIGQLQDLQSALDLSYNNFTGRIPSTISTLPKLESLDLSHNQLVGEVPGQIGDMKSLGYLNLSYNNLEGKLKKQFSRWQADAFVGNAGLCGSPLSHCNRAGSKNQRSLSPKTVVIISAISSLAAIALMVLVIILFFKQNHDLFKKVRGGNSAFSSNSSSSQAPLFSNGGAKSDIKWDDIMEATHYLNEEFMIGSGGSGKVYKAELKNGETIAVKKILWKDDLMSNKSFNREVKTLGTIRHRHLVKLMGYCSSKADGLNLLIYEYMANGSVWDWLHANENTKKKEVLGWETRLKIALGLAQGVEYLHYDCVPPIVHRDIKSSNVLLDSNIEAHLGDFGLAKILTGNYDTNTESNTMFAGSYGYIAPEYAYSLKATEKSDVYSMGIVLMEIVTGKMPTEAMFDEETDMVRWVETVLDTPPGSEAREKLIDSELKSLLPCEEEAAYQVLEIALQCTKSYPQERPSSRQASEYLLNVFNNRAASYREMQTDTDKMRGVGWQMLSLSLGLVLAILNKVAPQACPAQCSCSGSTVDCHGLALRSVPRNIPRNTERLDLNGNNITRITKTDFAGLRHLRVLQLMENKISTIERGAFQDLKELERLRLNRNHLQLFPELLFLGTAKLYRLDLSENQIQAIPRKAFRGAVDIKNLQLDYNQISCIEDGAFRALRDLEVLTLNNNNITRLSVASFNHMPKLRTFRLHSNNLYCDCHLAWLSDWLRQRPRVGLYTQCMGPSHLRGHNVAEVQKREFVCSGHQSFMAPSCSVLHCPAACTCSNNIVDCRGKGLTEIPTNLPETITEIRLEQNTIKVIPPGAFSPYKKLRRIDLSNNQISELAPDAFQGLRSLNSLVLYGNKITELPKSLFEGLFSLQLLLLNANKINCLRVDAFQDLHNLNLLSLYDNKLQTIAKGTFSPLRAIQTMHLAQNPFICDCHLKWLADYLHTNPIETSGARCTSPRRLANKRIGQIKSKKFRCSAKEQYFIPGTEDYRSKLSGDCFADLACPEKCRCEGTTVDCSNQKLNKIPEHIPQYTAELRLNNNEFTVLEATGIFKKLPQLRKINFSNNKITDIEEGAFEGASGVNEILLTSNRLENVQHKMFKGLESLKTLMLRSNRITCVGNDSFIGLSSVRLLSLYDNQITTVAPGAFDTLHSLSTLNLLANPFNCNCYLAWLGEWLRKKRIVTGNPRCQKPYFLKEIPIQDVAIQDFTCDDGNDDNSCSPLSRCPTECTCLDTVVRCSNKGLKVLPKGIPRDVTELYLDGNQFTLVPKELSNYKHLTLIDLSNNRISTLSNQSFSNMTQLLTLILSYNRLRCIPPRTFDGLKSLRLLSLHGNDISVVPEGAFNDLSALSHLAIGANPLYCDCNMQWLSDWVKSEYKEPGIARCAGPGEMADKLLLTTPSKKFTCQGPVDVNILAKCNPCLSNPCKNDGTCNSDPVDFYRCTCPYGFKGQDCDVPIHACISNPCKHGGTCHLKEGEEDGFWCICADGFEGENCEVNVDDCEDNDCENNSTCVDGINNYTCLCPPEYTGELCEEKLDFCAQDLNPCQHDSKCILTPKGFK";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(protein);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);
        Construct construct = c2d.run(comp);

        List<Transcript> mRNAs = construct.getmRNAs();
        String[] dnaSeqCodons = mRNAs.get(0).getCodons();
        String dnaSeq = "";
        for (int i = 0; i < dnaSeqCodons.length; i += 1) {
            dnaSeq = dnaSeq + dnaSeqCodons[i];
        }

        int window_size = 60;
        for (int window = 0; window < dnaSeq.length(); window += window_size) {
            String window_bases = dnaSeq.substring(window, window + window_size);
            double window_gc_count = 0;
            for (int i = 0; i < window_bases.length(); i++) {
                if (window_bases.charAt(i) == 'G' || window_bases.charAt(i) == 'C') {
                    window_gc_count++;
                }
            }
            double window_gc_perc = window_gc_count / window_size;
            assertTrue(window_gc_perc < .8);
            assertTrue(window_gc_perc > .2);
        }
    }

    @Test
    /**
     * Tests whether the output contains only A,C,G and T.
     *
     * @author Abhinav Koppu
     */
    public void testACTG() throws Exception {
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaaccggcacggaactcgctcgggctggccccggtgcattttttaaatacccgcgagaaatagagttgatcgtcaaaaccaacattgcgaccgacggtggcgataggcatccgggtggtgctcaaaagcagcttcgcctggctgatacgttggtcctcgcgccagcttaagacgctaatccctaactgctggcggaaaagatgtgacagacgcgacggcgacaagcaaacatgctgtgcgacgctggcgatatcaaaattgctgtctgccaggtgatcgctgatgtactgacaagcctcgcgtacccgattatccatcggtggatggagcgactcgttaatcgcttccatgcgccgcagtaacaattgctcaagcagatttatcgccagcagctccgaatagcgcccttccccttgcccggcgttaatgatttgcccaaacaggtcgctgaaatgcggctggtgcgcttcatccgggcgaaagaaccccgtattggcaaatattgacggccagttaagccattcatgccagtaggcgcgcggacgaaagtaaacccactggtgataccattcgcgagcctccggatgacgaccgtagtgatgaatctctcctggcgggaacagcaaaatatcacccggtcggcaaacaaattctcgtccctgatttttcaccaccccctgaccgcgaatggtgagattgagaatataacctttcattcccagcggtcggtcgataaaaaaatcgagataaccgttggcctcaatcggcgttaaacccgccaccagatgggcattaaacgagtatcccggcagcaggggatcattttgcgcttcagccatacttttcatactcccgccattcagagaagaaaccaattgtccatattgcatcagacattgccgtcactgcgtcttttactggctcttctcgctaaccaaaccggtaaccccgcttattaaaagcattctgtaacaaagcgggaccaaagccatgacaaaaacgcgtaacaaaagtgtctataatcacggcagaaaagtccacattgattatttgcacggcgtcacactttgctatgccatagcatttttatccataagattagcggatcctacctgacgctttttatcgcaactctctactgtttctccatacccgtttttttgggctagc";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCCGAACTCAGAAGTGAAACGCCGTAGCGCCGATGGTAGTGTGGGGTCTCCCCATGCGAGAGTAGGGAACTGCCAGGCATCAAATAAAACGAAAGGCTCAGTCGAAAGACTGGGCCTT";
        String PaIPDS = "MALLSSSLSSQIPTGSHPLTHTQCIPHFSTTINAGISAGKPRSFYLRWGKGSNKIIACVGEGTTSLPYQSAEKTDSLSAPTLVKREFPPGFWKDHVIDSLTSSHKVSAAEEKRMETLISEIKNIFRSMGYGETNPSAYDTAWVARIPAVDGSEHPEFPETLEWILQNQLKDGSWGEGFYFLAYDRILATLACIITLTLWRTGETQIRKGIEFFKTQAGKIEDEADSHRPSGFEIVFPAMLKEAKVLGLDLPYELPFIKQIIEKREAKLERLPTNILYALPTTLLYSLEGLQEIVDWEKIIKLQSKDGSFLTSPASTAAVFMRTGNKKCLEFLNFVLKKFGNHVPCHYPLDLFERLWAVDTVERLGIDHHFKEEIKDALDYVYSHWDERGIGWARENPIPDIDDTAMGLRILRLHGYNVSSDVLKTFRDENGEFFCFLGQTQRGVTDMLNVNRCSHVAFPGETIMQEAKLCTERYLRNALEDVGAFDKWALKKNIRGEVEYALKYPWHRSMPRLEARSYIEHYGPNDVWLGKTMYMMPYISNLKYLELAKLDFNHVQSLHQKELRDLRRWWKSSGLSELKFTRERVTEIYFSAASFIFEPEFATCRDVYTKISIFTVILDDLYDAHGTLDNLELFSEGVKRWDLSLVDRMPQDMKICFTVLYNTVNEIAVEGRKRQGRDVLGYIRNVLEILLAAHTKEAEWSAARYVPSFDEYIENASVSISLGTLVLISVLFTGEILTDDVLSKIGRGSRFLQLMGLTGRLVNDTKTYEAERGQGEVASAVQCYMKEHPEISEEEALKHVYTVMENALDELNREFVNNRDVPDSCRRLVFETARIMQLFYMEGDGLTLSHEMEIKEHVKNCLFQPVA";
        ArrayList<String> proteins = new ArrayList<>();
        proteins.add(PaIPDS);
        Composition comp = new Composition(Host.Ecoli, promoter, proteins, terminator);

        //Instantiate and run this algorithm
        CompositionToDNA c2d = new CompositionToDNA();
        c2d.initiate();
        Construct dna = c2d.run(comp);

        //Compile the Construct to a sequence
        List<Transcript> mRNAs = dna.getmRNAs();
        String[] dnaSeqCodons = mRNAs.get(0).getCodons();
        String dnaSeq = "";
        for (int i = 0; i < dnaSeqCodons.length; i += 1) {
            dnaSeq = dnaSeq + dnaSeqCodons[i];
        }
        assertFalse(!(dnaSeq.contains("A") || dnaSeq.contains("C") || dnaSeq.contains("G") || dnaSeq.contains("T")));
    }
}
