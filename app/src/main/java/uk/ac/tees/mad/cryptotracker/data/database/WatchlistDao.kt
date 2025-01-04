package uk.ac.tees.mad.cryptotracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    fun getAllWatchlistItems(): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCoinToWatchlist(item: WatchlistItem)

    @Delete
    suspend fun removeCoinFromWatchlist(item: WatchlistItem)
}
