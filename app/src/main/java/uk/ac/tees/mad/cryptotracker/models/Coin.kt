package uk.ac.tees.mad.cryptotracker.models

import kotlinx.serialization.Serializable

@Serializable
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Long,
    val market_cap_rank: Int,
    val price_change_percentage_24h: Double
)

@Serializable
data class CoinDetails(
    val id: String,
    val symbol: String,
    val name: String,
    val image: Image,
    val market_data: MarketData,
    val description: Description,
    val market_cap_rank: Int,
    val sentiment_votes_up_percentage: Double
)

@Serializable
data class Image(
    val large: String
)

@Serializable
data class MarketData(
    val current_price: Map<String, Double>,
    val price_change_percentage_24h: Double,
    val price_change_percentage_7d: Double,
    val price_change_percentage_30d: Double,
    val market_cap: Map<String, Double>,
    val total_volume: Map<String, Double>,
    val ath: Map<String, Double>,
    val atl: Map<String, Double>,
    val max_supply: Double?,
    val circulating_supply: Double
)

@Serializable
data class Description(
    val en: String
)
