package logger

import org.apache.commons.text.StringEscapeUtils
import perfect.common.Trace

class FormatLog(name : String) {
    private val sb = StringBuffer().append(""""type":"""").append(name).append("\"")

    fun param(key : String, value : Any) : FormatLog {
        sb.append(",\"").append(key).append("\":\"").append(value).append("\"")
        return this
    }

    fun param(key : String, value : String) : FormatLog {
        return param(key, StringEscapeUtils.escapeJson(value))
    }

    fun log() {
        Trace.log.info("{%s}", sb)
    }
}