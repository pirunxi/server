package game.mall

import annotations.Handler
import annotations.RoleProcessor
import common.IHandler
import common.IHandler.log
import common.getRoleid
import common.response
import common.responseDirecly
import logger.FormatLog
import msg.gs.mall.CBuy
import msg.gs.mall.CSell
import msg.gs.mall.SBuy
import msg.gs.mall.SSell

/**
 * Created by HuangQiang on 2017/6/5.
 */
@Handler
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
        FormatLog("sell").param("roleid", m.getRoleid()).param("shopid", m.id).log()
        return true
    }
}