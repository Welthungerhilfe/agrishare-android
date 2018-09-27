package app.agrishare;

/**
 * Created by ernestnyumbu on 1/8/2018.
 */

public interface Constants {

    double screeninchesthresholdforgridcolumncountchange = 9.0;

    //JSON names
    String KEY_USERNAME = "username";
    String KEY_PASSWORD = "password";
    String KEY_RESET_TOKEN = "resetToken";
    String KEY_EMAIL_ADDRESS = "emailaddress";
    String KEY_FIRST_NAME = "firstname";
    String KEY_LAST_NAME = "lastname";
    String KEY_BIOGRAPHY = "biography";
    String KEY_PAGE_INDEX = "PageIndex";
    String KEY_PAGE_SIZE = "PageSize";
    String KEY_QUERY = "Query";
    String KEY_USER = "User";
    String KEY_START_DATE = "StartDate";
    String KEY_POST = "Post";
    String KEY_PostId = "PostId";
    String KEY_Liked = "Liked";
    String KEY_LikeCount = "LikeCount";
    String KEY_ID = "Id";
    String KEY_RecentLikes = "RecentLikes";
    String KEY_Followers = "Followers";
    String KEY_UserId = "UserId";
    String KEY_FirstName = "FirstName";
    String KEY_FROM_NOTIFICATION = "from_notification";
    String KEY_FollowStatus = "FollowStatus";
    String KEY_ThumbPath = "ThumbPath";
    String KEY_ZoomPath = "ZoomPath";
    String KEY_ImageRatio = "ImageRatio";
    String KEY_FilePath= "FilePath";
    String KEY_NOTIFICATION_ID= "notificationID";
    String KEY_FILTER_BY_TAG = "FilterByTag";
    String KEY_FILTER_BY_LOCATION = "FilterByLocation";
    String KEY_FAQ = "FAQ";
    String KEY_TITLE = "title";
    String KEY_HASHTAG_ID = "HashtagId";
    String KEY_HASHTAG = "Hashtag";
    String KEY_PLACE_ID = "placeId";
    String KEY_COMPETITION = "Competition";
    String KEY_COMPETITION_USER = "CompetitionUser";
    String KEY_TELEPHONE = "Telephone";
    String KEY_LOCATION = "Location";
    String KEY_EQUIPMENT_SERVICE = "EquipmentService";
    String KEY_ENABLE_TEXT= "EnableText";
    String KEY_SERVICE = "Service";
    String KEY_CATEGORY = "Category";
    String KEY_IS_LOOKING = "is_looking";
    String KEY_LISTING = "LISTING";
    String KEY_EDIT = "edit";
    String KEY_SEARCH_QUERY = "search_query";
    String KEY_BOOKING = "Booking";
    String KEY_NOTIFICATION = "Notification";

    //for tabs
    String DASHBOARD = "dashboard";
    String SEARCH = "search";
    String MANAGE = "manage";
    String PROFILE = "profile";
    
    //PREFS
    String PREFS = "user_prefs";
    String PREFS_USER_DETAILS = "user_details";
    String PREFS_TOKEN = "token";
    String PREFS_IS_DEVICE_REGISTERED_ON_OUR_SERVER = "isDeviceRegisteredOnOurServer";
    String PREFS_LAST_UPDATE = "last_update";
    String PREFS_LAST_NOTIFICATIONS_UPDATE = "last_notifications_update";
    String PREFS_CURRENT_LANGUAGE = "current_language";
    String PREFS_CURRENT_LANGUAGE_LOCALE_NAME = "current_language_locale_name";
    String PREFS_HAS_SHOWN_DASHBOARD_INTRO = "hasShownDashboardIntro";

    //endpoint names
    String GET_USERNAME = "authentication/username/unique";
    String POST_REGISTER = "authentication/register";
    String POST_LOGIN = "authentication/login";
    String POST_PASSWORD_FORGOT = "authentication/password/forgot";
    String POST_PASSWORD_RESET = "authentication/password/reset";
    String POST_SUBMIT = "post/submit";
    String GET_POST_DELETE = "post/delete";
    String POST_UPDATE = "post/update";
    String POST_FOLLOW= "user/follow";
    String POST_UNFOLLOW= "user/unfollow";
    String POST_COMMENT = "post/comments/add";
    String POST_USER_PROFILE_EDIT = "user/profile/edit";
    String POST_COMPETITIONS_ADD = "competitions/add";
    String GET_USER_PROFILE = "user/profile/userid";
    String GET_USER_PROFILE_BY_USERNAME = "user/profile/username";
    String GET_USER_DEVICE_REGISTER = "user/device/register";
    String GET_USER_DEVICE_REMOVE = "user/device/remove";
    String POST_NOTIFICATIONS_READ = "notifications/read";
    String POST_FORGOT_PASSWORD = "authentication/password/forgot";
    String GET_EXPLORE_TAG_BY_HASHTAG = "explore/posts/tagtext";
    String GET_EXPLORE_RANDOM_POSTS = "explore/posts/random";
    String GET_EXPLORE_POSTS_TAG_BY_ID = "explore/posts/tag/id";
    String GET_EXPLORE_POSTS_TAG = "explore/posts/tag";
    String GET_EXPLORE_POSTS_NEARBY = "explore/posts/nearby";
    String GET_EXPLORE_POSTS_LOCATION = "explore/posts/location";
    String GET_EXPLORE_POSTS_SPECIES = "explore/posts/species";
    String GET_EXPLORE_TAGS = "explore/tags";
    String GET_EXPLORE_USERS = "explore/users";
    String GET_EXPLORE_LOCATIONS = "explore/locations";
    String GET_USER_FEED = "user/feed";
    String GET_USER_FEED_LATEST = "user/feed/latest";
    String GET_USER_POSTS = "user/posts";
    String GET_POST_COMMENTS = "post/comments";
    String GET_USER_FOLLOWING_STATUS = "user/following/status";
    String GET_NOTIFICATIONS = "notifications";
    String GET_NOTIFICATIONS_LATEST = "notifications/latest";
    String GET_POST_LIKES_ADD = "post/likes/add";
    String GET_POST_LIKES_REMOVE = "post/likes/remove";
    String GET_FOLLOWERS = "user/followers";
    String GET_FOLLOWING = "user/following";
    String GET_POST = "post";
    String GET_POST_REPORT = "post/report";
    String GET_POST_LIKES = "post/likes";
    String GET_EXPLORE_SPECIES = "explore/species";
    String GET_COMPETITIONS_JOIN = "competitions/join";
    String GET_COMPETITIONS_LEAVE = "competitions/leave";
    String GET_POST_SPECIES_SUGGEST = "post/species/suggest";
    String GET_POST_HASHTAGS_SUGGEST = "post/hashtags/suggest";
}
