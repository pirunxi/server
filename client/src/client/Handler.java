package client;

import gate.Session;
import msg.client2gate.SBindServer;
import msg.gs.auth.CAuth;
import msg.gs.auth.SAuth;
import msg.gs.login.*;
import msg.gs.mall.CBuy;
import msg.gs.mall.CSell;
import msg.gs.mall.SBuy;
import org.slf4j.Logger;
import perfect.common.Trace;
import perfect.io.Message;

import java.util.Random;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public enum Handler {
    Ins;
    private final static Logger log = Trace.log;

    private final Config conf = Config.getIns();
    public void bind() {
        SBindServer.handler = this::process;
        SAuth.handler = this::process;
        SGetRoleList.handler = this::process;
        SCreateRole.handler = this::process;
        SRoleLogin.handler = this::process;
        SBuy.handler = this::process;
    }

    private void reply(Message old, Message newm) {
        Session session = (Session)old.getContext();
        session.send(newm);
    }

    private void process(SBindServer msg) {
        log.info("bind server:{}", msg.serverid);
        reply(msg, new CAuth(conf.getAccount()));
    }

    private void process(SAuth msg) {
        log.info("auth {}", msg);
        reply(msg, new CGetRoleList());
    }

    private void process(SGetRoleList m) {
        log.info("rolelist :{}", m);
        if(m.roles.isEmpty()) {
            reply(m, new CCreateRole("huangqiang@" + new Random().nextInt(100), 1, 2));
        } else {
            reply(m, new CRoleLogin(m.roles.get(0).roleid));
        }
    }

    private void process(SCreateRole m) {
        log.info("create role:{}", m);
        reply(m, new CRoleLogin(m.roleinfo.roleid));
    }

    private void process(SRoleLogin m) {
        log.info("srolelogin {}", m);
        reply(m, new CBuy(1));
        reply(m, new CSell("xxx"));
    }

    private void process(SBuy m) {

    }
}
