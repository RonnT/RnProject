package top.titov.gas.model.event;

import java.io.Serializable;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class Product extends BaseEvent implements Serializable {

    private String announcement;
    private String icon;
}
