package evaluation;

import domain.parameterestimation.DataSetReader;
import domain.utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Experiment {

    private Simulation simulation;

    public int SHIFT_START_TIME;
    public int SHIFT_LENGTH;
    public int STARTING_STATE_OF_CHARGE;
    public String ENVIRONMENT;
    public DataSetReader DATA_SET_READER;
    public String INPUT_GRAPH_FILE_NAME;
    public String INPUT_STATION_FILE_NAME;
    public String DATA_SET_NAME;
    public int ONE_GRID_CELL_WIDTH;
    public int ONE_GRID_CELL_HEIGHT;
    public int NUM_OF_CLUSTERS;


    public Experiment(Simulation simulation, int SHIFT_START_TIME, int SHIFT_LENGTH, int STARTING_STATE_OF_CHARGE, DataSetReader DATA_SET_READER,
                      String INPUT_GRAPH_FILE_NAME, String INPUT_STATION_FILE_NAME, String DATA_SET_NAME, String ENVIRONMENT,
                      int ONE_GRID_CELL_HEIGHT, int ONE_GRID_CELL_WIDTH) {
        this.simulation = simulation;
        this.SHIFT_START_TIME = SHIFT_START_TIME;
        this.SHIFT_LENGTH = SHIFT_LENGTH;
        this.STARTING_STATE_OF_CHARGE = STARTING_STATE_OF_CHARGE;
        this.ENVIRONMENT = ENVIRONMENT;
        this.DATA_SET_READER = DATA_SET_READER;
        this.INPUT_GRAPH_FILE_NAME = INPUT_GRAPH_FILE_NAME;
        this.INPUT_STATION_FILE_NAME = INPUT_STATION_FILE_NAME;
        this.DATA_SET_NAME = DATA_SET_NAME;
        this.ONE_GRID_CELL_WIDTH = ONE_GRID_CELL_WIDTH;
        this.ONE_GRID_CELL_HEIGHT = ONE_GRID_CELL_HEIGHT;

        Utils.setUtilsParameters(SHIFT_START_TIME, SHIFT_LENGTH, STARTING_STATE_OF_CHARGE, DATA_SET_READER, INPUT_GRAPH_FILE_NAME,
                INPUT_STATION_FILE_NAME, DATA_SET_NAME, ENVIRONMENT, ONE_GRID_CELL_HEIGHT, ONE_GRID_CELL_WIDTH);
    }


    public Experiment(Simulation simulation, int SHIFT_START_TIME, int SHIFT_LENGTH, int STARTING_STATE_OF_CHARGE, DataSetReader DATA_SET_READER,
                      String INPUT_GRAPH_FILE_NAME, String INPUT_STATION_FILE_NAME, String DATA_SET_NAME, String ENVIRONMENT,
                      int NUM_OF_CLUSTERS) {
        this.simulation = simulation;
        this.SHIFT_START_TIME = SHIFT_START_TIME;
        this.SHIFT_LENGTH = SHIFT_LENGTH;
        this.STARTING_STATE_OF_CHARGE = STARTING_STATE_OF_CHARGE;
        this.ENVIRONMENT = ENVIRONMENT;
        this.DATA_SET_READER = DATA_SET_READER;
        this.INPUT_GRAPH_FILE_NAME = INPUT_GRAPH_FILE_NAME;
        this.INPUT_STATION_FILE_NAME = INPUT_STATION_FILE_NAME;
        this.DATA_SET_NAME = DATA_SET_NAME;
        this.NUM_OF_CLUSTERS = NUM_OF_CLUSTERS;
    }


    public void doExperiment() throws IOException {
        Utils.setUtilsParameters(SHIFT_START_TIME, SHIFT_LENGTH, STARTING_STATE_OF_CHARGE, DATA_SET_READER,
                INPUT_GRAPH_FILE_NAME, INPUT_STATION_FILE_NAME, DATA_SET_NAME, ENVIRONMENT, NUM_OF_CLUSTERS);

        simulation.setAgent("base");

        for (int i = 0; i < Utils.NUM_OF_SHIFTS_IN_EXPERIMENTS; i++){
            simulation.startSimulation();
            simulation.clearShiftSimulationResults();
            saveSimulationStatistics(simulation.getSimulationStatistics(), "base");
            simulation.clearStatistics();
        }
        /*simulation.switchAgents("base");

        for (int i = 0; i < Utils.NUM_OF_SHIFTS_IN_EXPERIMENTS; i++){
            simulation.startSimulation();
            simulation.clearShiftSimulationResults();
            saveSimulationStatistics(simulation.getSimulationStatistics(), "base");
            simulation.clearStatistics();
        }*/
    }

    private void saveSimulationStatistics(SimulationStatistics simulationStatistics, String agent) throws IOException {
        try(FileWriter fw = new FileWriter("data/experimentresults/" + agent + "_env-" + ENVIRONMENT + "_start-"
                + SHIFT_START_TIME + "_charge-" + STARTING_STATE_OF_CHARGE + "_city-" + DATA_SET_NAME + ".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(simulationStatistics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String toString() {
        return "Experiment: " +
                " SHIFT_START_TIME = " + SHIFT_START_TIME +
                ", SHIFT_LENGTH = " + SHIFT_LENGTH +
                ", STARTING_STATE_OF_CHARGE = " + STARTING_STATE_OF_CHARGE +
                ", ENVIRONMENT = " + ENVIRONMENT +
                ", INPUT_GRAPH_FILE_NAME = " + INPUT_GRAPH_FILE_NAME +
                ", INPUT_STATION_FILE_NAME = " + INPUT_STATION_FILE_NAME +
                ", DATA_SET_NAME = " + DATA_SET_NAME +
                ", ONE_GRID_CELL_WIDTH = " + ONE_GRID_CELL_WIDTH +
                ", ONE_GRID_CELL_HEIGHT = " + ONE_GRID_CELL_HEIGHT +
                ", NUM_OF_CLUSTERS = " + NUM_OF_CLUSTERS;
    }
}
