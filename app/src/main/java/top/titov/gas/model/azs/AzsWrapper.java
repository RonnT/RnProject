package top.titov.gas.model.azs;

import java.util.List;

/**
 * Created by Roman Titov on 03.11.2015.
 */
public class AzsWrapper {

    private List<Azs> stations;

    public Azs getAzs(){
        return new Azs();
    }

    public List<Azs> getAzsList(){
        return stations;
    }
}
