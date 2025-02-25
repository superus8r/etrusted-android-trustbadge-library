/*
 * Created by Ali Kabiri on 30.1.2023.
 * Copyright (c) 2022-2023 Trusted Shops AG
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.etrusted.android.trustbadge.library.data.datasource

import com.etrusted.android.trustbadge.library.common.internal.IUrls
import com.etrusted.android.trustbadge.library.common.internal.Urls
import com.etrusted.android.trustbadge.library.common.internal.readStream
import com.etrusted.android.trustbadge.library.model.ChannelInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

internal interface IShopGradeDetailDatasource {
    suspend fun fetchShopGradeDetail(channelId: String, accessToken: String): Result<ChannelInfo>
}

@Suppress("BlockingMethodInNonBlockingContext")
internal class ShopGradeDetailDatasource(
    private val urls: IUrls = Urls,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): IShopGradeDetailDatasource {

    override suspend fun fetchShopGradeDetail(
        channelId: String,
        accessToken: String,
    ): Result<ChannelInfo> {

        return withContext(dispatcher) {

            val url = URL(urls.channelAggregateRatingUrl() +
                    "/$channelId/service-reviews/aggregate-rating")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Authorization", "Bearer $accessToken")

            try {

                val inputStream = BufferedInputStream(urlConnection.inputStream)
                val body = readStream(inputStream)
                val tBadgeData = ChannelInfo.fromString(body)
                Result.success(tBadgeData)

            } catch (e: Exception) {

                Result.failure(e)

            } finally {

                urlConnection.disconnect()
            }
        }
    }
}