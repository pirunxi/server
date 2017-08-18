package game.auth

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
object Handler : IHandler {
    override fun bind() {
        CAuth.handler = CAuth.Handler { PCAuth(it).execute() }
    }

   private class PCAuth(val m : CAuth) : perfect.txn.Procedure() {
       override fun process(): Boolean {

           if(m.account.isEmpty()) {
               log.debug("CAuth. account is empty");
               return false;
           }
           var account = db.auth.Accounts.get(m.account);
           val userid : Long
           if(account == null) {
               account = db.auth.Account.newBean()
               val now = System.currentTimeMillis()
               userid = Database.getIns().nextid(db.auth.Accounts.getTable())
               account.userid = userid
               account.createtime = now
               db.auth.Accounts.insert(m.account, account)

               val user =  db.login.User.newBean()
               user.createtime = now
               db.login.Users.insert(userid, user)
               log.info("create account:{} userid:{}", m.account, userid)
           } else {
               userid = account.userid
           }
           Transaction.syncExecuteWhileCommit({
               GateClient.getIns().auth(m.session, m.account, userid);
           });
           return true
       }
   }
}