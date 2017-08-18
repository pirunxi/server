package gs;

import client.ClientServer;
import gate.Config;
import msg.server2gate.X2GateForwardToClientSession;
import msg.server2gate.X2XAnnounceServer;
import perfect.io.Connection;
import perfect.io.Server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class GsServer extends Server<Session> {
    private static GsServer ins;
    public static GsServer getIns() {
        return ins;
    }

    public static void start(Conf conf) {
        if(ins != null) throw new RuntimeException("GsManager has open");
        ins = new GsServer(conf);
        ins.open();
    }

    public GsServer(Conf conf) {
        super(conf);
    }

    @Override
    protected Session newSession(Connection connection) {
        return new Session(connection);
    }


    private final ConcurrentHashMap<Integer, Session> sessionsByServerid = new ConcurrentHashMap<>();
    @Override
    protected void onAddSession(Session session) {
        X2XAnnounceServer msg = new X2XAnnounceServer();
        msg.serverid = Config.getIns().getServerid();
        msg.type = "gate";
        session.send(msg);
    }

    @Override
    protected void onDelSession(Session session) {
        if(sessionsByServerid.remove(session.serverid, session)) {
            log.info("GsServer.onDelSession  session:{} bind server:{}", session, session.serverid);
        } else {
            log.error("GsServer.onDelSession session:[} not bind server", session);
        }
    }

    public Session getSessionByServerid(int serverid) {
        return sessionsByServerid.get(serverid);
    }

    public void process(X2XAnnounceServer msg) {
        Session cur = (Session)msg.getContext();
        final int serverid = msg.serverid;
        cur.serverid = serverid;
        Session exist = sessionsByServerid.put(serverid, cur);
        if(exist != null) {
            log.error("X2XAnnounceServer serverid:{} exist session:{}. remove it and add new session:{} ", serverid, exist ,cur);
            exist.close();
        }
        log.info("X2XAnnounceServer sid:{} <==> serverid:{}", cur.getSessionId(), serverid);
    }

    public void process(X2GateForwardToClientSession msg) {
        log.debug("forward to client. msg:{}", msg);
        ClientServer.getIns().send(msg.sid, msg.msg);
    }
}
