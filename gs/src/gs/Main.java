package gs;

import org.apache.log4j.PropertyConfigurator;

/**
 * Created by HuangQiang on 2017/5/31.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        loadConfig(args);
        Modules.start();
    }

    private static void loadConfig(String[] args) throws Exception {
        if(args.length != 1) {
            System.out.println("usage:  java -cp gs.jar gs.Main conf.xml");
            System.exit(1);
        }
        Config.getIns().load(args[0]);
        cfg._Tables_.setDataDir("data");
        cfg._Tables_.load();
    }

}
