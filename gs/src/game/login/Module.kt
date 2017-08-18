package game.login

import common.IHandler
import common.IModule

/**
 * Created by HuangQiang on 2017/6/5.
 */
object Module : IModule {
    override fun start() {

    }

    override fun getHandler(): IHandler {
        return Handler
    }
}