package uk.ac.tees.mad.cryptotracker.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.tees.mad.cryptotracker.models.Coin
import uk.ac.tees.mad.cryptotracker.models.CoinDetails

interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<Coin>

    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String
    ): CoinDetails
}