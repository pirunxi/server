package common

import gate.ClientSession
import msg.gs.common.SError
import msg.gs.common.SError3
import perfect.io.Message
import perfect.txn.Transaction

/**
 * Created by HuangQiang on 2017/6/5.
 */

val Message.session  : ClientSession get() = this.context as ClientSession

fun Message.response(reply: Message) {
    Transaction.syncExecuteWhileCommit { this.session.send(reply) }
}

fun Message.responseDirecly(reply: Message) {
    this.session.send(reply)
}

fun Message.getRoleid() : Long = this.session.roleid
fun Message.getUserid() : String = this.session.account;

fun Message.err(err : Int) : Boolean {
    val session = this.context as ClientSession
    session.send(SError(err))
    return false
}

fun Message.err(err : String) : Boolean {
    val session = this.context as ClientSession
    session.send(SError3(err))
    return false
}
