package evaluation.experiments;

import domain.parameterestimation.NewYorkDataSetReader;
import domain.utils.Utils;
import evaluation.Experiment;
import evaluation.Simulation;

import java.io.IOException;


/**
 * Experiment using New York historical taxi trip computing simulation results of Base Model Agent and Charging
 * Recommender Model Agent with K-Means environment.
 * */
public class NewYorkBaseKMeansExperiment {

    public static void doExperiment() throws IOException {
        Simulation simulation = new Simulation();

        Utils.setUtilsParameters(8*60, 12*60, 20, new NewYorkDataSetReader(),
                "new_york.fst", "new_york_chargingstations.json",
                "new_york", "kmeans", 100);
        simulation.initSimulation();

        for (int startingStateOfCharge = 10; startingStateOfCharge <= 100; startingStateOfCharge+=10) {
            Experiment experiment = new Experiment(simulation, 8*60, 12*60, startingStateOfCharge, new NewYorkDataSetReader(),
                    "new_york_full.fst", "new_york_chargingstations.json",
                    "new_york", "kmeans", 100);
            System.out.println("Starting: " + experiment);

            experiment.doExperiment();
            experiment = null;
        }
        simulation = null;
    }
}
