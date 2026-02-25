package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.WatchRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IWatchRepository
import java.io.File
import java.util.*

class WatchService(private val watchRepository: IWatchRepository) {

    suspend fun getAllWatches(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val watches = watchRepository.getWatches(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar jam tangan",
            mapOf(Pair("watches", watches))
        )
        call.respond(response)
    }

    suspend fun getWatchById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val watch = watchRepository.getWatchById(id) ?: throw AppException(404, "Data jam tidak tersedia!")

        val response = DataResponse("success", "Berhasil mengambil data jam", mapOf(Pair("watch", watch)))
        call.respond(response)
    }

    private suspend fun getWatchRequest(call: ApplicationCall): WatchRequest {
        val watchReq = WatchRequest()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> watchReq.nama = part.value.trim()
                        "deskripsi" -> watchReq.deskripsi = part.value
                        "price" -> watchReq.price = part.value
                        "specs" -> watchReq.specs = part.value
                        "features" -> watchReq.features = part.value
                        "material" -> watchReq.material = part.value
                        "batteryLife" -> watchReq.batteryLife = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/watches/$fileName" // Simpan di folder khusus jam tangan

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    watchReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return watchReq
    }

    private fun validateWatchRequest(watchReq: WatchRequest) {
        val validatorHelper = ValidatorHelper(watchReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("price", "Harga tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(watchReq.pathGambar)
        if (!file.exists()) throw AppException(400, "Gambar gagal diupload!")
    }

    suspend fun createWatch(call: ApplicationCall) {
        val watchReq = getWatchRequest(call)
        validateWatchRequest(watchReq)

        val existWatch = watchRepository.getWatchByName(watchReq.nama)
        if (existWatch != null) {
            val tmpFile = File(watchReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Jam tangan dengan nama ini sudah terdaftar!")
        }

        val watchId = watchRepository.addWatch(watchReq.toEntity())
        call.respond(DataResponse("success", "Berhasil menambah jam tangan", mapOf(Pair("watchId", watchId))))
    }

    suspend fun updateWatch(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val oldWatch = watchRepository.getWatchById(id) ?: throw AppException(404, "Data jam tidak tersedia!")

        val watchReq = getWatchRequest(call)
        if (watchReq.pathGambar.isEmpty()) watchReq.pathGambar = oldWatch.pathGambar

        validateWatchRequest(watchReq)

        if (watchReq.nama != oldWatch.nama) {
            val existWatch = watchRepository.getWatchByName(watchReq.nama)
            if (existWatch != null) {
                val tmpFile = File(watchReq.pathGambar)
                if (tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Jam dengan nama ini sudah terdaftar!")
            }
        }

        if (watchReq.pathGambar != oldWatch.pathGambar) {
            val oldFile = File(oldWatch.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = watchRepository.updateWatch(id, watchReq.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data jam!")

        call.respond(DataResponse("success", "Berhasil mengubah data jam", null))
    }

    suspend fun deleteWatch(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val oldWatch = watchRepository.getWatchById(id) ?: throw AppException(404, "Data jam tidak tersedia!")

        val isDeleted = watchRepository.removeWatch(id)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data jam!")

        val oldFile = File(oldWatch.pathGambar)
        if (oldFile.exists()) oldFile.delete()

        call.respond(DataResponse("success", "Berhasil menghapus data jam", null))
    }

    suspend fun getWatchImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val watch = watchRepository.getWatchById(id) ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(watch.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)

        call.respondFile(file)
    }
}