package game.login

import annotations.Handler
import common.*
import common.IHandler.log
import gate.GateClient
import msg.gs.login.*
import perfect.db.Database
import perfect.txn.Transaction

/**
 * Created by HuangQiang on 2017/6/5.
 */
@Handler
object Handler : IHandler {
    override fun bind() {
        CGetRoleList.handler = CGetRoleList.Handler { PCGetRoleList(it).execute() }
        CCreateRole.handler = CCreateRole.Handler { PCCreateRole(it).execute() }
        CRoleLogin.handler = CRoleLogin.Handler { PCRoleLogin(it).execute() }
    }

    class PCGetRoleList(val msg: CGetRoleList) : perfect.txn.Procedure() {
        override fun process(): Boolean {
            val re = SGetRoleList()
            val user = db.auth.Accounts.get(msg.getUserid())
            for (roleid in user.roleids) {
                val role = db.login.Roles.get(roleid)
                val ri = RoleInfo()
                ri.roleid = roleid
                ri.name = role.name
                ri.gender = role.gender
                ri.profession = role.profession
                re.roles.add(ri)
            }
            msg.response(re)
            return true
        }
    }


    class PCCreateRole(val msg: CCreateRole) : perfect.txn.Procedure() {
        override fun process(): Boolean {
            val existRoleid = db.login.Rolenames2id.get(msg.name)
            if (existRoleid != null) return false
            val userid = msg.getUserid()
            val user = db.auth.Accounts.get(userid)
            val roleids = user.roleids
            val newRoleid = Database.getIns().nextid(db.login.Roles.getTable())
            val role = db.login.Role.newBean()

            role.gender = msg.gender
            role.name = msg.name
            role.profession = msg.profession
            role.createtime = System.currentTimeMillis()
            db.login.Roles.insert(newRoleid, role)
            roleids.add(newRoleid)
            log.info("create role. userid:{} roleid:{}", userid, newRoleid)

            val ri = RoleInfo()
            ri.roleid = newRoleid
            ri.name = msg.name
            ri.gender = msg.gender
            ri.profession = msg.profession
            msg.response(SCreateRole(ri))
            return true
        }
    }


    class PCRoleLogin(val msg: CRoleLogin) : perfect.txn.Procedure() {
        override fun process(): Boolean {
            val roleid = msg.roleid
            val userid = msg.getUserid()
            val user = db.auth.Accounts.get(userid)
            if (!user.roleids.contains(roleid))
                return msg.err("not user's role")
            val role = db.login.Roles.get(roleid) ?: return msg.err("roleid not exist")
            game.event.Login(roleid).trigger()
            Transaction.syncExecuteWhileCommit{
                GateClient.getIns().login(msg.session, roleid)
                msg.responseDirecly(SRoleLogin(roleid))
            }
            return true
        }
    }

}