package org.example.demohhclone


import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MeasureTime(
    val name: String = ""
)

@Aspect
@Component
class MeasureTimeAspect {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Around("@annotation(measureTime)")
    fun measureExecutionTime(joinPoint: ProceedingJoinPoint, measureTime: MeasureTime): Any? {
        val startTime = System.currentTimeMillis()

        try {
            return joinPoint.proceed()
        } finally {
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime

            val methodName = if (measureTime.name.isNotBlank()) {
                measureTime.name
            } else {
                val methodSignature = joinPoint.signature as MethodSignature
                "${methodSignature.declaringType.simpleName}.${methodSignature.name}()"
            }


            println("[$methodName] Execution time: ${executionTime}ms")
        }
    }

    @Around("@within(measureTime)")
    fun measureClassExecutionTime(joinPoint: ProceedingJoinPoint, measureTime: MeasureTime): Any? {
        return measureExecutionTime(joinPoint, measureTime)
    }
}