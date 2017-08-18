package common;

import perfect.txn.Executor;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public abstract class NoProcedure implements Runnable{
    public final void execute() {
        Executor.getNormalExecutor().execute(this);
    }
}
