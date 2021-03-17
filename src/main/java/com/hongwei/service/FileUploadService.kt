package com.hongwei.service

import com.hongwei.util.CompressUtils
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileUploadService {
    fun testLocalOnWin() {
        CompressUtils.unTar(File("D:\\test\\bbbb.tar"), File("D:\\test\\bbbb_untarred"))
    }
}