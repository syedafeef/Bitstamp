package com.afeef.cryptoapi

import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset

class ApiAvailabilityTest {

    @Test
    @Throws(Exception::class)
    fun testAvailability() {
        val connection = URL("https://www.bitstamp.net/api/v2/ticker/btcusd").openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(InputStreamReader(response, Charset.defaultCharset())).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }
}