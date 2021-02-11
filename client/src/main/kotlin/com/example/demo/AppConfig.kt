package com.example.demo

import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream


@Configuration
class AppConfig {
    private fun restTemplate(): RestTemplate {
        val httpClient = HttpClientBuilder.create().build()
        val clientHttpRequestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
        return RestTemplate(clientHttpRequestFactory)
    }

    @Bean
    fun defaultRestTemplate(): RestTemplate {
        return restTemplate()
    }

    @Bean
    fun gzipRestTemplate(): RestTemplate {
        return restTemplate().apply {
            interceptors.add(GzipClientHttpRequestInterceptor())
        }
    }
}

class GzipClientHttpRequestInterceptor : ClientHttpRequestInterceptor {
    companion object {
        private const val GZIP_ENCODING = "gzip"
    }

    private fun compress(body: ByteArray): ByteArray {
        val stream = ByteArrayOutputStream()
        GZIPOutputStream(stream).use { it.write(body) }
        return stream.toByteArray()
    }

    override fun intercept(request: HttpRequest, body: ByteArray, exec: ClientHttpRequestExecution): ClientHttpResponse {
        request.headers.add(HttpHeaders.CONTENT_ENCODING, GZIP_ENCODING)
        return exec.execute(request, compress(body))
    }
}
