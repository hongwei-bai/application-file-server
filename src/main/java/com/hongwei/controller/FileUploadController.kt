package com.hongwei.controller

import com.hongwei.constants.AppDataConfigurations
import com.hongwei.service.FileCompressService
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class FileUploadController {
    private val logger: Logger = LogManager.getLogger(FileUploadController::class.java)

    @Autowired
    private lateinit var appDataConfigurations: AppDataConfigurations

    @Autowired
    private lateinit var fileCompressService: FileCompressService

    @PostMapping("/upload.do")
    @ResponseBody
    fun singleFileUpload(@RequestParam("file") file: MultipartFile,
                         @RequestParam("path") path: String? = "",
                         redirectAttributes: RedirectAttributes): ResponseEntity<*> {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload")
            return ResponseEntity.ok("redirect:uploadStatus")
        }
        val pathRoot = appDataConfigurations.uploadExercisePath + path
        try { // Get the file and save it somewhere
            val bytes = file.bytes
            val pathString = pathRoot + File.separator + file.originalFilename
            val fileHandle = File(pathString)
            fileHandle.setReadable(true, false)
            fileHandle.setExecutable(true, false)
            fileHandle.setWritable(true, false)
            Files.write(Paths.get(pathString), bytes)
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.originalFilename + "'")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        fileCompressService.decompressTar(pathRoot, file.originalFilename)

        return ResponseEntity.ok("redirect:uploadStatus")
    }

    @GetMapping("/uploadStatus")
    fun uploadStatus(): String? {
        return "uploadStatus"
    }
}