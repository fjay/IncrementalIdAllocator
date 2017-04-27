package com.asiainfo.iia.server.id

import com.asiainfo.common.util.Registrar
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong
import org.apache.curator.retry.ExponentialBackoffRetry

/**
 * @author Jay Wu
 */
class IdAllocator(val id: Int, val poolSize: Int, client: CuratorFramework) : Registrar.Applicant<Int> {

    private val counter = DistributedAtomicLong(client, "/nodeId/$id", ExponentialBackoffRetry(-1, 0))

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

    override fun getKey() = id
}