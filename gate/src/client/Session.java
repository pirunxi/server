package client;

import gs.GsServer;
import perfect.io.Connection;
import perfect.io.Message;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class Session extends perfect.io.Session {
    public int serverid;
    public gs.Session gsSession;
    public Session(Connection connection) {
        super(connection);
    }

    public void forwardToGs(Message msg) {
        if(gsSession == null || !gsSession.send(msg)) {
            gsSession = GsServer.getIns().getSessionByServerid(serverid);
            if(gsSession != null && !gsSession.send(msg)) {
                gsSession = null;
            }
        }
    }
}
