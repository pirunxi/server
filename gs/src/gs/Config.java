package gs;

import gate.GateClient;
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
    private final GateClient.Conf gateConf = new GateClient.Conf();

    public GateClient.Conf getGateConf() {
        return gateConf;
    }


    public int getServerid() {
        return serverid;
    }

    public void load(String xmlConfFile) throws Exception {
        Element root = Utils.openXml(xmlConfFile);
        Utils.foreach(root, (tag, ele) -> {
            switch (tag) {
                case "Base" : Utils.readBean(this, ele); break;
                case "GateClient" : Utils.readBean(gateConf, ele); break;
                default: System.err.println("unknown config tag:" + tag);
            }
        });
    }
}
