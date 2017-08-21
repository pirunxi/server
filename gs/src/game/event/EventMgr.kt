package game.event

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by HuangQiang on 2017/6/5.
 */
object EventMgr {

    private data class ListenerInfo<T : AbstractEvent>(val clazz : Class<T>, val obj : Any, val listener : (T) -> Unit)

    fun<T : AbstractEvent> add(clazz: Class<T>, obj : Any, listener : (T) -> Unit) {
        synchronized(this) {
            val info = ListenerInfo(clazz, obj, listener)
            val listeners = listenersByType.getOrDefault(clazz, ArrayList<ListenerInfo<AbstractEvent>>())
            if(listeners.any {it.obj == obj})
                throw RuntimeException("listener class:$clazz try listen twice obj:$obj")
            listenersByType.put(clazz, listeners.plus(info))
        }
    }

    fun<T : AbstractEvent> remove(clazz : Class<T>, obj : Any) {
        synchronized(this) {
            val listeners = listenersByType[clazz]
            if(listeners == null || listeners.none { it.obj == obj}) {
                throw RuntimeException("listener class:$clazz not exist obj:$obj")
            }
            listenersByType.put(clazz, listeners.filter { it.obj != obj }.toList())
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun<T : AbstractEvent> trigger(arg : T) {
        val listeners = listenersByType[arg.javaClass]
        if(listeners != null) {
            for(listener in listeners) {
                (listener.listener as ((T) -> Unit))(arg)
            }
        }
    }

    private val listenersByType = ConcurrentHashMap<Class<*>, List<ListenerInfo<out AbstractEvent>>>()
}