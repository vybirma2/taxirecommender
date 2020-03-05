package solver;

import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FullModel;
import burlap.mdp.singleagent.model.TransitionProb;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.*;

public class MyValueIteration extends ValueIteration {
    public MyValueIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxIterations);
    }




    @Override
    public boolean performReachabilityFrom(State si) {
        HashableState sih = this.stateHash(si);
        if (this.valueFunction.containsKey(sih) && this.foundReachableStates) {
            return false;
        } else {
            DPrint.cl(this.debugCode, "Starting reachability analysis");
            LinkedList<HashableState> openList = new LinkedList();
            Set<HashableState> openedSet = new HashSet();
            openList.offer(sih);
            openedSet.add(sih);

            while(true) {
                HashableState sh;
                do {
                    do {
                        if (openList.isEmpty()) {
                            DPrint.cl(this.debugCode, "Finished reachability analysis; # states: " + this.valueFunction.size());
                            this.foundReachableStates = true;
                            this.hasRunVI = false;
                            return true;
                        }

                        sh = openList.poll();
                    } while(this.valueFunction.containsKey(sh));
                } while(this.model.terminal(sh.s()) && this.stopReachabilityFromTerminalStates);

                this.valueFunction.put(sh, this.valueInitializer.value(sh.s()));
                List<Action> actions = this.applicableActions(sh.s());
                Iterator var7 = actions.iterator();

                while(var7.hasNext()) {
                    Action a = (Action)var7.next();
                    List<TransitionProb> tps = ((FullModel)this.model).transitions(sh.s(), a);
                    Iterator var10 = tps.iterator();

                    while(var10.hasNext()) {
                        TransitionProb tp = (TransitionProb)var10.next();
                        HashableState tsh = this.stateHash(tp.eo.op);
                        if (!openedSet.contains(tsh) && !this.valueFunction.containsKey(tsh)) {
                            openedSet.add(tsh);
                            openList.offer(tsh);
                        }
                    }
                }
            }
        }
    }


    @Override
    public HashableState stateHash(State s) {
        return this.hashingFactory.hashState(s);
    }

}
