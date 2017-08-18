package utils;

import annotations.RoleProcessor;
import common.IHandler;
import common.IModule;
import gs.Modules;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by HuangQiang on 2017/6/3.
 */
public class GenBinds {
    public static void main(String[] args) throws IOException {

        ArrayList<String> ls = new ArrayList<>();
        ls.add("package msg;");
        ls.add("public class _Binds_ {");
        ls.add("\tpublic static void bind() {");
        for(IModule module : Modules.getModules()) {
            for(IHandler handler : module.getHandlers()) {
                String name = handler.getClass().getName();
                for(Method method : handler.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(RoleProcessor.class)) {
                        Class<?> msgType = method.getParameterTypes()[1];
                        ls.add(String.format("\t\t%s.handler = m -> { new perfect.txn.Procedure() { protected boolean process() { return %s.Ins.process(((gate.ClientSession)m.getContext()).getRoleid(), m); }}.execute(); };", msgType.getName(), name));
//                    } else if(method.isAnnotationPresent(RoleNoTransactionProcessor.class)) {
//                        Class<?> msgType = method.getParameterTypes()[1];
//                        ls.add(String.format("\t\t%s.handler = m -> { new common.NoProcedure() { public void run() { %s.Ins.process(((gate.ClientSession)m.getContext()).getRoleid(), m); }}.execute(); };", msgType.getName(), name));
                    }
                }
            }
        }
        ls.add("\t}");
        ls.add("}");
        Files.write(Paths.get("msg", "_Binds_.java"), ls.stream().collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
    }
}
