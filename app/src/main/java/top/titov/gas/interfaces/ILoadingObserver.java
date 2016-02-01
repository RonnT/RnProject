package top.titov.gas.interfaces;

/**
 * Created by Yagupov Ruslan on 13.08.2015.
 */
public interface ILoadingObserver {

    enum Status {
        LOADING_SUCCESS,
        LOADING_LOCAL,
        LOADING_FAILED
    }

    void onLoadingStatus(Status status);
}
