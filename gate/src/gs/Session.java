package gs;

import perfect.io.Connection;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class Session extends perfect.io.Session {
    public int serverid;
    public Session(Connection connection) {
        super(connection);
    }
}
