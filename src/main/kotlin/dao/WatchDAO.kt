package org.delcom.dao

import org.delcom.tables.WatchTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class WatchDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, WatchDAO>(WatchTable)

    var nama by WatchTable.nama
    var pathGambar by WatchTable.pathGambar
    var deskripsi by WatchTable.deskripsi
    var price by WatchTable.price
    var specs by WatchTable.specs
    var features by WatchTable.features
    var material by WatchTable.material
    var batteryLife by WatchTable.batteryLife
    var createdAt by WatchTable.createdAt
    var updatedAt by WatchTable.updatedAt
}