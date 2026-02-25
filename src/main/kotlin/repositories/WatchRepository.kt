package org.delcom.repositories

import org.delcom.dao.WatchDAO
import org.delcom.entities.Watch
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.WatchTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class WatchRepository : IWatchRepository {
    override suspend fun getWatches(search: String): List<Watch> = suspendTransaction {
        if (search.isBlank()) {
            WatchDAO.all()
                .orderBy(WatchTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            WatchDAO
                .find { WatchTable.nama.lowerCase() like keyword }
                .orderBy(WatchTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getWatchById(id: String): Watch? = suspendTransaction {
        WatchDAO
            .find { (WatchTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getWatchByName(name: String): Watch? = suspendTransaction {
        WatchDAO
            .find { (WatchTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addWatch(watch: Watch): String = suspendTransaction {
        val watchDAO = WatchDAO.new {
            nama = watch.nama
            pathGambar = watch.pathGambar
            deskripsi = watch.deskripsi
            price = watch.price
            specs = watch.specs
            features = watch.features
            material = watch.material
            batteryLife = watch.batteryLife
            createdAt = watch.createdAt
            updatedAt = watch.updatedAt
        }
        watchDAO.id.value.toString()
    }

    override suspend fun updateWatch(id: String, newWatch: Watch): Boolean = suspendTransaction {
        val watchDAO = WatchDAO
            .find { WatchTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (watchDAO != null) {
            watchDAO.nama = newWatch.nama
            watchDAO.pathGambar = newWatch.pathGambar
            watchDAO.deskripsi = newWatch.deskripsi
            watchDAO.price = newWatch.price
            watchDAO.specs = newWatch.specs
            watchDAO.features = newWatch.features
            watchDAO.material = newWatch.material
            watchDAO.batteryLife = newWatch.batteryLife
            watchDAO.updatedAt = newWatch.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeWatch(id: String): Boolean = suspendTransaction {
        val rowsDeleted = WatchTable.deleteWhere {
            WatchTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}