package gate;


import msg.server2gate.X2GateForwardToClientSession;
import perfect.common.Trace;
import perfect.io.Message;

/**
 * Created by HuangQiang on 2017/6/2.
 */
public final class ClientSession {
    private final int sid;
    private gate.Session gateSession;
    private volatile String account;
    private volatile long roleid;

    ClientSession(int sid, gate.Session gateSession) {
        this.sid = sid;
        this.gateSession = gateSession;
    }

    int getSid() {
        return sid;
    }

    public Session getGateSession() {
        return gateSession;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setGateSession(Session gateSession) {
        this.gateSession = gateSession;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public boolean send(Message msg) {
        Trace.log.debug("ClientSession.send sid:{} account:{} roleid:{} msg:{}", sid, account, roleid, msg);
        X2GateForwardToClientSession forward = new X2GateForwardToClientSession(sid, msg.encodeToBytes());

        if(gateSession == null || !gateSession.send(forward)) {
            gateSession = GateClient.getIns().getSession();
            return gateSession.send(forward);
        } else {
            return true;
        }
    }
}
