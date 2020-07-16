package com.young.weexbase.anno

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.xutils.common.util.LogUtil

/**
 * 切面日志
 */

@Aspect
open class LogAspect {
    companion object {
        private const val TAG = "WeexMethod_"
    }

    @Pointcut("execution(@com.berryee.fireadmin.anno.LogAsp * *(..))")
    fun weexMethodLog() {
    }

    /**
     * 打印weex日志，标注@LogAsp
     */
    @Throws
    @Before("weexMethodLog()")
    fun logBeforeWeexMethod(joinPoint: JoinPoint) {
        val method = joinPoint.signature as MethodSignature
        val mName = method.name
        LogUtil.d("$TAG name = $mName")
        fun paramNum(): (String) -> Unit {
            var index = 0
            return {
                LogUtil.d("$TAG param = $index -> $it")
                index += 1
            }
        }

        var logParam = paramNum()
        joinPoint.args.forEach {
            logParam(it.toString())
        }
    }
}