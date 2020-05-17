import evaluation.experiments.NewYorkBaseGridWorldExperiment;
import evaluation.experiments.NewYorkBaseKMeansExperiment;
import evaluation.experiments.PragueBaseGridExperiment;
import evaluation.experiments.PragueBaseKMeansExperiment;

import java.io.IOException;

/**
 * Main class starting all evaluation experiments
 * */
public class Main {

    public static void main(String[] args) throws IOException {
        PragueBaseGridExperiment.doExperiment();
        PragueBaseKMeansExperiment.doExperiment();
        NewYorkBaseGridWorldExperiment.doExperiment();
        NewYorkBaseKMeansExperiment.doExperiment();
    }
}
