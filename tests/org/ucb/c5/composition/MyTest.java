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
 * Checks the runtime is below 10 sec to run 1000 random samples
 *
 * @author Samarpita Patra samarpitap
 */

public class MyTest {


    public MyTest() {

    }

    //checks whether algorithm can run in sufficient time with 1000 random samples
    @Test
    public void testRuntimeForRandom() throws Exception {
        CompositionToDNA compToDNA = new CompositionToDNA();
        compToDNA.initiate();

        String peptide;
        String promoter = "ttatgacaacttgacggctacatcattcactttttcttcacaa";
        String terminator = "TGCCTGGCGGCAGTAGCGCGGTGGTCCCACCTGACCCCATGCC";
        String proteinChoices = "ACDEFGHIKLMNPQRSTVWY";

        long threshold = 10000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            peptide = "M";
            double len = Math.random() * 50 + 1;
            for (int j = 0; j < len; j++) {
                peptide += proteinChoices.charAt((int) (Math.random() * proteinChoices.length()));
            }
            ArrayList<String> list = new ArrayList<>();
            list.add(peptide);

            Composition comp = new Composition(Host.Ecoli, promoter, list, terminator);
            Construct dna = compToDNA.run(comp);
            List<Transcript> protein2dna = dna.getmRNAs();
            String[] proteinsDnas = protein2dna.get(0).getCodons();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        assertTrue(time < threshold);


    }
}
