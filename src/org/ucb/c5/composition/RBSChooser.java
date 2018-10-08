package org.ucb.c5.composition;

import java.util.*;

import org.ucb.c5.composition.model.RBSOption;
import org.ucb.c5.sequtils.CalcEditDistance;
import org.ucb.c5.sequtils.HairpinCounter;
import org.ucb.c5.sequtils.Translate;
import org.ucb.c5.utils.FileUtils;

/**
 * Second generation RBSChooser algorithm
 *
 * Employs a list of genes and their associated ribosome binding sites for
 * highly-expressed proteins in E. coli.
 *
 * @author Samarpita Patra samarpitap
 * @author J. Christopher Anderson
 */
public class RBSChooser {

    private List<RBSOption> rbss;
    private HashMap<String, String[]> ecoliGenes;


    public void initiate() throws Exception {

        //initialize HashMap that stores rbs name and its original cds
        ecoliGenes = new HashMap<>();

        //initialize rbss
        rbss = new ArrayList<>();

        //Read the data file
        String data = FileUtils.readResourceFile("composition/data/coli_genes.txt");

        //Creating the HashMap for coli_genes.txt
        String[] lines_data = data.split("\\r|\\r?\\n");

        //Create and initiate translate
        Translate translate = new Translate();
        translate.initiate();

        for (int i = 0; i < lines_data.length; i++) {

            String line = lines_data[i];
            String[] tabs = line.split("\t");

            //value will have name of ecoli at i = 0
            //cds at i = 6
            ecoliGenes.put(tabs[1], tabs);

        }

        //Read rbs file
        String rbsOptions = FileUtils.readResourceFile("composition/data/rbs_options.txt");

        //Create RBS list


        String[] lines_rbs = rbsOptions.split("\\r|\\r?\\n");
        for (int i = 0; i <lines_rbs.length; i++) {
            String line = lines_rbs[i];
            String[] tabs = line.split("\t");

            String rName = tabs[0];
            String description = "from ecoli: " + ecoliGenes.get(rName)[0];
            String rbs = tabs[1];
            String cds = ecoliGenes.get(rName)[6];
            String first6aas =  translate.run(cds.substring(0,18));

            rbss.add(new RBSOption(rName, description, rbs, cds, first6aas));


        }


    }



    /**
     * Provided a protein of sequence 'peptide', this computes the best ribosome
     * binding site to use from a list of options.
     *
     * It also permits a list of options to exclude.
     *
     * @param cds The DNA sequence, ie ATCG
     * @param ignores The list of RBS's to exclude
     * @return
     * @throws Exception
     */
    public RBSOption run(String cds, Set<RBSOption> ignores) throws Exception {

        //initialize distance calculator
        CalcEditDistance calcdist = new CalcEditDistance();

        RBSOption bestRBS = null;
        int bestDist = 1000;
        double bestHairpin = 100.00;
        HairpinCounter hairpinC = new HairpinCounter();
        hairpinC.initiate();

        for (RBSOption option: rbss) {

            if (ignores.contains(option)){
                continue;
            }

            int dist = calcdist.run(cds, option.getCds());
            String seq = option.getRbs()+cds.substring(0,6);
            double hairpinCount = hairpinC.run(seq);

            if (dist < bestDist && hairpinCount < bestHairpin) {
                bestDist = dist;
                bestHairpin = hairpinCount;
                bestRBS = option;
            }

        }


        return bestRBS;
    }


    public static void main(String[] args) throws Exception {
        //Create an example
////        String peptide = "MLSDTIDTKQQQQQLHVLFIDSYDSFTYNVVRLIEQQTDISPGVNAVHVTTVHSDTFQSMDQLLPLLPLFDAIVVGPGPGNPNNGAQDMGIISELFENANGKLDEVPILGICLGFQAMCLAQGADVSELNTIKHGQVYEMHLNDAARACGLFSGYPDTFKSTRYHSLHVNAEGIDTLLPLCTTEDENGILLMSAQTKNKPWFGVQYHPESCCSELGGLLVSNFLKLSFINNVKTGRWEKKKLNGEFSDILSRLDRTIDRDPIYKVKEKYPKGEDTTYVKQFEVSEDPKLTFEICNIIREEKFVMSSSVISENTGEWSIIALPNSASQVFTHYGAMKKTTVHYWQDSEISYTLLKKCLDGQDSDLPGSLEVIHEDKSQFWITLGKFMENKIIDNHREIPFIGGLVGILGYEIGQYIACGRCNDDENSLVPDAKLVFINNSIVINHKQGKLYCISLDNTFPVALEQSLRDSFVRKKNIKQSLSWPKYLPEEIDFIITMPDKLDYAKAFKKCQDYMHKGDSYEMCLTTQTKVVPSAVIEPWRIFQTLVQRNPAPFSSFFEFKDIIPRQDETPPVLCFLSTSPERFLKWDADTCELRPIKGTVKKGPQMNLAKATRILKTPKEFGENLMILDLIRNDLYELVPDVRVEEFMSVQEYATVYQLVSVVKAHGLTSASKKTRYSGIDVLKHSLPPGSMTGAPKKITVQLLQDKIESKLNKHVNGGARGVYSGVTGYWSVNSNGDWSVNIRCMYSYNGGTSWQLGAGGAITVLSTLDGELEEMYNKLESNLQIFM";
        String peptide = "MSQTVHFQGNPVTVANSIPQ";
        String cds = "atgagccagaccgtgcattttcagggcaacccggtgaccgtggcgaacagcattccgcag".toUpperCase();

        //Initiate the chooser
        RBSChooser chooser = new RBSChooser();
        chooser.initiate();

        //Make the first choice with an empty Set of ignores
        Set<RBSOption> ignores = new HashSet<>();
        RBSOption selected1 = chooser.run(cds, ignores);

        //Add the first selection to the list of things to ignore
        ignores.add(selected1);

        //Choose again with an ignore added
        RBSOption selected2 = chooser.run(cds, ignores);

        //Print out the two options, which should be different
        System.out.println("Protein starts with:");
        System.out.println(cds.substring(0, 6));
        System.out.println();
        System.out.println("Selected1:\n");
        System.out.println(selected1.toString());
        System.out.println();
        System.out.println("Selected2:\n");
        System.out.println(selected2.toString());
    }
}