package com.example.demo

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@Service
class GzipRequestFilter : Filter {
    companion object {
        private const val GZIP_ENCODING = "gzip"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            val contentEncoding = request.getHeader(HttpHeaders.CONTENT_ENCODING)
            if (contentEncoding != null && contentEncoding.indexOf(GZIP_ENCODING) >= 0) {
                chain.doFilter(decompressedRequest(request), response)
                return
            }
        }
        chain.doFilter(request, response)
    }


    private fun decompressedRequest(httpServletRequest: HttpServletRequest): HttpServletRequestWrapper {
        val gzipInputStream = GZIPInputStream(httpServletRequest.inputStream)

        return object : HttpServletRequestWrapper(httpServletRequest) {
            override fun getInputStream() = DecompressedServletInputStream(gzipInputStream)

            override fun getReader() = BufferedReader(InputStreamReader(gzipInputStream))
        }
    }

    class DecompressedServletInputStream(private val inputStream: InputStream) : ServletInputStream() {
        override fun setReadListener(readListener: ReadListener) { }

        override fun isFinished() = inputStream.available() == 0

        override fun read() = inputStream.read()

        override fun isReady() = inputStream.available() > 0
    }
}
