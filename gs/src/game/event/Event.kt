package game.event

/**
 * Created by HuangQiang on 2017/6/5.
 */
abstract class AbstractEvent {
    fun trigger() {
        EventMgr.trigger(this)
    }
}

class Login(val roleid : Long) : AbstractEvent()