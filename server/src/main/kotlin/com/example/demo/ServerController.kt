package com.example.demo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo/server")
class ServerController {
    private val logger = LoggerFactory.getLogger(this::class.java)

    data class Image (
            val title: String?,
            val value: String?
    )

    @PostMapping("/echo")
    fun default(@RequestBody image: Image): Image {
        logger.info("receive an image titled '${image.title}'")
        return image
    }
}
