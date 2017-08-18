package common;

import gate.ClientSession;
import org.slf4j.Logger;
import perfect.common.Trace;
import perfect.io.Message;
import perfect.txn.Transaction;

/**
 * Created by HuangQiang on 2017/6/2.
 */
public interface IHandler {
    Logger log = Trace.log;

    void bind();

    default void response(Message from, Message reply) {
        Transaction.syncExecuteWhileCommit(() -> {
            ((ClientSession)from.getContext()).send(reply);
        });
    }

    default void directlyResponse(Message from, Message reply) {
        ((ClientSession)from.getContext()).send(reply);
    }
}

