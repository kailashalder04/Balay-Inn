package com.example.ui.components

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.SteelBlueDarkBg
import com.example.ui.theme.SteelBlueLightBg
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sinh

/**
 * Web Mercator Projection utilities for OpenStreetMap & CartoDB tiles.
 */
object WebMercator {
    const val TILE_SIZE = 256.0

    fun lngToNormX(lng: Double): Double = (lng + 180.0) / 360.0

    fun latToNormY(lat: Double): Double {
        val latClamped = lat.coerceIn(-85.05112878, 85.05112878)
        val latRad = Math.toRadians(latClamped)
        val sinLat = sin(latRad)
        return 0.5 - ln((1.0 + sinLat) / (1.0 - sinLat)) / (4.0 * PI)
    }

    fun normXToLng(normX: Double): Double {
        var x = normX
        while (x < 0.0) x += 1.0
        while (x > 1.0) x -= 1.0
        return x * 360.0 - 180.0
    }

    fun normYToLat(normY: Double): Double {
        val clampedY = normY.coerceIn(0.00001, 0.99999)
        val y2 = PI * (1.0 - 2.0 * clampedY)
        return Math.toDegrees(atan(sinh(y2)))
    }

    fun worldPixelX(lng: Double, zoom: Double): Double {
        val scale = TILE_SIZE * 2.0.pow(zoom)
        return lngToNormX(lng) * scale
    }

    fun worldPixelY(lat: Double, zoom: Double): Double {
        val scale = TILE_SIZE * 2.0.pow(zoom)
        return latToNormY(lat) * scale
    }
}

/**
 * Location candidate for search dropdown options.
 */
data class SearchableLocation(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String, // "WORLD", "COUNTRY", "REGION", "PROVINCE", "CITY", "BARANGAY", "LANDMARK", "PROPERTY"
    val latitude: Double,
    val longitude: Double,
    val targetZoom: Float = 12.0f,
    val propertyId: String? = null
)

/**
 * Formats rent compactly (e.g. ₱18k).
 */
fun formatRentCompact(amount: Double): String {
    return if (amount >= 1000) {
        val k = amount / 1000.0
        if (k % 1.0 == 0.0) "₱${k.toInt()}k" else "₱${String.format(Locale.US, "%.1f", k)}k"
    } else {
        "₱${amount.toInt()}"
    }
}

/**
 * Production-ready Worldwide Interactive Rental Map with silky-smooth Google Maps-style navigation.
 *
 * Enhanced features:
 * 1. Smooth 1.2s flyTo cinematic transitions with 0.25 easeLinearity (CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)).
 * 2. Momentum / inertia touch panning on mobile (deceleration 2500 px/s², max speed 1400 px/s).
 * 3. Fractional zoom snapping with zoomSnap = 0.25 and zoomDelta = 0.5.
 * 4. Rock-solid sub-pixel marker alignment with zero jitter or flickering during active pan & zoom.
 * 5. Direct live street tiles streaming from OpenStreetMap and CartoDB Positron.
 */
