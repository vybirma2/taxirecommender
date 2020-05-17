package evaluation.experiments;

import domain.parameterestimation.PragueDataSetReader;
import domain.utils.Utils;
import evaluation.Experiment;
import evaluation.Simulation;

import java.io.IOException;


/**
 * Experiment using Prague historical taxi trip computing simulation results of Base Model Agent and Charging
 * Recommender Model Agent with K-Means environment.
 * */
public class PragueBaseKMeansExperiment {


    public static void doExperiment() throws IOException {
        Simulation simulation = new Simulation();

        Utils.setUtilsParameters(8*60, 12*60, 20, new PragueDataSetReader(),
                "prague.fst", "prague_charging_stations_full.json",
                "prague", "kmeans", 125);
        simulation.initSimulation();
        for (int startingStateOfCharge = 10; startingStateOfCharge <= 100; startingStateOfCharge+=10){
            Experiment experiment = new Experiment(simulation, 8*60, 12*60, startingStateOfCharge, new PragueDataSetReader(),
                    "prague.fst", "prague_charging_stations_full.json",
                    "prague", "kmeans", 125);
            System.out.println("Starting: " + experiment);

            experiment.doExperiment();
            experiment = null;
        }
        simulation = null;
    }
}
