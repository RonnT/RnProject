package top.titov.gas.helper;

import java.util.Comparator;

import top.titov.gas.model.azs.Azs;

/**
 * Created by Andrew Vasilev on 31.07.2015.
 */
public class AzsDistanceComparator implements Comparator<Azs> {
    @Override
    public int compare(Azs lhs, Azs rhs) {
        float d1 = lhs.getDistance();
        float d2 = rhs.getDistance();

        return d1 < d2 ? -1 : d1 == d2 ? 0 : 1;
    }
}
