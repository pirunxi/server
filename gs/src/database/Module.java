package database;

import common.IHandler;
import common.IModule;
import gs.Config;
import perfect.db.Database;
import perfect.txn.Executor;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public enum Module implements IModule {
    Ins;

    @Override
    public void start() {
        Config conf = Config.getIns();
        Executor.start(4, 10, 2);
        Database.init(conf.getServerid(), "", db._Tables_.getTables());
        Database.getIns().start();
    }

    @Override
    public IHandler getHandler() {
        return null;
    }
}
