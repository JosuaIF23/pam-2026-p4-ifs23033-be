package org.delcom.repositories

import org.delcom.entities.Watch

interface IWatchRepository {
    suspend fun getWatches(search: String): List<Watch>
    suspend fun getWatchById(id: String): Watch?
    suspend fun getWatchByName(name: String): Watch?
    suspend fun addWatch(watch: Watch): String
    suspend fun updateWatch(id: String, newWatch: Watch): Boolean
    suspend fun removeWatch(id: String): Boolean
}