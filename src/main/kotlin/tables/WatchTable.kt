package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object WatchTable : UUIDTable("watches") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val price = varchar("price", 50)
    val specs = text("specs")
    val features = text("features")
    val material = text("material")
    val batteryLife = varchar("battery_life", 100)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}