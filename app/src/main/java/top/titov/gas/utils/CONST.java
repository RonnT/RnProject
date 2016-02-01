package top.titov.gas.utils;

// import org.osmdroid.util.GeoPoint;

/**
 * Created by Andrew Vasilev on 16.01.2015.
 * Class contains project constance
 */
public class CONST {

    public static final long
            DEFAULT_UPDATE_TIMESTAMP    = 1450082414;

    public static final String
            FROM_BOTTOM_NOTIF           = "FROM_BOTTOM_NOTIF",

            TAG_LAT                     = "TAG_LAT",
            TAG_LNG                     = "TAG_LNG",

            TAG_AZS                     = "TAG_AZS",
            TAG_AZS_ADDRESS             = "TAG_AZS_ADDRESS",
            TAG_AZS_RATING_OVERALL      = "TAG_AZS_RATING_OVERALL",
            TAG_EVENT                   = "TAG_EVENT",

            TAG_MENU_FRAGMENT           = "TAG_MENU_FRAGMENT",
            POSITION                    = "POSITION",

            INDEX_USER_AGENT            = "User-Agent",
            INDEX_DATA                  = "data",
            INDEX_REGION_CODE           = "region_code",

            RATE_SERVICE                = "RATE_SERVICE",
            RATE_WAITING_TIME           = "RATE_WAITING_TIME",
            RATE_FUEL_QUALITY           = "RATE_FUEL_QUALITY",
            RATE_FUEL_PRICE             = "RATE_FUEL_PRICE",
            RATE_SHOP_PRICE             = "RATE_SHOP_PRICE",

            INTENT_UPDATE_AZS           = "INTENT_UPDATE_AZS",
            INTENT_UPDATE_LOCATION      = "UPDATE_LOCATION",
            INTENT_UPDATE_LOCATION_AZS      = "INTENT_UPDATE_LOCATION_AZS",
            INTENT_UPDATE_PHOTOS        = "UPDATE_PHOTOS",
            NEW_REGION                  = "NEW_REGION",

            SELECTED_ITEM               = "SELECTED_ITEM",
            PUSH_ITEM_ID                = "PUSH_ITEM_ID",
            PUSH_EVENT_ID               = "PUSH_EVENT_ID",
            PUSH_PRODUCT_ID             = "PUSH_PRODUCT_ID",
            PUSH_TYPE                   = "PUSH_TYPE",
            PUSH_MESSAGE                = "PUSH_MESSAGE",
            FROM_PUSH                   = "FROM_PUSH",

            DATE_FORMAT                 = "dd.MM.yyyy в HH:mm",
            ACTUAL_PRICE_DATE_FORMAT    = "dd.MM.yyyy",
            TIME_FORMAT                 = "HH:mm";

    public static final int
            NOT_DEFINED                 = -1,

            ZOOM_MY_LOCATION            = 10,
            ZOOM_RUSSIA                 = 4,

            MIN_DISTANCE_TO_LOAD_AZS_METERS     = 15000,
            CLOSEST_AZS_RADIUS_METERS           = 20000,

            ERROR_CODE_OK               = 0,
            RESULT_CODE_OK              = -1,
            DEFALUT_OFFSET              = 0,
            DEFALUT_LIMIT               = 60,
            ITEMS_TO_LOAD_MORE          = 10,
            MAX_PHOTO_NUMBER_IN_QUERY   = 10,

            ANIMATION_BURGER_TIME       = 500,
            DEFAULT_REGION              = 77,
            REQ_CODE_SELECT_REGION      = 1,

            AZS_TYPE_0                  = 0,
            AZS_TYPE_1                  = 1,

            PUSH_TYPE_FIRST             = 1,
            PUSH_TYPE_EVENT             = 2,
            PUSH_TYPE_PRODUCT           = 3,

            FIRST_ITEM                  = 0,

            FLASHING_TIME               = 3000;

    public static final String
                FILTER_WASH         = "wash",
                FILTER_TIRE = "tire",
                FILTER_CAFE         = "cafe",
                FILTER_SHOP         = "shop",
                FILTER_VISA         = "visa",
                FILTER_FUEL         = "FILTER_FUEL",

                FILTER_FUEL_92              = "92",
                FILTER_FUEL_95              = "95",
                FILTER_FUEL_98              = "98",
                FILTER_FUEL_DIESEL          = "diesel",
                FILTER_FUEL_GAS             = "gas",

                FUEL_92             = "92",
                FUEL_95             = "95",
                FUEL_98             = "98",
                FUEL_DIESEL         = "diesel",
                FUEL_92_FORA        = "92_fora",
                FUEL_95_FORA        = "95_fora",
                FUEL_98_FORA        = "98_fora",
                FUEL_DIESEL_FORA    = "diesel_fora",
                FUEL_GAS            = "gas",

                INTENT_SHOW_SNACK_NOTIF             = "INTENT_SHOW_SNACK_NOTIF",

                NEED_DISTANCE       = "1",
                NEED_AS_SYNCHRONISE = "1";

    public static final int
                RATING_SMALL        = 3,
                SERVICE_EXIST       = 1,
                FUEL_AVAILABLE      = 1;

    public static final short
        LIST_HEADER                 = 1;

    public static final short
        HTTP_REQUEST_SUCCESS        = 200;

    public static final long
        VIBRATE_TIME                = 1000,
        FUEL_PRICE_UPDATE_INTERVAL  = 3600000;

    public static final boolean
        CHECK_CHANGES            = true,
        DONT_CHECK          = false;
}
