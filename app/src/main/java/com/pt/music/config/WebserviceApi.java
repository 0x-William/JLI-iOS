package com.pt.music.config;

public interface WebserviceApi {

    // Time out
    public static int REQUEST_TIME_OUT = 30000;

    // Official site
    String PROTOCOL_HTTP = "http://";

    String PROTOCOL_HTTPS = "https://";

    //lord server : http://khmermuzik.info/mp3online/
    //fruity server : 117.7.238.103:8888/mp3online-v2
    String SERVER_DOMAIN = PROTOCOL_HTTP + "myjli.com/";

    String MUSIC_DOMAIN = SERVER_DOMAIN + "mobileapp/recordings/index.php/api/";

    /*************************
     * APIs URL
     ****************************/

    String GET_ALBUM_API = MUSIC_DOMAIN + "album";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/album

    String GET_CATEGORIES = MUSIC_DOMAIN + "category";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/category

    String GET_SONGS = MUSIC_DOMAIN + "songView";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/songView

    String GET_SONG_BY_CATEGORY = MUSIC_DOMAIN + "songCategory";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/songCategory?categoryId=5

    String GET_SONG_BY_ALBUM = MUSIC_DOMAIN + "songAlbum";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/songAlbum?albumId=1

    String SEARCH_SONG = MUSIC_DOMAIN + "nameSong";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/nameSong?song=viet

    String TOP_SONG = MUSIC_DOMAIN + "topSong";
    // http://fruitysolution.vn:8888/mp3online/index.php/api/topSong?page=1

    String ADD_NEW_VIEW = MUSIC_DOMAIN + "listenSong";
    // http://fruitysolution.vn:8888/Mp3online-v2/index.php/api/listenSong?id=6

    String ADD_NEW_DOWN_LOAD = MUSIC_DOMAIN + "downloadSong";
    // http://fruitysolution.vn:8888/Mp3online-v2/index.php/api/download?id=6

    String GET_DASHBOARD = "http://myjli.com/mobileapp/mobile-dashboard/index.php";

    String GET_FEEDBACK = "http://myjli.com/mobileapp/feedback/";

    String GET_DESKTOP = "http://myjli.com/mobileapp/desktop";

    String GET_FORUM = "http://www.myjli.com/plugins/forum/index.php";

    String GET_ADS = "http://myjli.com/mobileapp/ads/";
    /*******************************
     * API KEYS
     ************************************/

    String KEY_STATUS = "status";
    String KEY_ALL_PAGE = "allpage";
    String KEY_ERROR = "error";
    String KEY_OK = "OK";
    String KEY_SUCCESS = "success";
    String KEY_DATA = "data";
    String KEY_CONTENT = "content";
    String KEY_MESSAGE = "message";
    String KEY_DATE = "date";
    String KEY_TOTAL_PAGE = "total_page";
    String KEY_ID = "id";
    String KEY_CATEGORYID = "categoryId";
    String KEY_CALENDAR_ID = "calendar_id";
    String KEY_NAME = "name";
    String KEY_SINGER_NAME = "singerName";
    String KEY_LINK = "link";
    String KEY_USER_ID = "userID";
    String KEY_USER_ID_ADD = "userID_add";
    String KEY_USERNAME = "username";
    String KEY_IMAGE = "image";
    String KEY_SHARE_LINK = "link_app";
    String KEY_COMMENT = "comment";
    String KEY_PASSWORD = "password";
    String KEY_EMAIL = "email";
    String KEY_GENDER = "gender";
    String KEY_ADDRESS = "address";
    String KEY_BIRTHDAY = "birthday";
    String KEY_JOB_TITLE = "jobTitle";
    String KEY_MOBILEPHONE = "mobilePhone";
    String KEY_POSTCODE = "postcode";
    String KEY_HOMEPHONE = "homePhone";
    String KEY_ROLEID = "roleId";
    String KEY_AVATAR = "avatar";
    String KEY_TITLE = "title";
    String KEY_DESCRIPTION = "description";
    String KEY_TOPIC = "topic";
    String KEY_URL = "url";
    String KEY_VOTE_INFO = "voteInfo";
    String KEY_UP_VOTE_COUNT = "up_vote_count";
    String KEY_DOWN_VOTE_COUNT = "down_vote_count";
    String KEY_PRICE = "price";
    String KEY_IS_FREE = "isFree";
    String KEY_PAID = "paid";
    String KEY_SINGER = "singer";
    String KEY_SINGER_ID = "SingerID";
    String KEY_SINGERNAME = "SingerName";
    String KEY_SINGER_IMAGE = "Image";
    String KEY_SINGER_DESCRIPTION = "Description";
    String KEY_TYPE = "type";
    String KEY_VIEWS = "views";
    String KEY_LIKE = "like";
    String KEY_FAVORITE = "favorite";
    String KEY_TOPIC_ID = "TopicID";
    String KEY_USER_ONE = "user_one";
    String KEY_USER_TWO = "user_two";
    String KEY_USER_TO = "user_to";
    String KEY_TO_USER = "to_user";
    String KEY_NUMBER_ITEM = "number_item";
    String KEY_TOTAL_AMOUNT = "total_amount";
    String KEY_PLAN_START = "planStart";
    String KEY_PLAN_END = "planEnd";
    String KEY_PLAN_EFFORT = "planEffort";
    String KEY_ACTUAL_START = "actualStart";
    String KEY_ACTUAL_END = "actualEnd";
    String KEY_ACTUAL_EFFORT = "actualEffort";
    String KEY_CREATED = "created";
    String KEY_UPDATED = "updated";
    String KEY_CREATE_BY = "createdBy";
    String KEY_LOCATION_ID = "location_id";
    String KEY_PROJECT_ID = "projectId";
    String KEY_PICTURE = "picture";
    String KEY_ISPARENT = "isParent";
    String KEY_PARENT_ID = "parentId";

    /******************
     * Parameters key.
     *******************/
    // User
    public static String PARAM_USERNAME = "username";
    public static String PARAM_PASSWORD = "password";
    public static String PARAM_NAME = "name";
    public static String PARAM_EMAIL = "email";
    public static String PARAM_COUNTRY = "country";
    public static String PARAM_GENDER = "gender";

    // View
    public static String PARAM_VIEW_ID = "id";

}
