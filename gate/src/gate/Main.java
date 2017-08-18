package gate;

import client.ClientServer;
import gs.GsServer;
import io.netty.channel.nio.NioEventLoopGroup;
import msg._Refs_;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        loadConfig(args);

        bind();
        startNetwork();
    }

    private static void loadConfig(String[] args) throws Exception {
        if(args.length != 1) {
            System.out.println("usage:  java -cp gate.jar gate.Main conf.xml");
            System.exit(1);
        }
        Config.getIns().load(args[0]);
    }

    private static void bind() {
        Handler.Ins.bind();
    }

    private static void startNetwork() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Config conf = Config.getIns();
        {
            ClientServer.Conf gc = conf.getGateConf();
            gc.bossGroup = boss;
            gc.workGroup = worker;
            gc.stubs = _Refs_.gate_client;
            ClientServer.start(gc);
        }
        {
            GsServer.Conf gc = conf.getGsConf();
            gc.bossGroup = boss;
            gc.workGroup = worker;
            gc.stubs = _Refs_.gate_server;
            GsServer.start(gc);
        }
    }
}
