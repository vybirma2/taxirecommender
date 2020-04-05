package domain.states;

import java.util.Comparator;

public class TaxiGraphStateComparator implements Comparator<TaxiGraphState> {


    @Override
    public int compare(TaxiGraphState o1, TaxiGraphState o2) {
        return Double.compare(o1.getStateOfCharge(), o2.getStateOfCharge());
    }
}
