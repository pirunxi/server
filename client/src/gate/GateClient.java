package gate;

import client.Config;
import msg.client2gate.CBindServer;
import perfect.io.Connection;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public class GateClient extends perfect.io.Client<Session> {

    private static GateClient ins;

    public static GateClient getIns() {
        return ins;
    }

    public static void start(Conf conf) {
        if(ins != null) throw new RuntimeException("GateClient has started");
        ins = new GateClient(conf);
        ins.open();
    }

    private GateClient(Conf conf) {
        super(conf);
    }

    @Override
    protected Session newSession(Connection connection) {
        return new Session(connection);
    }

    @Override
    protected void onAddSession(Session session) {
       session.send(new CBindServer(Config.getIns().getConnectServerid()));
    }

    @Override
    protected void onDelSession(Session session) {
        log.info("clear all client sessions");
    }
}
