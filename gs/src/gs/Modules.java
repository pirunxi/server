package gs;

import annotations.RoleProcessor;
import common.IHandler;
import common.IModule;
import game.login.Module;
import gate.GateClient;
import org.slf4j.Logger;
import perfect.common.Trace;
import perfect.io.Message;
import perfect.txn.Procedure;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public class Modules {
    private final static Logger log = Trace.log;
    private final static List<IModule> modules = Arrays.asList(
            game.auth.Module.INSTANCE
            , Module.INSTANCE
            , game.mall.Module.INSTANCE


            //  这几个必须放最后
            ,database.Module.Ins
            ,gate.Module.Ins
    );

    public static List<IModule> getModules() {
        return modules;
    }

    private static void bindByAnnotation(IHandler handler) {
        // msg._Binds_.bind();
        String name = handler.getClass().getName();
        for(Method method : handler.getClass().getDeclaredMethods()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if(method.isAnnotationPresent(RoleProcessor.class)) {
                if(method.getReturnType() != boolean.class || paramTypes.length != 1 || !Message.class.isAssignableFrom(paramTypes[0])) {
                    log.error("handler:{} method:{} is annotated as RoleHandler, signture must be  (long, T extends Message) -> boolean"
                            ,name, method);
                    throw new RuntimeException("handler:" + handler + " method: " + method + " is annotated as RoleHandler, signture must be (T extends Message) -> boolean");
                }

                GateClient.addProcessor(paramTypes[0], m -> {
                    new Procedure() {
                        @Override
                        protected boolean process() {
                            try {
                                return (Boolean)method.invoke(handler, m);
                            } catch (Exception e) {
                                log.error("RoleMsgProcessor", e);
                                return false;
                            }
                        }
                    }.execute();
                });

            } else if(method.getName().equals("process")) {
                // 检查是否有函数遗漏写 注解了
                if(paramTypes.length == 1 && Message.class.isAssignableFrom(paramTypes[0])) {
                    log.warn("handler:{} method:{} is not annotated as RoleHandler. forget @RoleHanlder ?", name, method);
                }
            }
        }
    }

    static void start() {

        Set<IModule> startedModules = new HashSet<>();
        for(IModule module : modules) {
            String name = module.getClass().getName();
            if(!startedModules.add(module)) {
                throw new RuntimeException("module:" + name + " duplicate");
            }
            log.info("module:{} init begin", name);
            for(IHandler handler : module.getHandlers()) {
                handler.bind();
                bindByAnnotation(handler);
                //checkUnbindMessageProcessor(handler);
            }
            module.start();
            log.info("module:{} init end", name);
        }
    }
}
