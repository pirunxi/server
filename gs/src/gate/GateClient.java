package gate;

import gs.Config;
import msg._Refs_;
import msg.gs.auth.SAuth;
import msg.server2gate.Gate2XDelSession;
import msg.server2gate.Gate2XForwardFromClientSession;
import msg.server2gate.X2XAnnounceServer;
import perfect.io.Connection;
import perfect.io.Message;
import perfect.marshal.BinaryStream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public class GateClient extends perfect.io.Client<Session> {

    private static GateClient ins;

    public static GateClient getIns() {
        return ins;
    }

    static void start(Conf conf) {
        if(ins != null) throw new RuntimeException("GateClient has started");
        ins = new GateClient(conf);
        ins.open();
    }


    private final ConcurrentHashMap<Integer, ClientSession> clients = new ConcurrentHashMap<>();


    private GateClient(Conf conf) {
        super(conf);
    }

    @Override
    protected Session newSession(Connection connection) {
        return new Session(connection);
    }

    @Override
    protected void onAddSession(Session session) {
        X2XAnnounceServer msg = new X2XAnnounceServer();
        msg.type = "gs";
        msg.serverid = Config.getIns().getServerid();
        session.send(msg);
    }

    @Override
    protected void onDelSession(Session session) {
        log.info("clear all client sessions");
        clients.clear();
    }

    public void process(Gate2XDelSession msg) {
        ClientSession session = clients.remove(msg.sid);
        if(session != null) {
            log.info("remove client session:{}", session);
        } else {
            log.warn("remove not exist client session:{}", msg.sid);
        }
    }

    public void process(Gate2XForwardFromClientSession forward) {
        BinaryStream bs = BinaryStream.wrap(forward.msg);
        try {
            final int sid = forward.sid;
            final Message m = Message.decode(_Refs_.gs_client, bs);
            if(m != null) {
                ClientSession session = clients.get(sid);
                if(session == null) {
                    session = new ClientSession(sid, getSession());
                    clients.put(sid, session);
                }
                dispatch(m, session);
            } else {
                log.debug("Gate2XForwardFromClientSession sid:{} unknown msg id", sid);
            }
        } catch (Exception e) {
            log.error("process Gate2XForwardFromClientSession", e);
        }
    }

    public interface MsgProcessor {
        void process(Message m);
    }

    private static final Map<Class<?>, MsgProcessor> processors = new HashMap<>();

    public static void addProcessor(Class<?> clazz, MsgProcessor processor) {
        if(processors.put(clazz, processor) != null) {
            throw new RuntimeException("msg:" + clazz.getName() + " processor duplicate!");
        }
    }

    private void dispatch(Message m, ClientSession session) {
        int mid = m.getTypeId();
        String account = session.getAccount();
        if(account == null && !_Refs_.gs_client_not_auth.containsKey(mid)) {
            log.debug("session sid:{} not auth. can't send msg:{}", session.getSid(), m);
            return;
        }
        long roleid = session.getRoleid();
        if(roleid == 0 && !_Refs_.gs_client_not_role.containsKey(mid)) {
            log.debug("session sid:{} account:{} not role login. can't send msg:{}", session.getSid(), account, m);
            return;
        }
        log.debug("GateClient.dispatch sid:{} account:{} roleid:{} msg:{}", session.getSid(), account, roleid, m);
        m.setContext(session);

        MsgProcessor processor = processors.get(m.getClass());
        if(processor != null) {
            processor.process(m);
        } else {
            m.run();
        }
    }

    public void auth(ClientSession session, String account) {
        if(session.getAccount() != null) return;
        session.setAccount(account);
        session.setRoleid(0);

        session.send(new SAuth(account));
        log.info("GateClient.auth sid:{} account:{}", session.getSid(), account);
    }

    public void login(ClientSession session, long roleid) {
        session.setRoleid(roleid);
        log.info("GateClient.login sid:{} account:{} roleid:{}", session.getSid(), session.getAccount(), session.getRoleid());
    }
}
