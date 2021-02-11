package com.example.demo

import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI

@RestController
@RequestMapping("/demo/client")
class ClientController(private val defaultRestTemplate: RestTemplate,
                       private val gzipRestTemplate: RestTemplate) {
    data class Image (
            val title: String?,
            val value: String?
    )

    private fun exchange(restTemplate: RestTemplate): Image {
        val inputStream = ClassPathResource("image/sample.png").inputStream
        val base64EncodedImage = Base64.encodeBase64String(FileCopyUtils.copyToByteArray(inputStream))
        val request = Image("strawberry", base64EncodedImage)
        val uri = URI("http://localhost:8081/demo/server/echo")
        val entity = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
        return restTemplate.exchange(entity, Image::class.java).body ?: throw RuntimeException("empty response...")
    }

    @GetMapping("/non-compressive")
    fun default(): Image {
        return exchange(defaultRestTemplate)
    }

    @GetMapping("/compressive")
    fun compress(): Image {
        return exchange(gzipRestTemplate)
    }
}
