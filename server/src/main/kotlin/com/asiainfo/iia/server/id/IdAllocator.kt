package com.asiainfo.iia.server.id

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong
import org.apache.curator.retry.ExponentialBackoffRetry

/**
 * @author Jay Wu
 */
class IdAllocator(val segmentKey: Int,
                  val poolSize: Int,
                  client: CuratorFramework) : Registrar.Applicant<Int> {

    private val counter = DistributedAtomicLong(client, "/id_segment/$segmentKey", ExponentialBackoffRetry(1000, 0))

    private var currentSeq: Long = 1
    private var nextMaxSeq: Long = 0

    @Synchronized
    fun alloc(): Long? {
        if (currentSeq > nextMaxSeq) {
            val result = counter.add(poolSize.toLong())

            if (!result.succeeded()) {
                return null
            }

            nextMaxSeq = result.postValue()
            currentSeq = nextMaxSeq - poolSize + 1
        }

        return currentSeq++
    }

    override fun getKey() = segmentKey
}