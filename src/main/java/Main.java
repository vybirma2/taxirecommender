import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import domain.TaxiRecommenderDomainGenerator;


public class Main {
    public static void main(String[] args) {
        TaxiRecommenderDomainGenerator taxiRecommenderDomainGenerator = new TaxiRecommenderDomainGenerator(
                "data/graphs/prague_small.fst",
                "data/chargingstations/prague_charging_stations.json");
        try {

            SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
            SADomain domain = taxiRecommenderDomainGenerator.getDomain();
            Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
            GraphStateNode initialState = new GraphStateNode();
            Policy p = planner.planFromState(initialState);


            PolicyUtils.rollout(p, initialState, domain.getModel()).write("vi");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
