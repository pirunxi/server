package game.event

/**
 * Created by HuangQiang on 2017/6/5.
 */
abstract class AbstractEvent {
    fun trigger() {
        EvtMgr.trigger(this)
    }
}

class Login(val roleid : Long) : AbstractEvent()
class Logout(val roleid : Long) : AbstractEvent()