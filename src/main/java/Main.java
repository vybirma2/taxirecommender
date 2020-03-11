import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.singleagent.SADomain;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;
import parameterestimation.PragueDataSetReader;
import solver.TaxiGraphHashableFactory;

public class Main {
    public static void main(String[] args) {

        TaxiRecommenderDomainGenerator taxiRecommenderDomainGenerator = null;
        try {
            taxiRecommenderDomainGenerator = new TaxiRecommenderDomainGenerator(
                    "data/graphs/prague_small.fst",
                    "data/chargingstations/prague_charging_stations_full.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            TaxiGraphHashableFactory hashingFactory = new TaxiGraphHashableFactory();
            SADomain domain = taxiRecommenderDomainGenerator.getDomain();
            Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
            TaxiGraphState initialState = new TaxiGraphState(0, 100, 0);
            Policy p = planner.planFromState(initialState);


            PolicyUtils.rollout(p, initialState, domain.getModel()).write("vi");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
