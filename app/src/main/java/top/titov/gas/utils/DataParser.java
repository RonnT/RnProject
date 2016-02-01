package top.titov.gas.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew Vasilev on 15.01.2015.
 * Class for parsing JSON data.
 */
public class DataParser {
    public static final String
        INDEX_LIMIT         = "limit",
        INDEX_OFFSET        = "offset",
        INDEX_ERROR_CODE    = "error_code",
        INDEX_ERROR_NAME    = "error_name";

    public static int getErrorCode(JSONObject response) throws JSONException {
        return response.getInt(INDEX_ERROR_CODE);
    }

    public static String getErrorName(JSONObject response) throws JSONException {
        return response.getString(INDEX_ERROR_NAME);
    }
}
