package game.mall

import common.IHandler
import common.IModule
import game.event.EvtMgr

/**
 * Created by HuangQiang on 2017/6/5.
 */
object Module : IModule {
    override fun start() {
        EvtMgr.add(game.event.Login::class.java, this, this::onEvent)
        EvtMgr.add(game.event.Logout::class.java, this, this::onEvent)
    }

    override fun getHandler(): IHandler {
        return Handler
    }

    private fun onEvent(evt : game.event.Login) {
        println("==== ${evt.roleid} login ==")
    }

    private fun onEvent(evt : game.event.Logout) {
        println("==== ${evt.roleid} login ==")
    }
}