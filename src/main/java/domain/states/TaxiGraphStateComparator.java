package domain.states;

import java.util.Comparator;

public class TaxiGraphStateComparator implements Comparator<TaxiState> {

    @Override
    public int compare(TaxiState o1, TaxiState o2) {
        return Double.compare(o2.getTimeStamp(), o1.getTimeStamp());
    }
}
