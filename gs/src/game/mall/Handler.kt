package game.mall

import annotations.RoleProcessor
import common.IHandler
import common.IHandler.log
import common.getRoleid
import common.response
import common.responseDirecly
import msg.gs.mall.CBuy
import msg.gs.mall.CSell
import msg.gs.mall.SBuy
import msg.gs.mall.SSell

/**
 * Created by HuangQiang on 2017/6/5.
 */
object Handler : IHandler {
    override fun bind() {

    }

    @RoleProcessor
    fun process(m : CBuy) : Boolean {
        m.response(SBuy(2))
        return true
    }

    @RoleProcessor
    fun process(m : CSell) : Boolean {
        log.debug("== process roleid:{} {}", m.getRoleid(), m)
        m.responseDirecly(SSell("${m.id}@huangqiang"))
        return true
    }
}