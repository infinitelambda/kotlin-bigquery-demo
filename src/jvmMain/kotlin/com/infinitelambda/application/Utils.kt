package com.infinitelambda.application

import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
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

@OptIn(ExperimentalContracts::class)
@Suppress("SUBTYPING_BETWEEN_CONTEXT_RECEIVERS") // should be fine as long as T and U are not a part of the same hierarchy
inline fun <T, U, R> with(
    receiver1: T,
    receiver2: U,
    block: context(T, U) (TypePlacedHolder<U>) -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(receiver1, receiver2, TypePlacedHolder)
}

// Work-around for bug with context receiver lambda
sealed interface TypePlacedHolder<out A> {
    companion object : TypePlacedHolder<Nothing>
}