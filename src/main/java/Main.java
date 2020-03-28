import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.DynamicProgramming;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import domain.TaxiGraphRewardFunction;
import domain.TaxiGraphStateModel;
import domain.TaxiRecommenderDomainGenerator;
import domain.environmentrepresentation.fullenvironment.FullEnvironment;
import domain.environmentrepresentation.gridenvironment.GridEnvironment;
import domain.states.TaxiGraphState;
import parameterestimation.PragueDataSetReader;
import solver.TaxiGraphHashableFactory;
import utils.Utils;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaxiRecommenderDomainGenerator taxiRecommenderDomainGenerator = null;
        try {
            taxiRecommenderDomainGenerator = new TaxiRecommenderDomainGenerator(
                    "prague_full.fst",
                    "prague_charging_stations_full.json",
                    new GridEnvironment());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            TaxiGraphHashableFactory hashingFactory = new TaxiGraphHashableFactory();
            SADomain domain = taxiRecommenderDomainGenerator.getDomain();
            ValueIteration planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
            TaxiGraphState initialState = new TaxiGraphState(taxiRecommenderDomainGenerator.getEnvironment().getEnvironmentNodes().iterator().next().getId(), 100, Utils.SHIFT_START_TIME);
            planner.performReachabilityFrom(initialState);

            ((TaxiGraphRewardFunction)taxiRecommenderDomainGenerator.getRf()).computeRewardForStates(planner.getAllStates(), domain.getActionTypes());


            System.out.println("hgbhb");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
