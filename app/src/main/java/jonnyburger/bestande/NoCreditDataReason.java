package jonnyburger.bestande;

/**
 * Created by jonnyburger on 11.12.15.
 */
public enum NoCreditDataReason {
    USERNAME_PW_WRONG,
    SCRAPE_TIMEOUT,
    SCRAPE_ERROR,
    SCRAPE_PARSE_ERROR,
    NO_USERNAME,
    NO_PASSWORD,
    LOGIN_PAGE_LOAD_FAIL,
    OFFLINE,
    NOT_TRIED,
    REQUEST_FAILED,
    NO_CREDENTIALS_SUPPLIED,
    USERNAME_UNKNOWN,
    OTHER_REASON
}
