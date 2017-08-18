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
public abstract class UProcedure<T extends Message> extends perfect.txn.Procedure {
    protected final T message;
    protected final ClientSession session;
    public UProcedure(T message) {
        this.message = message;
        this.session = (ClientSession)message.getContext();
        if(getUserid() == 0) throw new RuntimeException("user not auth. can't call user procedure.");
    }

    public long getUserid() {
        return session.getUserid();
    }


    protected boolean error(int err) {
        session.send(new SError(err));
        return false;
    }

    protected boolean error(int err, int param) {
        session.send(new SError2(err,param));
        return false;
    }

    protected boolean error(String err) {
        session.send(new SError3(err));
        return false;
    }

    protected void response(Message res) {
        Transaction.syncExecuteWhileCommit(() -> {
            session.send(res);
        });
    }

    protected void directlySend(Message res) {
        session.send(res);
    }

    protected void syncExecuteWhileCommit(Runnable task) {
        Transaction.syncExecuteWhileCommit(task);
    }
}
