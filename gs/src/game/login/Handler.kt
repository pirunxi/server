package game.login

import common.IHandler
import common.IHandler.log
import common.UProcedure
import gate.GateClient
import msg.gs.login.*
import perfect.db.Database

/**
 * Created by HuangQiang on 2017/6/5.
 */
object Handler : IHandler {
    override fun bind() {
        CGetRoleList.handler = CGetRoleList.Handler { PCGetRoleList(it).execute() }
        CCreateRole.handler = CCreateRole.Handler { PCCreateRole(it).execute() }
        CRoleLogin.handler = CRoleLogin.Handler { PCRoleLogin(it).execute() }
    }

    class PCGetRoleList(m: CGetRoleList) : UProcedure<CGetRoleList>(m) {
        override fun process(): Boolean {
            val re = SGetRoleList()
            val user = db.login.Users.get(userid);
            for (roleid in user.roleids) {
                val role = db.login.Roles.get(roleid)
                val ri = RoleInfo()
                ri.roleid = roleid
                ri.name = role.name
                ri.gender = role.gender
                ri.profession = role.profession
                re.roles.add(ri)
            }
            response(re)
            return true
        }
    }


    class PCCreateRole(m: CCreateRole) : UProcedure<CCreateRole>(m) {
        override fun process(): Boolean {
            val m = message
            val existRoleid = db.login.Rolenames2id.get(m.name)
            if (existRoleid != null) return false

            val user = db.login.Users.get(userid)
            val roleids = user.roleids
            val newRoleid = Database.getIns().nextid(db.login.Roles.getTable())
            val role = db.login.Role.newBean()

            role.gender = m.gender
            role.name = m.name
            role.profession = m.profession
            role.createtime = System.currentTimeMillis()
            db.login.Roles.insert(newRoleid, role)
            roleids.add(newRoleid)
            log.info("create role. userid:{} roleid:{}", userid, newRoleid)

            val ri = RoleInfo()
            ri.roleid = newRoleid
            ri.name = m.name
            ri.gender = m.gender
            ri.profession = m.profession
            response(SCreateRole(ri))
            return true
        }
    }


    class PCRoleLogin(p: CRoleLogin) : UProcedure<CRoleLogin>(p) {
        override fun process(): Boolean {
            val roleid = message.roleid
            val user = db.login.Users.get(userid)
            if (!user.roleids.contains(roleid))
                return error("not user's role")
            val role = db.login.Roles.get(roleid) ?: return error("roleid not exist")
            game.event.Login(roleid).trigger()
            syncExecuteWhileCommit({
                GateClient.getIns().login(session, roleid)
                directlySend(SRoleLogin(roleid))
            })
            return true
        }
    }

}