package com.infinitelambda.application

import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun cancelOnShutdown(
    context: CoroutineContext,
    timeout: Duration = 30.seconds,
    block: suspend CoroutineScope.() -> Unit
): Unit = runBlocking(context) {
    val job = launch(start = CoroutineStart.LAZY, block = block)
    val isShutdown = AtomicBoolean(false)

    val hook = Thread({
        isShutdown.set(true)

        val latch = CountDownLatch(1)
        suspend { job.cancelAndJoin() }
            .startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) {
                latch.countDown()
            })
        latch.await(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
    }, "Shutdown hook")

    Runtime.getRuntime().addShutdownHook(hook)

    job.start()
    job.join()

    if (!isShutdown.getAndSet(true)) {
        Runtime.getRuntime().removeShutdownHook(hook)
    }
}