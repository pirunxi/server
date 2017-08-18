package gate;

import client.ClientServer;
import gs.GsServer;
import msg.client2gate.CBindServer;
import msg.server2gate.X2GateForwardToClientSession;
import msg.server2gate.X2XAnnounceServer;
import org.slf4j.Logger;
import perfect.common.Trace;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public enum Handler {
    Ins;

    private final static Logger log = Trace.log;

    void bind() {
        X2GateForwardToClientSession.handler = this::process;
        X2XAnnounceServer.handler = this::process;

        CBindServer.handler = this::process;
    }

    private void process(X2XAnnounceServer msg) {
        log.info("Handler.process {}", msg);
        if(msg.serverid == 0) {
            log.error("X2XAnnouceServer. serverid is 0");
            return;
        }
        switch (msg.type) {
            case "gs" : {
                GsServer.getIns().process(msg);
                break;
            }
            default: {
                log.error("X2XAnnouceServer. unknown type:{}", msg.type);
            }
        }
    }

    private void process(X2GateForwardToClientSession msg) {
        GsServer.getIns().process(msg);
    }

    private void process(CBindServer msg) {
        ClientServer.getIns().process(msg);
    }
}
