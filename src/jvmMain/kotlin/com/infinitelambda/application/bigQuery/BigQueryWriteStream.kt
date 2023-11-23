package com.infinitelambda.application.bigQuery

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.cloud.bigquery.storage.v1.*
import com.google.cloud.bigquery.storage.v1.TableName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import java.util.concurrent.Phaser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BigQueryWriteStream(
    projectId: String,
    datasetName: String,
    tableName: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : AutoCloseable {

    private val client: BigQueryWriteClient = BigQueryWriteClient.create()
    private val writer: JsonStreamWriter
    private val inflightRequestCount: Phaser = Phaser(1)

    init {
        val stream = WriteStream.newBuilder().setType(WriteStream.Type.COMMITTED).build()

        val writeStream = client.createWriteStream(
            CreateWriteStreamRequest.newBuilder()
                .setParent(TableName.of(projectId, datasetName, tableName).toString())
                .setWriteStream(stream)
                .build()
        )

        writer = JsonStreamWriter.newBuilder(writeStream.name, writeStream.tableSchema, client).build()
    }

    suspend fun push(data: JSONArray): Unit = suspendCancellableCoroutine {
        val future = writer.append(data)
        ApiFutures.addCallback(future, object : ApiFutureCallback<AppendRowsResponse> {
            override fun onFailure(t: Throwable) {
                inflightRequestCount.arriveAndDeregister()
                it.resumeWithException(t)
            }

            override fun onSuccess(result: AppendRowsResponse) {
                inflightRequestCount.arriveAndDeregister()
                it.resume(Unit)
            }

        }, dispatcher.asExecutor())

        it.invokeOnCancellation { future.cancel(false) }

        inflightRequestCount.register()
    }

    override fun close() {
        inflightRequestCount.arriveAndAwaitAdvance()
        writer.close()
        client.finalizeWriteStream(writer.streamName)
    }


}