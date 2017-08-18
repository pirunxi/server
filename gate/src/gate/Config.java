package gate;

import client.ClientServer;
import gs.GsServer;
import org.w3c.dom.Element;
import perfect.common.Utils;

/**
 * Created by HuangQiang on 2017/6/1.
 */
public class Config {
    private static final Config ins = new Config();
    public static Config getIns() {
        return ins;
    }


    private int serverid;
    private final ClientServer.Conf gateConf = new ClientServer.Conf();
    private final GsServer.Conf gsConf = new GsServer.Conf();

    public ClientServer.Conf getGateConf() {
        return gateConf;
    }

    public GsServer.Conf getGsConf() {
        return gsConf;
    }

    public int getServerid() {
        return serverid;
    }

    public void load(String xmlConfFile) throws Exception {
        Element root = Utils.openXml(xmlConfFile);
        Utils.foreach(root, (tag, ele) -> {
            switch (tag) {
                case "Base" : Utils.readBean(this, ele); break;
                case "ClientServer" : Utils.readBean(gateConf, ele); break;
                case "GsServer" : Utils.readBean(gsConf, ele); break;
                default: System.err.println("unknown config tag:" + tag);
            }
        });
    }
}
