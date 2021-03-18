package com.hongwei.service

import com.hongwei.util.CompressUtils
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileCompressService {
    fun decompressTar(path: String, file: String?) {
        if (file?.contains(".") == true) {
            val ext = file.split(".").last()
            if (ext.equals("tar", true)) {
                val filename = file.replace(".$ext", "")
                val tar = path + File.separator + file
                val output = path + File.separator + filename
                CompressUtils.unTar(
                        File(tar),
                        File(output)
                )
            }
        }
    }
}