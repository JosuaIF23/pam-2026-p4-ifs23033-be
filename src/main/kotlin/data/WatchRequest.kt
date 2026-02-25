package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Watch

@Serializable
data class WatchRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var price: String = "",
    var specs: String = "",
    var features: String = "",
    var material: String = "",
    var batteryLife: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "price" to price,
            "specs" to specs,
            "features" to features,
            "material" to material,
            "batteryLife" to batteryLife,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Watch {
        return Watch(
            nama = nama,
            deskripsi = deskripsi,
            price = price,
            specs = specs,
            features = features,
            material = material,
            batteryLife = batteryLife,
            pathGambar = pathGambar,
        )
    }
}