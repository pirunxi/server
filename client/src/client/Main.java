package client;


import gate.GateClient;
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
            System.out.println("usage:  java -cp client.jar client.Main conf.xml");
            System.exit(1);
        }
        Config.getIns().load(args[0]);
    }

    private static void bind() {
        Handler.Ins.bind();
    }

    private static void startNetwork() {
        Config conf = Config.getIns();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        {
            GateClient.Conf gc = conf.getGateConf();
            gc.stubs = _Refs_.client;
            gc.bossGroup = boss;
            GateClient.start(gc);
        }
    }
}
