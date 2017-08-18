package common;

import java.util.Collections;
import java.util.List;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public interface IModule {
    void start();

    IHandler getHandler();

    default List<IHandler> getHandlers() {
        IHandler handler = getHandler();
        return handler != null ? Collections.singletonList(handler) : Collections.emptyList();
    }
}
