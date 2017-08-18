package gate;

import common.IHandler;
import msg.server2gate.Gate2XDelSession;
import msg.server2gate.Gate2XForwardFromClientSession;
import msg.server2gate.X2XAnnounceServer;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public enum Handler implements IHandler {
    Ins;

    public void bind() {
        X2XAnnounceServer.handler = this::process;
        Gate2XDelSession.handler = this::process;
        Gate2XForwardFromClientSession.handler = this::process;
    }

    public void process(X2XAnnounceServer msg) {
        log.debug("== process {}", msg);
    }

    public void process(Gate2XDelSession msg) {
        log.debug("== process {}", msg);
    }

    public void process(Gate2XForwardFromClientSession msg) {
        GateClient.getIns().process(msg);
    }
}
