package top.titov.gas.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import top.titov.gas.interfaces.IObservable;

/**
 * C - list item class
 * O - observable class
 */
public class ObservableList<C> extends ArrayList<C> {
    private List<IObservable> mObservers = new ArrayList<>();

    @Override public boolean addAll(Collection<? extends C> collection) {
        boolean result = super.addAll(collection);

        notifyObservers();

        return result;
    }

    @Override
    public void clear() {
        super.clear();
        notifyObservers();
    }

    public void addObserver(IObservable pObserver) {
        mObservers.add(pObserver);
    }

    public void removeObserver(IObservable pObserver) {
        mObservers.remove(pObserver);
    }

    public void notifyObservers() {
        for (IObservable observer : mObservers) {
            observer.onDataChanged();
        }
    }
}
