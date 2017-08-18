package common;

import gate.ClientSession;
import msg.gs.common.SError;
import msg.gs.common.SError2;
import msg.gs.common.SError3;
import perfect.io.Message;
import perfect.txn.Transaction;

/**
 * Created by HuangQiang on 2017/6/2.
 */
public abstract class RProcedure<T extends Message> extends perfect.txn.Procedure {
    protected final T message;
    protected final ClientSession session;
    public RProcedure(T message) {
        this.message = message;
        this.session = (ClientSession)message.getContext();

        if(getUserid() == 0 || getRoleid() == 0) throw new RuntimeException("user not login. can't call role procedure.");
    }

    public long getUserid() {
        return session.getUserid();
    }

    public long getRoleid() {
        return session.getRoleid();
    }


    protected void error(int err) {
        session.send(new SError(err));
    }

    protected void error(int err, int param) {
        session.send(new SError2(err,param));
    }

    protected void err(String err) {
        session.send(new SError3(err));
    }

    protected void response(Message res) {
        Transaction.syncExecuteWhileCommit(() -> {
                session.send(res);
        });
    }
}
