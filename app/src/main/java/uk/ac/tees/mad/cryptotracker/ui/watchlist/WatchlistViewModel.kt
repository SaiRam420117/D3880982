package uk.ac.tees.mad.cryptotracker.ui.watchlist

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.ac.tees.mad.cryptotracker.data.database.WatchlistDao
import uk.ac.tees.mad.cryptotracker.data.database.WatchlistDatabase
import uk.ac.tees.mad.cryptotracker.data.database.WatchlistItem

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {
    private val watchlistDao: WatchlistDao =
        WatchlistDatabase.getDatabase(application).watchlistDao()

    val watchlist: Flow<List<WatchlistItem>> = watchlistDao.getAllWatchlistItems()

    fun addToWatchlist(coin: WatchlistItem) {
        viewModelScope.launch {
            watchlistDao.addCoinToWatchlist(coin)
        }.invokeOnCompletion {
            Toast.makeText(
                getApplication(),
                "${coin.symbol.uppercase()} added to watchlist",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    fun removeFromWatchlist(coin: WatchlistItem) {
        viewModelScope.launch {
            watchlistDao.removeCoinFromWatchlist(coin)
        }.invokeOnCompletion {
            Toast.makeText(getApplication(), "Removed from watchlist", Toast.LENGTH_SHORT).show()
        }
    }

    fun isCoinInWatchlist(coinId: String): Flow<Boolean> {
        return watchlist.map { list ->
            list.any { it.id == coinId }
        }
    }
}
