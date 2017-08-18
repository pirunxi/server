package gate;

import common.IHandler;
import common.IModule;
import gs.Config;
import io.netty.channel.nio.NioEventLoopGroup;
import msg._Refs_;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public enum Module implements IModule {
    Ins;


    @Override
    public void start() {
        Config conf = Config.getIns();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        {
            GateClient.Conf gc = conf.getGateConf();
            gc.stubs = _Refs_.gate_server;
            gc.bossGroup = boss;
            GateClient.start(gc);
        }
    }

    @Override
    public IHandler getHandler() {
        return Handler.Ins;
    }
}
