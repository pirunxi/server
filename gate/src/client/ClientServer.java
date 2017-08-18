package client;

import gs.GsServer;
import msg.client2gate.CBindServer;
import msg.client2gate.SBindServer;
import msg.server2gate.Gate2XDelSession;
import msg.server2gate.Gate2XForwardFromClientSession;
import org.slf4j.Logger;
import perfect.common.Trace;
import perfect.io.Connection;
import perfect.marshal.BinaryStream;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class ClientServer extends perfect.io.Server<Session> {
    private final static Logger log = Trace.log;

    private static ClientServer ins;

    public static ClientServer getIns() {
        return ins;
    }

    public static void start(Conf conf) {
        if(ins != null) throw new RuntimeException("ClientSessionManager has open");
        ins = new ClientServer(conf);
        ins.open();
    }

    public ClientServer(Conf conf) {
        super(conf);
    }

    @Override
    protected Session newSession(Connection connection) {
        return new Session(connection);
    }

    @Override
    protected void onAddSession(Session session) {

    }

    @Override
    protected void onDelSession(Session session) {
        if(session.serverid != 0)
            session.forwardToGs(new Gate2XDelSession(session.getSessionId()));
    }

    public void process(CBindServer msg) {
        if(msg.serverid <= 0) return;
        Session session = (Session)msg.getContext();
        if(session.serverid != 0) {
            log.error("session:{} try bind server:{} which has bind to server:{}", session, msg.serverid, session.serverid);
            return;
        }
        session.serverid = msg.serverid;
        session.gsSession = GsServer.getIns().getSessionByServerid(msg.serverid);
        session.send(new SBindServer(msg.serverid));
    }

    @Override
    protected boolean onUnknownMessage(Connection conn, int type, BinaryStream os) {
        Session session = (Session)conn.getContext();
        if(session.serverid == 0) return false;
        session.forwardToGs(new Gate2XForwardFromClientSession(session.getSessionId(), os.remainCopy()));
        return true;
    }
}
