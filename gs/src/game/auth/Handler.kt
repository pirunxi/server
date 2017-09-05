package game.auth

import annotations.Handler
import common.IHandler
import common.session
import gate.GateClient
import msg.gs.auth.CAuth
import perfect.common.Trace.log
import perfect.db.Database
import perfect.txn.Transaction

/**
 * Created by HuangQiang on 2017/6/5.
 */
@Handler
object Handler : IHandler {
    override fun bind() {
        CAuth.handler = CAuth.Handler { PCAuth(it).execute() }
    }

   private class PCAuth(val m : CAuth) : perfect.txn.Procedure() {
       override fun process(): Boolean {

           if(m.account.isEmpty()) {
               log.debug("CAuth. account is empty")
               return false
           }
           var account = db.auth.Accounts.get(m.account)
           if(account == null) {
               account = db.auth.Account.newBean()
               val now = System.currentTimeMillis()
               account.createtime = now
               db.auth.Accounts.insert(m.account, account)
               log.info("create account:{}", m.account)
           }
           Transaction.syncExecuteWhileCommit({
               GateClient.getIns().auth(m.session, m.account)
           })
           return true
       }
   }
}