package com.azamovme.soplay.tv.stv.parser

import com.azamovme.soplay.tv.stv.response.RandomTvResponse
import com.azamovme.soplay.utils.client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RandomTvData {
    val link =
        "http://tv.musical.uz//api/api.php?get_posts&page=1&count=85&api_key=cda11bx8aITlKsXCpNB7yVLnOdEGqg342ZFrQzJRetkSoUMi9w"

    suspend fun getRandomTvChannels(): RandomTvResponse = withContext(Dispatchers.IO) {

        val response = client.get(link)
        if (response.isSuccessful) {
            return@withContext response.parsed()
        }
        return@withContext RandomTvResponse()
    }

    //20...26
    suspend fun getDataByCategory(categoryId: Int) :RandomTvResponse = withContext(Dispatchers.IO){
        val categoryDataLink =
            "http://tv.musical.uz//api/api.php?get_category_posts=&id=${categoryId}&page=1&count=85&api_key=cda11bx8aITlKsXCpNB7yVLnOdEGqg342ZFrQzJRetkSoUMi9w"

        val response = client.get(categoryDataLink)
        if (response.isSuccessful){
            return@withContext response.parsed()
        }
        return@withContext  RandomTvResponse()

    }
}