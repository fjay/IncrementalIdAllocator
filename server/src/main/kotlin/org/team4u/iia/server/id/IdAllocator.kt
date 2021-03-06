package org.team4u.iia.server.id

import cn.hutool.log.LogFactory
import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.KeeperException
import org.team4u.common.kotlin.extension.isNotEmpty
import org.team4u.iia.server.ApplicationErrorCode
import org.team4u.kit.core.codec.CodecRegistry
import org.team4u.kit.core.lang.WaitingTask
import org.team4u.kit.core.log.LogMessage
import java.util.concurrent.TimeUnit

/**
 * @author Jay Wu
 */
class IdAllocator(
    val segmentKey: Int,
    val poolSize: Int,
    val client: CuratorFramework
) : Registrar.Applicant<Int> {

    private val log = LogFactory.get()

    private val counter by lazy { LongGenerator("/id_segment/$segmentKey", 0) }

    private var currentSeq: Long = 1
    private var nextMaxSeq: Long = 0

    @Synchronized
    fun alloc(): Long? {
        if (currentSeq > nextMaxSeq) {
            try {
                nextMaxSeq = counter.add(poolSize)
                currentSeq = nextMaxSeq - poolSize + 1

            } catch (e: Exception) {
                log.error(
                    LogMessage(this.javaClass.simpleName, "alloc")
                        .fail()
                        .append("nodePath", counter.nodePath)
                        .append("currentSeq", currentSeq)
                        .toString(), e
                )
                return null
            }
        }

        return currentSeq++
    }

    override fun getKey() = segmentKey

    private inner class LongGenerator(val nodePath: String, val initValue: Long) {

        private val remoteValue = WaitingTask<Long>()
        private var currentValue: Long? = null

        @Synchronized
        fun add(step: Int): Long {
            if (currentValue == null) {
                initRemoteValue()
                currentValue = remoteValue.get(60, TimeUnit.SECONDS)
                ApplicationErrorCode.INIT_REMOTE_VALUE_TIMEOUT.isNotEmpty(currentValue)
            }

            currentValue = currentValue!! + step
            client.setData().forPath(nodePath, CodecRegistry.LONG_TO_BYTE_ARRAY_CODEC.encode(currentValue))
            return currentValue!!
        }

        private fun initRemoteValue() {
            client.sync().inBackground { client, event ->
                val data = try {
                    client.data.forPath(nodePath)
                } catch (e: KeeperException.NoNodeException) {
                    null
                } catch (e: Exception) {
                    log.error(
                        LogMessage(this.javaClass.simpleName, "initRemoteValue")
                            .fail()
                            .append("nodePath", nodePath)
                            .toString(), e
                    )
                    remoteValue.finish(null)
                    return@inBackground
                }

                remoteValue.finish(
                    if (data == null) {
                        client.create().creatingParentContainersIfNeeded().forPath(
                            nodePath,
                            CodecRegistry.LONG_TO_BYTE_ARRAY_CODEC.encode(initValue)
                        )
                        initValue
                    } else {
                        CodecRegistry.LONG_TO_BYTE_ARRAY_CODEC.decode(data)
                    }
                )
            }.forPath(nodePath)
        }
    }
}