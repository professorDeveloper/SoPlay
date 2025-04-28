package com.azamovme.soplay.tv.stv.parser

import com.lagradost.nicehttp.Requests
import com.azamovme.soplay.tv.stv.response.channels_ontv.ChannelList
import com.azamovme.soplay.tv.stv.response.channels_ontv.Data
import com.azamovme.soplay.utils.Utils
import com.azamovme.soplay.utils.parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OntvParser {
    suspend fun getChannels(): List<Data> = withContext(Dispatchers.IO) {
        val niceHttp = Requests(baseClient = Utils.httpClient, responseParser = parser)
        val response = niceHttp.get(

            "https://api.ontv.uz/api/v2/channels?append=promotion&include=file&per_page=100&_f=json&_l=ru",
            mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
            ),
        ).parsed<ChannelList>()
        return@withContext response.data
    }
}