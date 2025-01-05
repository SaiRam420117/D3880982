package uk.ac.tees.mad.cryptotracker.ui.coindetail

import androidx.compose.foundation.MarqueeDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import uk.ac.tees.mad.cryptotracker.data.database.WatchlistItem
import uk.ac.tees.mad.cryptotracker.models.CoinDetails
import uk.ac.tees.mad.cryptotracker.models.CoinResponseState
import uk.ac.tees.mad.cryptotracker.ui.home.ErrorScreen
import uk.ac.tees.mad.cryptotracker.ui.home.LoadingScreen
import uk.ac.tees.mad.cryptotracker.ui.theme.ProfitGreen
import uk.ac.tees.mad.cryptotracker.ui.watchlist.WatchlistViewModel
import java.util.Locale

@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    navController: NavHostController,
    viewModel: CryptoDetailsViewModel = viewModel()
) {
    val coinDetails by viewModel.coinDetails.collectAsState()

    LaunchedEffect(cryptoId) {
        viewModel.fetchCoinDetails(cryptoId)
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)  // Background from theme
    ) {
        when (coinDetails) {
            is CoinResponseState.Error -> ErrorScreen((coinDetails as CoinResponseState.Error).message) {
                viewModel.fetchCoinDetails(cryptoId)
            }

            is CoinResponseState.Success -> CoinDetailsContent(
                (coinDetails as CoinResponseState.Success).coins,
                navController
            )

            else -> LoadingScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailsContent(
    coin: CoinDetails,
    navController: NavHostController,
    watchlistViewModel: WatchlistViewModel = viewModel()
) {
    val isInWatchlist by watchlistViewModel.isCoinInWatchlist(coin.id)
        .collectAsState(initial = false)
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    var expandedDescription by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${coin.name} (${coin.symbol.uppercase()})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val watchlistItem = WatchlistItem(
                            id = coin.id,
                            name = coin.name,
                            symbol = coin.symbol,
                            imageUrl = coin.image.large,
                            currentPrice = coin.market_data.current_price["usd"] ?: 0.0
                        )
                        if (isInWatchlist) {
                            watchlistViewModel.removeFromWatchlist(watchlistItem)
                        } else {
                            watchlistViewModel.addToWatchlist(watchlistItem)
                        }
                    }) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (isInWatchlist) "Remove from Watchlist" else "Add to Watchlist",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
            )
        },
        containerColor = surfaceColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            PriceSection(coin)

            Spacer(modifier = Modifier.height(24.dp))

            MarketInfoSection(coin)

            Spacer(modifier = Modifier.height(24.dp))

            AdditionalDataSection(coin)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "About ${coin.name}",
                style = MaterialTheme.typography.titleMedium.copy(color = textColor),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (expandedDescription) coin.description.en else coin.description.en.take(
                    200
                ),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                overflow = TextOverflow.Ellipsis
            )
            TextButton(
                onClick = { expandedDescription = !expandedDescription },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (expandedDescription) "Show Less" else "Show More")
            }
        }
    }
}

@Composable
fun PriceSection(coin: CoinDetails) {
    Column {
        // Coin Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.image.large,
                contentDescription = "${coin.name} logo",
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "$${formatPrice(coin.market_data.current_price["usd"])}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Price Changes
        PriceChangeRow("24h", coin.market_data.price_change_percentage_24h)
        PriceChangeRow("7d", coin.market_data.price_change_percentage_7d)
        PriceChangeRow("30d", coin.market_data.price_change_percentage_30d)
    }
}

@Composable
fun MarketInfoSection(coin: CoinDetails) {
    Column {
        Text("Market Information", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {

            InfoCardWithIcon(
                title = "Market Cap",
                value = "$${formatLargeNumber(coin.market_data.market_cap["usd"] ?: 0.0)}",
                icon = Icons.Default.ShowChart,
                modifier = Modifier.weight(1f)
            )

            InfoCardWithIcon(
                title = "24h Trading Volume",
                value = "$${formatLargeNumber(coin.market_data.total_volume["usd"] ?: 0.0)}",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth()) {

            InfoCardWithIcon(
                title = "Market Cap Rank",
                value = "#${coin.market_cap_rank}",
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )

            InfoCardWithIcon(
                title = "Community Sentiment",
                value = "${coin.sentiment_votes_up_percentage}%",
                icon = Icons.Default.ThumbUp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AdditionalDataSection(coin: CoinDetails) {
    Column {
        Text("Additional Data", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {

            InfoCardWithIcon(
                title = "All-Time High",
                value = "$${formatPrice(coin.market_data.ath["usd"])}",
                icon = Icons.Default.ArrowUpward,
                modifier = Modifier.weight(1f)
            )

            InfoCardWithIcon(
                title = "All-Time Low",
                value = "$${formatPrice(coin.market_data.atl["usd"])}",
                icon = Icons.Default.ArrowDownward,
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth()) {


            InfoCardWithIcon(
                title = "Max Supply",
                value = coin.market_data.max_supply?.let { formatLargeNumber(it) } ?: "N/A",
                icon = Icons.Default.AllInbox,
                modifier = Modifier.weight(1f)
            )

            InfoCardWithIcon(
                title = "Circulating Supply",
                value = formatLargeNumber(coin.market_data.circulating_supply),
                icon = Icons.Default.Sync,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun InfoCardWithIcon(title: String, value: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun PriceChangeRow(period: String, change: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Price Change ($period)")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${String.format(Locale.getDefault(), "%.2f", change)}%",
                color = if (change >= 0) ProfitGreen else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (change >= 0) Icons.Filled.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (change >= 0) ProfitGreen else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun formatPrice(price: Double?): String {
    return String.format(Locale.getDefault(), "%.2f", price ?: 0.0)
}

fun formatLargeNumber(number: Double): String {
    return when {
        number >= 1_000_000_000 -> String.format(
            Locale.getDefault(),
            "%.2fB",
            number / 1_000_000_000
        )

        number >= 1_000_000 -> String.format(Locale.getDefault(), "%.2fM", number / 1_000_000)
        number >= 1_000 -> String.format(Locale.getDefault(), "%.2fK", number / 1_000)
        else -> String.format(Locale.getDefault(), "%.2f", number)
    }
}
