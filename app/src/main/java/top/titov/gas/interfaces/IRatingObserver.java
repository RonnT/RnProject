package top.titov.gas.interfaces;

/**
 * Created by Andrew Vasilev on 30.07.2015.
 */
public interface IRatingObserver {
    void onRatingChanged(int pRatingType, float pRating);
}