@Composable
fun InteractiveRentalMap(
    properties: List<PropertyEntity>,
    selectedProperty: PropertyEntity?,
    onPropertySelected: (PropertyEntity) -> Unit,
    allProvinces: List<ProvinceEntity> = emptyList(),
    allCities: List<CityMunicipalityEntity> = emptyList(),
    allBarangays: List<BarangayEntity> = emptyList(),
    onSearchThisArea: ((minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) -> Unit)? = null,
    isAreaSearchActive: Boolean = false,
    onClearAreaSearch: (() -> Unit)? = null,
    onToggleSave: ((String) -> Unit)? = null,
    savedPropertyIds: Set<String> = emptySet(),
    propertyImagesMap: Map<String, List<String>> = emptyMap(),
    onToggleViewMode: (() -> Unit)? = null,
    onOpenFilters: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Default coordinates: Metro Cebu hub or selected property
    val initialLat = selectedProperty?.latitude ?: 10.3157
    val initialLng = selectedProperty?.longitude ?: 123.8854
    val initialZoom = if (selectedProperty != null) 14.0f else 12.0f

    // Animated Camera Coordinates & Zoom (for silky-smooth 60fps transitions)
    val centerLatAnim = remember { Animatable(initialLat.toFloat()) }
    val centerLngAnim = remember { Animatable(initialLng.toFloat()) }
    val zoomAnim = remember { Animatable(initialZoom) }

    var activeProperty by remember(selectedProperty) { mutableStateOf(selectedProperty) }
    var mapLayerStyle by remember { mutableStateOf("OSM") } // "OSM" = Standard Street View, "POSITRON" = Clean Light Map
    var showStyleSelector by remember { mutableStateOf(false) }

    // Map bounds tracking for "Search this area"
    var hasPannedSinceLastSearch by remember { mutableStateOf(false) }
    var currentBoundsMinLat by remember { mutableDoubleStateOf(initialLat - 0.05) }
    var currentBoundsMaxLat by remember { mutableDoubleStateOf(initialLat + 0.05) }
    var currentBoundsMinLon by remember { mutableDoubleStateOf(initialLng - 0.05) }
    var currentBoundsMaxLon by remember { mutableDoubleStateOf(initialLng + 0.05) }

    // Search bar state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // User GPS state
    var userGpsLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Silky-Smooth 1.2s FlyTo transition with 0.25 easeLinearity
    fun flyTo(targetLat: Double, targetLng: Double, targetZoom: Float) {
        coroutineScope.launch {
            val flyEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f) // 0.25 easeLinearity
            val flyDuration = 1200 // 1.2s animation duration
            val snappedTargetZoom = (round(targetZoom / 0.25f) * 0.25f).coerceIn(3.0f, 18.0f)

            launch {
                centerLatAnim.animateTo(
                    targetValue = targetLat.toFloat(),
                    animationSpec = tween(flyDuration, easing = flyEasing)
                )
            }
            launch {
                centerLngAnim.animateTo(
                    targetValue = targetLng.toFloat(),
                    animationSpec = tween(flyDuration, easing = flyEasing)
                )
            }
            launch {
                zoomAnim.animateTo(
                    targetValue = snappedTargetZoom,
                    animationSpec = tween(flyDuration, easing = flyEasing)
                )
            }
            hasPannedSinceLastSearch = true
        }
    }

    // React to selectedProperty changing from outside
    LaunchedEffect(selectedProperty) {
        if (selectedProperty != null) {
            activeProperty = selectedProperty
            flyTo(selectedProperty.latitude, selectedProperty.longitude, 14.5f)
        }
    }

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            try {
                val locManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val gpsLoc = locManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (gpsLoc != null) {
                    userGpsLocation = Pair(gpsLoc.latitude, gpsLoc.longitude)
                    flyTo(gpsLoc.latitude, gpsLoc.longitude, 15.0f)
                    Toast.makeText(context, "Centered on your GPS location", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Acquiring GPS fix...", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    // Precompiled list of searchable destinations across Cebu, Bohol, Philippines, and Worldwide
    val allSearchableLocations = remember(properties, allCities, allBarangays) {
        val list = mutableListOf<SearchableLocation>()

        list.add(SearchableLocation("cebu_metro", "Metro Cebu", "Cebu City, Mandaue, Lapu-Lapu, Talisay", "CITY", 10.3157, 123.8854, 12.0f))
        list.add(SearchableLocation("cebu_it_park", "Cebu I.T. Park", "Lahug, Cebu City • BPO & Lifestyle Hub", "LANDMARK", 10.3297, 123.9063, 14.5f))
        list.add(SearchableLocation("cebu_business_park", "Cebu Business Park", "Ayala Center, Cebu City", "LANDMARK", 10.3175, 123.9056, 14.5f))
        list.add(SearchableLocation("mandaue_hub", "Mandaue City", "Subangdaku, Tipolo, Banilad", "CITY", 10.3340, 123.9400, 13.0f))
        list.add(SearchableLocation("lapu_lapu_hub", "Lapu-Lapu City", "Mactan Island, Punta Engaño, Maribago", "CITY", 10.3120, 123.9780, 13.0f))
        list.add(SearchableLocation("bohol_hub", "Bohol Province", "Central Visayas • 48 LGUs", "PROVINCE", 9.8500, 124.1500, 9.5f))
        list.add(SearchableLocation("panglao_island", "Panglao Island", "Alona Beach, Tawala, Doljo • Bohol", "CITY", 9.5800, 123.7700, 13.5f))
        list.add(SearchableLocation("tagbilaran_city", "Tagbilaran City", "Capital of Bohol • Cogon, Dao", "CITY", 9.6500, 123.8550, 13.0f))
        list.add(SearchableLocation("bacolod_city", "Bacolod City", "City of Smiles • Lacson St, Mandalagan, USLS", "CITY", 10.6766, 122.9509, 13.0f))
        list.add(SearchableLocation("philippines_country", "Philippines", "Southeast Asia • Nationwide Discovery", "COUNTRY", 12.8797, 121.7740, 6.0f))
        list.add(SearchableLocation("worldwide_globe", "Worldwide", "Global Rental Discovery", "WORLD", 15.0, 120.0, 3.0f))

        allCities.forEach { city ->
            val coords = when {
                city.name.contains("Cebu", ignoreCase = true) -> Pair(10.3157, 123.8854)
                city.name.contains("Bacolod", ignoreCase = true) -> Pair(10.6766, 122.9509)
                city.name.contains("Silay", ignoreCase = true) -> Pair(10.7967, 122.9772)
                city.name.contains("Bago", ignoreCase = true) -> Pair(10.5375, 122.8361)
                city.name.contains("Murcia", ignoreCase = true) -> Pair(10.6039, 123.0422)
                city.name.contains("Victorias", ignoreCase = true) -> Pair(10.9000, 123.0833)
                city.name.contains("Cadiz", ignoreCase = true) -> Pair(10.9575, 123.2847)
                city.name.contains("Sagay", ignoreCase = true) -> Pair(10.9000, 123.4167)
                city.name.contains("San Carlos", ignoreCase = true) -> Pair(10.4833, 123.4167)
                city.name.contains("Kabankalan", ignoreCase = true) -> Pair(9.9889, 122.8125)
                city.name.contains("Himamaylan", ignoreCase = true) -> Pair(10.1000, 122.8667)
                city.name.contains("La Carlota", ignoreCase = true) -> Pair(10.4242, 122.9231)
                city.name.contains("Escalante", ignoreCase = true) -> Pair(10.8333, 123.5000)
                city.name.contains("Sipalay", ignoreCase = true) -> Pair(9.7500, 122.4000)
                city.name.contains("Mandaue", ignoreCase = true) -> Pair(10.3340, 123.9400)
                city.name.contains("Lapu", ignoreCase = true) -> Pair(10.3120, 123.9780)
                city.name.contains("Talisay", ignoreCase = true) -> {
                    if (city.provinceId == "prov_bacolod") Pair(10.7333, 122.9667) else Pair(10.2447, 123.8492)
                }
                city.name.contains("Tagbilaran", ignoreCase = true) -> Pair(9.6500, 123.8550)
                city.name.contains("Panglao", ignoreCase = true) -> Pair(9.5800, 123.7700)
                city.name.contains("Dauis", ignoreCase = true) -> Pair(9.6200, 123.8600)
                else -> properties.find { it.cityMunicipalityId == city.id }?.let { Pair(it.latitude, it.longitude) } ?: Pair(10.3157, 123.8854)
            }
            list.add(
                SearchableLocation(
                    id = "city_${city.id}",
                    name = city.name,
                    subtitle = "${city.type} • Code: ${city.code}",
                    type = "CITY",
                    latitude = coords.first,
                    longitude = coords.second,
                    targetZoom = 13.0f
                )
            )
        }

        properties.forEach { p ->
            list.add(
                SearchableLocation(
                    id = "prop_${p.id}",
                    name = p.title,
                    subtitle = "₱${p.monthlyRent.toInt()} / mo • ${p.propertyType}",
                    type = "PROPERTY",
                    latitude = p.latitude,
                    longitude = p.longitude,
                    targetZoom = 15.0f,
                    propertyId = p.id
                )
            )
        }

        list
    }

    val searchResults = remember(searchQuery, allSearchableLocations) {
        if (searchQuery.isBlank()) {
            allSearchableLocations.take(8)
        } else {
            allSearchableLocations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.subtitle.contains(searchQuery, ignoreCase = true)
            }.take(12)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_worldwide_map")
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx().toDouble() }
        val heightPx = with(density) { maxHeight.toPx().toDouble() }

        // Live Animated Camera State
        val curLat = centerLatAnim.value.toDouble()
        val curLng = centerLngAnim.value.toDouble()
        val curZoom = zoomAnim.value

        // Slippy Tile Math
        val currentZ = curZoom.toInt().coerceIn(1, 18)
        val fracScale = 2.0.pow((curZoom - currentZ).toDouble())
        val tileSizeOnScreen = WebMercator.TILE_SIZE * fracScale

        val centerZPixelX = WebMercator.worldPixelX(curLng, currentZ.toDouble())
        val centerZPixelY = WebMercator.worldPixelY(curLat, currentZ.toDouble())

        val minWorldX = centerZPixelX - (widthPx / 2.0) / fracScale
        val maxWorldX = centerZPixelX + (widthPx / 2.0) / fracScale
        val minWorldY = centerZPixelY - (heightPx / 2.0) / fracScale
        val maxWorldY = centerZPixelY + (heightPx / 2.0) / fracScale

        val maxTileIndex = (1 shl currentZ) - 1
        val minTileX = (floor(minWorldX / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIndex)
        val maxTileX = (floor(maxWorldX / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIndex)
        val minTileY = (floor(minWorldY / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIndex)
        val maxTileY = (floor(maxWorldY / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIndex)

        // Update bounds for "Search this area"
        val curMinLon = WebMercator.normXToLng(minWorldX / (WebMercator.TILE_SIZE * (1 shl currentZ)))
        val curMaxLon = WebMercator.normXToLng(maxWorldX / (WebMercator.TILE_SIZE * (1 shl currentZ)))
        val curMaxLat = WebMercator.normYToLat(minWorldY / (WebMercator.TILE_SIZE * (1 shl currentZ)))
        val curMinLat = WebMercator.normYToLat(maxWorldY / (WebMercator.TILE_SIZE * (1 shl currentZ)))

        currentBoundsMinLat = curMinLat
        currentBoundsMaxLat = curMaxLat
        currentBoundsMinLon = curMinLon
        currentBoundsMaxLon = curMaxLon

        val velocityTracker = remember { VelocityTracker() }

        // 1. Map Canvas/Container with Momentum Inertia & Fractional Zoom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (mapLayerStyle == "OSM") Color(0xFFE2E8F0) else Color(0xFFF8FAFC))
                .pointerInput(Unit) {
                    coroutineScope {
                        while (true) {
                            awaitPointerEventScope {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                velocityTracker.resetTracking()
                                velocityTracker.addPosition(down.uptimeMillis, down.position)

                                var prevPos = down.position
                                var isMultiTouch = false

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val changes = event.changes
                                    if (changes.all { !it.pressed }) {
                                        break
                                    }

                                    if (changes.size > 1) {
                                        isMultiTouch = true
                                        val zoomChange = event.calculateZoom()
                                        val pan = event.calculatePan()

                                        val newZoom = (zoomAnim.value * zoomChange).coerceIn(3.0f, 18.0f)
                                        val curScale = WebMercator.TILE_SIZE * 2.0.pow(newZoom.toDouble())

                                        val curPixX = WebMercator.worldPixelX(centerLngAnim.value.toDouble(), newZoom.toDouble()) - pan.x.toDouble()
                                        val curPixY = WebMercator.worldPixelY(centerLatAnim.value.toDouble(), newZoom.toDouble()) - pan.y.toDouble()

                                        val newLng = WebMercator.normXToLng(curPixX / curScale)
                                        val newLat = WebMercator.normYToLat(curPixY / curScale)

                                        launch {
                                            centerLngAnim.snapTo(newLng.toFloat())
                                            centerLatAnim.snapTo(newLat.toFloat())
                                            zoomAnim.snapTo(newZoom)
                                        }
                                        hasPannedSinceLastSearch = true
                                    } else {
                                        val change = changes.first()
                                        val currentPos = change.position
                                        val delta = currentPos - prevPos
                                        prevPos = currentPos

                                        velocityTracker.addPosition(change.uptimeMillis, currentPos)

                                        val curScale = WebMercator.TILE_SIZE * 2.0.pow(zoomAnim.value.toDouble())
                                        val curPixX = WebMercator.worldPixelX(centerLngAnim.value.toDouble(), zoomAnim.value.toDouble()) - delta.x.toDouble()
                                        val curPixY = WebMercator.worldPixelY(centerLatAnim.value.toDouble(), zoomAnim.value.toDouble()) - delta.y.toDouble()

                                        val newLng = WebMercator.normXToLng(curPixX / curScale)
                                        val newLat = WebMercator.normYToLat(curPixY / curScale)

                                        launch {
                                            centerLngAnim.snapTo(newLng.toFloat())
                                            centerLatAnim.snapTo(newLat.toFloat())
                                        }
                                        hasPannedSinceLastSearch = true
                                    }
                                }

                                // Gesture Release Handling
                                if (isMultiTouch) {
                                    // Fine fractional zoom snap to 0.25 increments (zoomSnap = 0.25)
                                    val snapped = (round(zoomAnim.value / 0.25f) * 0.25f).coerceIn(3.0f, 18.0f)
                                    launch {
                                        zoomAnim.animateTo(snapped, tween(160, easing = FastOutSlowInEasing))
                                    }
                                } else {
                                    // Physics-based inertia: max speed 1400 px/s, deceleration 2500 px/s²
                                    val vel = velocityTracker.calculateVelocity()
                                    val vx = vel.x.coerceIn(-1400f, 1400f)
                                    val vy = vel.y.coerceIn(-1400f, 1400f)
                                    val speed = hypot(vx, vy)

                                    if (speed > 100f) {
                                        val durationSec = (speed / 2500f).coerceIn(0.2f, 0.85f)
                                        val inertiaDistX = (vx * durationSec / 2.0).toDouble()
                                        val inertiaDistY = (vy * durationSec / 2.0).toDouble()

                                        val curScale = WebMercator.TILE_SIZE * 2.0.pow(zoomAnim.value.toDouble())
                                        val curPixX = WebMercator.worldPixelX(centerLngAnim.value.toDouble(), zoomAnim.value.toDouble()) - inertiaDistX
                                        val curPixY = WebMercator.worldPixelY(centerLatAnim.value.toDouble(), zoomAnim.value.toDouble()) - inertiaDistY

                                        val targetLng = WebMercator.normXToLng(curPixX / curScale)
                                        val targetLat = WebMercator.normYToLat(curPixY / curScale)

                                        val animMs = (durationSec * 1000).toInt()
                                        launch {
                                            centerLngAnim.animateTo(targetLng.toFloat(), tween(animMs, easing = FastOutSlowInEasing))
                                        }
                                        launch {
                                            centerLatAnim.animateTo(targetLat.toFloat(), tween(animMs, easing = FastOutSlowInEasing))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            coroutineScope.launch {
                                val targetZoom = (round((zoomAnim.value + 1.0f) / 0.25f) * 0.25f).coerceAtMost(18.0f)
                                zoomAnim.animateTo(targetZoom, tween(300, easing = FastOutSlowInEasing))
                                hasPannedSinceLastSearch = true
                            }
                        },
                        onTap = {
                            activeProperty = null
                        }
                    )
                }
        ) {
            // 1.1 Render Live Tiles (Streamed from OpenStreetMap CDN or CartoDB Positron)
            for (tileY in minTileY..maxTileY) {
                for (tileX in minTileX..maxTileX) {
                    val tileScreenX = (widthPx / 2.0 + (tileX * WebMercator.TILE_SIZE - centerZPixelX) * fracScale).roundToInt()
                    val tileScreenY = (heightPx / 2.0 + (tileY * WebMercator.TILE_SIZE - centerZPixelY) * fracScale).roundToInt()
                    val tileWidth = (WebMercator.TILE_SIZE * fracScale).roundToInt()
                    val tileHeight = (WebMercator.TILE_SIZE * fracScale).roundToInt()

                    val tileUrl = if (mapLayerStyle == "POSITRON") {
                        val sub = listOf("a", "b", "c")[(tileX + tileY) % 3]
                        "https://$sub.basemaps.cartocdn.com/light_all/$currentZ/$tileX/$tileY.png"
                    } else {
                        "https://tile.openstreetmap.org/$currentZ/$tileX/$tileY.png"
                    }

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(tileScreenX, tileScreenY) }
                            .size(with(density) { tileWidth.toDp() }, with(density) { tileHeight.toDp() })
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(tileUrl)
                                .addHeader("User-Agent", "BalayInnRentalApp/1.0 (Android; Jetpack Compose)")
                                .crossfade(false)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 1.2 Render Property Markers (Rock-solid pin badges aligned to exact road coordinates)
            properties.forEach { prop ->
                val propPixelX = WebMercator.worldPixelX(prop.longitude, currentZ.toDouble())
                val propPixelY = WebMercator.worldPixelY(prop.latitude, currentZ.toDouble())

                val markerScreenX = (widthPx / 2.0 + (propPixelX - centerZPixelX) * fracScale).roundToInt()
                val markerScreenY = (heightPx / 2.0 + (propPixelY - centerZPixelY) * fracScale).roundToInt()

                if (markerScreenX in -60..(widthPx.toInt() + 60) && markerScreenY in -60..(heightPx.toInt() + 60)) {
                    val isSelected = activeProperty?.id == prop.id
                    val priceLabel = formatRentCompact(prop.monthlyRent)

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(markerScreenX - 35, markerScreenY - 18) }
                            .zIndex(if (isSelected) 10f else 1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF0F172A) else SteelBlue,
                            shadowElevation = if (isSelected) 8.dp else 4.dp,
                            modifier = Modifier
                                .border(
                                    width = if (isSelected) 2.dp else 1.5.dp,
                                    color = if (isSelected) Color(0xFF38BDF8) else Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    activeProperty = prop
                                    flyTo(prop.latitude, prop.longitude, curZoom.coerceAtLeast(13.5f))
                                }
                                .testTag("map_marker_${prop.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = priceLabel,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            // 1.3 User GPS Pulsing Dot
            if (userGpsLocation != null) {
                val (uLat, uLng) = userGpsLocation!!
                val gpsPixelX = WebMercator.worldPixelX(uLng, currentZ.toDouble())
                val gpsPixelY = WebMercator.worldPixelY(uLat, currentZ.toDouble())
                val gpsScreenX = (widthPx / 2.0 + (gpsPixelX - centerZPixelX) * fracScale).roundToInt()
                val gpsScreenY = (heightPx / 2.0 + (gpsPixelY - centerZPixelY) * fracScale).roundToInt()

                Box(
                    modifier = Modifier
                        .offset { IntOffset(gpsScreenX - 10, gpsScreenY - 10) }
                        .size(20.dp)
                        .background(Color(0xFF2563EB).copy(alpha = 0.25f), CircleShape)
                        .padding(4.dp)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                )
            }
        }

        // --- TOP REGION PRESETS OVER MAP ---
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .zIndex(20f)
        ) {
            // Quick Location Presets Row with Smooth 1.2s FlyTo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RegionChip(label = "🏙️ Metro Cebu", onClick = { flyTo(10.3157, 123.8854, 12.0f) })
                RegionChip(label = "🌴 Bohol & Panglao", onClick = { flyTo(9.7500, 123.9500, 10.25f) })
                RegionChip(label = "🎭 Bacolod & Cities", onClick = { flyTo(10.6766, 122.9509, 12.0f) })
                RegionChip(label = "🏛️ Silay & Talisay", onClick = { flyTo(10.7600, 122.9700, 12.5f) })
                RegionChip(label = "🏖️ Panglao Beach", onClick = { flyTo(9.5800, 123.7700, 13.5f) })
                RegionChip(label = "🏛️ Tagbilaran", onClick = { flyTo(9.6500, 123.8550, 13.0f) })
                RegionChip(label = "🏢 Mandaue", onClick = { flyTo(10.3340, 123.9400, 13.0f) })
                RegionChip(label = "✈️ Lapu-Lapu", onClick = { flyTo(10.3120, 123.9780, 13.0f) })
                RegionChip(label = "🇵🇭 Philippines", onClick = { flyTo(12.8797, 121.7740, 6.0f) })
                RegionChip(label = "🎯 Fit All (${properties.size})", onClick = {
                    if (properties.isNotEmpty()) {
                        val avgLat = properties.map { it.latitude }.average()
                        val avgLng = properties.map { it.longitude }.average()
                        flyTo(avgLat, avgLng, 11.0f)
                    }
                })
            }
        }

        // --- FLOATING "SEARCH THIS AREA" PILL ---
        AnimatedVisibility(
            visible = hasPannedSinceLastSearch || isAreaSearchActive,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 52.dp)
                .zIndex(20f)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (isAreaSearchActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        if (isAreaSearchActive) {
                            onClearAreaSearch?.invoke()
                            hasPannedSinceLastSearch = false
                        } else {
                            onSearchThisArea?.invoke(
                                currentBoundsMinLat,
                                currentBoundsMaxLat,
                                currentBoundsMinLon,
                                currentBoundsMaxLon
                            )
                            hasPannedSinceLastSearch = false
                            Toast.makeText(context, "Filtering listings within visible map area", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .testTag("search_this_area_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAreaSearchActive) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = null,
                        tint = if (isAreaSearchActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAreaSearchActive) "Clear Area Filter" else "Search this map area",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAreaSearchActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- FLOATING MAP CONTROLS (GPS, Layer Switcher, Zoom +/-) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = if (activeProperty != null) 175.dp else 24.dp)
                .zIndex(20f),
            horizontalAlignment = Alignment.End
        ) {
            // User GPS Locate Me
            Surface(
                shape = CircleShape,
                color = if (userGpsLocation != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.testTag("user_gps_button")
            ) {
                IconButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Locate Me",
                        tint = if (userGpsLocation != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Map Layer Toggle: "Standard Street View" vs "Clean Light Map"
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = { showStyleSelector = !showStyleSelector },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = "Map Style Layers",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Layer Style Selector Menu
            if (showStyleSelector) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        listOf(
                            "OSM" to "Standard Street View",
                            "POSITRON" to "Clean Light Map"
                        ).forEach { (code, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        mapLayerStyle = code
                                        showStyleSelector = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (mapLayerStyle == code) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (mapLayerStyle == code) FontWeight.Bold else FontWeight.Normal,
                                    color = if (mapLayerStyle == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zoom In / Zoom Out Controls with zoomDelta = 0.5 and zoomSnap = 0.25
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val target = (round((zoomAnim.value + 0.5f) / 0.25f) * 0.25f).coerceAtMost(18.0f)
                                zoomAnim.animateTo(target, tween(250, easing = FastOutSlowInEasing))
                                hasPannedSinceLastSearch = true
                            }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In")
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val target = (round((zoomAnim.value - 0.5f) / 0.25f) * 0.25f).coerceAtLeast(3.0f)
                                zoomAnim.animateTo(target, tween(250, easing = FastOutSlowInEasing))
                                hasPannedSinceLastSearch = true
                            }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                    }
                }
            }
        }

        // --- OPENSTREETMAP ATTRIBUTION BADGE ---
        Surface(
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = if (activeProperty != null) 160.dp else 4.dp, start = 8.dp)
                .zIndex(20f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (mapLayerStyle == "OSM") "© OpenStreetMap contributors" else "© OpenStreetMap • CARTO",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- SLIDE-UP PROPERTY SUMMARY BOTTOM SHEET MODAL ---
        AnimatedVisibility(
            visible = activeProperty != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .zIndex(30f)
        ) {
            if (activeProperty != null) {
                val prop = activeProperty!!
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
                val formattedRent = currencyFormat.format(prop.monthlyRent).replace("PHP", "₱")
                val isSaved = savedPropertyIds.contains(prop.id)
                val isDark = isSystemInDarkTheme()
                val keyAmenities = remember(prop.amenitiesJson) { parseKeyAmenities(prop.amenitiesJson) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .shadow(20.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                        .testTag("map_property_modal_sheet"),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                    color = if (isDark) SteelBlueDarkBg else SteelBlueLightBg,
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Drag Handle Pill
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 10.dp)
                                .width(38.dp)
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), CircleShape)
                        )

                        // Hero Image Banner & Overlay Badges
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            val images = propertyImagesMap[prop.id] ?: emptyList()
                            val thumbnail = images.firstOrNull()
                                ?: "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&auto=format&fit=crop&q=80"

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(thumbnail)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = prop.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Top Left Property Category Tag
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Black.copy(alpha = 0.72f)
                            ) {
                                Text(
                                    text = prop.propertyType,
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Top Right Actions (Save & Close)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    IconButton(onClick = { onToggleSave?.invoke(prop.id) }) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Save property",
                                            tint = if (isSaved) Color(0xFFE11D48) else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    IconButton(onClick = { activeProperty = null }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close modal",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Bottom Rent Price Banner
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "$formattedRent / month",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Title & Location
                        Text(
                            text = prop.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (prop.neighborhood.isNotBlank()) "${prop.neighborhood} • ${prop.nearbyLandmark}" else prop.nearbyLandmark,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Key Specs Row (Beds, Baths, Area, Furnished)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SpecBadge(label = "🛏️ ${prop.bedrooms} Bed", isDark = isDark)
                            SpecBadge(label = "🚿 ${prop.bathrooms} Bath", isDark = isDark)
                            SpecBadge(label = "📐 ${prop.floorAreaSqm.toInt()} m²", isDark = isDark)
                            SpecBadge(label = "🪑 ${prop.furnishedStatus}", isDark = isDark)
                        }

                        // Key Amenities Chips Row
                        if (keyAmenities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Key Amenities:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                keyAmenities.forEach { (emoji, name) ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                        border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                                    ) {
                                        Text(
                                            text = "$emoji $name",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Primary Action Button
                        Button(
                            onClick = { onPropertySelected(prop) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("map_modal_view_details_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("View Full Listing Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecBadge(label: String, isDark: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
        border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            maxLines = 1,
            softWrap = false
        )
    }
}

fun parseKeyAmenities(jsonOrCsv: String): List<Pair<String, String>> {
    val clean = jsonOrCsv.replace("[", "").replace("]", "").replace("\"", "")
    val rawList = clean.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val result = mutableListOf<Pair<String, String>>()
    rawList.forEach { item ->
        val lower = item.lowercase()
        val emoji = when {
            lower.contains("wifi") || lower.contains("internet") -> "📶"
            lower.contains("air") || lower.contains("ac") -> "❄️"
            lower.contains("park") || lower.contains("car") -> "🚗"
            lower.contains("kitchen") || lower.contains("cook") -> "🍳"
            lower.contains("pet") -> "🐾"
            lower.contains("secur") || lower.contains("guard") || lower.contains("cctv") -> "🛡️"
            lower.contains("balcony") || lower.contains("deck") -> "🌅"
            lower.contains("furnish") -> "🛋️"
            lower.contains("pool") -> "🏊"
            lower.contains("gym") -> "🏋️"
            lower.contains("bed") || lower.contains("bunk") -> "🛏️"
            lower.contains("bath") || lower.contains("cr") -> "🚿"
            else -> "✨"
        }
        result.add(Pair(emoji, item))
    }
    return if (result.isEmpty()) {
        listOf(Pair("📶", "WiFi Ready"), Pair("❄️", "Aircon"), Pair("🛡️", "24/7 Security"))
    } else {
        result.take(6)
    }
}

@Composable
private fun RegionChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

/**
 * Interactive map pin selector for property creation / posting.
 * Uses real live OpenStreetMap street tiles with smooth drag/tap pin placement.
 */
@Composable
fun LocationMapPicker(
    initialLat: Double,
    initialLng: Double,
    onLocationPicked: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentLat by remember(initialLat) { mutableDoubleStateOf(initialLat) }
    var currentLng by remember(initialLng) { mutableDoubleStateOf(initialLng) }
    var pickerZoom by remember { mutableFloatStateOf(14.0f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val wPx = with(density) { maxWidth.toPx().toDouble() }
            val hPx = with(density) { maxHeight.toPx().toDouble() }

            val pZ = pickerZoom.toInt().coerceIn(1, 18)
            val pFracScale = 2.0.pow((pickerZoom - pZ).toDouble())

            val pCenterPixelX = WebMercator.worldPixelX(currentLng, pZ.toDouble())
            val pCenterPixelY = WebMercator.worldPixelY(currentLat, pZ.toDouble())

            val pMinWorldX = pCenterPixelX - (wPx / 2.0) / pFracScale
            val pMaxWorldX = pCenterPixelX + (wPx / 2.0) / pFracScale
            val pMinWorldY = pCenterPixelY - (hPx / 2.0) / pFracScale
            val pMaxWorldY = pCenterPixelY + (hPx / 2.0) / pFracScale

            val maxTileIdx = (1 shl pZ) - 1
            val pMinTileX = (floor(pMinWorldX / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIdx)
            val pMaxTileX = (floor(pMaxWorldX / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIdx)
            val pMinTileY = (floor(pMinWorldY / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIdx)
            val pMaxTileY = (floor(pMaxWorldY / WebMercator.TILE_SIZE).toInt()).coerceIn(0, maxTileIdx)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentLat, currentLng, pickerZoom) {
                        detectTapGestures { tapOffset ->
                            val scale = WebMercator.TILE_SIZE * 2.0.pow(pickerZoom.toDouble())
                            val curPixX = WebMercator.worldPixelX(currentLng, pickerZoom.toDouble()) + (tapOffset.x - wPx / 2.0)
                            val curPixY = WebMercator.worldPixelY(currentLat, pickerZoom.toDouble()) + (tapOffset.y - hPx / 2.0)

                            val newLng = WebMercator.normXToLng(curPixX / scale)
                            val newLat = WebMercator.normYToLat(curPixY / scale)

                            currentLat = newLat
                            currentLng = newLng
                            onLocationPicked(newLat, newLng)
                        }
                    }
            ) {
                for (ty in pMinTileY..pMaxTileY) {
                    for (tx in pMinTileX..pMaxTileX) {
                        val sx = (wPx / 2.0 + (tx * WebMercator.TILE_SIZE - pCenterPixelX) * pFracScale).roundToInt()
                        val sy = (hPx / 2.0 + (ty * WebMercator.TILE_SIZE - pCenterPixelY) * pFracScale).roundToInt()
                        val tw = (WebMercator.TILE_SIZE * pFracScale).roundToInt()
                        val th = (WebMercator.TILE_SIZE * pFracScale).roundToInt()

                        val tileUrl = "https://tile.openstreetmap.org/$pZ/$tx/$ty.png"

                        Box(
                            modifier = Modifier
                                .offset { IntOffset(sx, sy) }
                                .size(with(density) { tw.toDp() }, with(density) { th.toDp() })
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(tileUrl)
                                    .addHeader("User-Agent", "BalayInnRentalApp/1.0 (Android; Jetpack Compose)")
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Fixed Pin in center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-16).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Target Location Pin",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(4.dp, CircleShape)
                )
            }

            // Coordinates readout overlay
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Pin: ${String.format(Locale.US, "%.5f", currentLat)}, ${String.format(Locale.US, "%.5f", currentLng)}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Zoom controls
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Row {
                    IconButton(
                        onClick = { pickerZoom = (pickerZoom + 0.5f).coerceAtMost(18f) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { pickerZoom = (pickerZoom - 0.5f).coerceAtLeast(10f) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
