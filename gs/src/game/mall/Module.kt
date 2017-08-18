package game.mall

import common.IHandler
import common.IModule
import game.event.EventMgr

/**
 * Created by HuangQiang on 2017/6/5.
 */
object Module : IModule {
    override fun start() {
        EventMgr.add(game.event.Login::class.java, this, this::onEvent)
    }

    override fun getHandler(): IHandler {
        return Handler;
    }

    private fun onEvent(evt : game.event.Login) {
        println("==== ${evt.roleid} login ==")
    }
}