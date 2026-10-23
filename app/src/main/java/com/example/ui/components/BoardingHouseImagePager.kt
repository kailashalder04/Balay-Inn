package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import com.example.ui.theme.SteelBlue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * Reusable Horizontal Image Pager component for Boarding House details.
 * Supports swiping through multiple property photos, indicators, chevrons,
 * and a full-screen high-resolution lightbox with a transparent lower thumbnail preview mode.
 */
@Composable
fun BoardingHouseImagePager(
    images: List<String>,
    propertyTitle: String,
    modifier: Modifier = Modifier,
    height: Dp = 500.dp,
    initialPage: Int = 0,
    roomType: String = "",
    onBack: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null,
    onReport: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val fallbackImages = remember {
        listOf(
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80"
        )
    }

    val displayImages = remember(images) {
        val validUrls = images.filter { it.isNotBlank() }
        if (validUrls.isNotEmpty()) validUrls else fallbackImages
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (displayImages.size - 1).coerceAtLeast(0)),
        pageCount = { displayImages.size }
    )

    var showFullScreenDialog by remember { mutableStateOf(false) }
    var fullScreenInitialPage by remember { mutableStateOf(0) }

    // --- Inline Horizontal Carousel ---
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Color.Black)
            .testTag("boarding_house_image_pager")
    ) {
        // Swipeable photo pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    fullScreenInitialPage = pagerState.currentPage
                    showFullScreenDialog = true
                }
                .testTag("boarding_house_horizontal_pager")
        ) { page ->
            AsyncImage(
                model = displayImages[page],
                contentDescription = "$propertyTitle photo ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top & bottom gradients for overlay contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        // Previous Chevron Button
        if (displayImages.size > 1 && pagerState.currentPage > 0) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .testTag("image_pager_prev_btn")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Photo",
                    tint = Color.White
                )
            }
        }

        // Next Chevron Button
        if (displayImages.size > 1 && pagerState.currentPage < displayImages.size - 1) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .testTag("image_pager_next_btn")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Photo",
                    tint = Color.White
                )
            }
        }

        // Top-left: Floating Back Button
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 12.dp, top = 10.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .testTag("detail_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Top-right Overlay: Three-dot menu on top, Heart icon directly below it
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 14.dp, top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Three-dot Overflow Menu (Share and Flag options)
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("detail_three_dots_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.testTag("detail_overflow_dropdown_menu")
                ) {
                    if (onShare != null) {
                        DropdownMenuItem(
                            text = { Text("Share", fontWeight = FontWeight.Medium) },
                            onClick = {
                                menuExpanded = false
                                onShare()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("menu_item_share")
                        )
                    }

                    if (onReport != null) {
                        DropdownMenuItem(
                            text = { Text("Flag", fontWeight = FontWeight.Medium, color = Color(0xFFD32F2F)) },
                            onClick = {
                                menuExpanded = false
                                onReport()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Flag,
                                    contentDescription = "Flag Listing",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("menu_item_flag")
                        )
                    }
                }
            }

            // 2. Heart Icon for Save/Favorite (below three-dots)
            if (onToggleSave != null) {
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("detail_heart_favorite_btn")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isSaved) "Remove from Saved" else "Save Property",
                        tint = if (isSaved) Color(0xFFE53935) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Floating Photo Count Badge (bottom-right: "1 / 5 Photos")
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.65f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clickable {
                    fullScreenInitialPage = pagerState.currentPage
                    showFullScreenDialog = true
                }
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${displayImages.size} Photos",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        // Dot indicators centered at bottom
        if (displayImages.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(displayImages.size) { index ->
                    val isCurrent = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(width = if (isCurrent) 16.dp else 6.dp, height = 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isCurrent) Color.White else Color.White.copy(alpha = 0.45f))
                    )
                }
            }
        }
    }

    // --- Full Screen Lightbox Modal ---
    if (showFullScreenDialog) {
        FullScreenImageLightbox(
            displayImages = displayImages,
            initialPage = fullScreenInitialPage,
            propertyTitle = propertyTitle,
            onDismiss = {
                // Sync inline carousel page with the last viewed full-screen page
                coroutineScope.launch {
                    pagerState.scrollToPage(fullScreenInitialPage)
                }
                showFullScreenDialog = false
            },
            onPageChange = { page ->
                fullScreenInitialPage = page
            }
        )
    }
}

/**
 * Full-screen lightbox viewer with pinch-to-zoom, gesture pan, and a transparent lower
 * list of thumbnail preview images so it blends seamlessly without harsh cutouts.
 */
@Composable
fun FullScreenImageLightbox(
    displayImages: List<String>,
    initialPage: Int,
    propertyTitle: String,
    onDismiss: () -> Unit,
    onPageChange: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lightboxPagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (displayImages.size - 1).coerceAtLeast(0)),
        pageCount = { displayImages.size }
    )

    LaunchedEffect(lightboxPagerState.currentPage) {
        onPageChange(lightboxPagerState.currentPage)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        var isZoomedIn by remember { mutableStateOf(false) }
        var showControls by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_lightbox_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Floating Header with Counter, Zoom Toggle, Share, and Close
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${lightboxPagerState.currentPage + 1} of ${displayImages.size}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Zoom Toggle
                            IconButton(
                                onClick = { isZoomedIn = !isZoomedIn },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (isZoomedIn) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                    contentDescription = if (isZoomedIn) "Zoom Out" else "Zoom In",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Share
                            IconButton(
                                onClick = {
                                    val currentUrl = displayImages.getOrNull(lightboxPagerState.currentPage) ?: ""
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, propertyTitle)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out this boarding house photo: $propertyTitle • $currentUrl"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Photo"))
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Close
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("lightbox_close_btn")
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close Lightbox",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Main Picture Area: centered with fixed standard zooming area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalPager(
                        state = lightboxPagerState,
                        userScrollEnabled = !isZoomedIn,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        var scale by remember(page) { mutableFloatStateOf(1f) }
                        var offset by remember(page) { mutableStateOf(Offset.Zero) }

                        LaunchedEffect(isZoomedIn) {
                            if (isZoomedIn) {
                                if (scale <= 1.02f) {
                                    scale = 2.5f
                                    offset = Offset.Zero
                                }
                            } else {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        }

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            val containerWidth = constraints.maxWidth.toFloat()
                            val containerHeight = constraints.maxHeight.toFloat()

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(page) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            val newScale = (scale * zoom).coerceIn(1f, 4.5f)
                                            scale = newScale
                                            if (newScale > 1.02f) {
                                                isZoomedIn = true
                                                // Maximum pan limit is strictly bounded so photo cannot move beyond the viewport
                                                val maxPanX = ((newScale - 1f) * containerWidth / 2f).coerceAtLeast(0f)
                                                val maxPanY = ((newScale - 1f) * containerHeight / 2f).coerceAtLeast(0f)
                                                offset = Offset(
                                                    x = (offset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                                                    y = (offset.y + pan.y).coerceIn(-maxPanY, maxPanY)
                                                )
                                            } else {
                                                // Standard zoom area is completely fixed: no movement allowed
                                                isZoomedIn = false
                                                offset = Offset.Zero
                                            }
                                        }
                                    }
                                    .pointerInput(page) {
                                        detectTapGestures(
                                            onTap = {
                                                showControls = !showControls
                                            },
                                            onDoubleTap = {
                                                if (scale > 1.05f || isZoomedIn) {
                                                    scale = 1f
                                                    offset = Offset.Zero
                                                    isZoomedIn = false
                                                } else {
                                                    scale = 2.5f
                                                    offset = Offset.Zero
                                                    isZoomedIn = true
                                                }
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = displayImages[page],
                                    contentDescription = "Full-Screen Photo ${page + 1}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offset.x,
                                            translationY = offset.y
                                        )
                                )
                            }
                        }
                    }

                    // Left Navigation Chevron
                    if (showControls && displayImages.size > 1 && lightboxPagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    lightboxPagerState.animateScrollToPage(lightboxPagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Photo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Right Navigation Chevron
                    if (showControls && displayImages.size > 1 && lightboxPagerState.currentPage < displayImages.size - 1) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    lightboxPagerState.animateScrollToPage(lightboxPagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Photo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // --- Lower list of snippet images, elevated cleanly ABOVE the bottom of the page ---
                if (displayImages.size > 1) {
                    AnimatedVisibility(
                        visible = showControls,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 68.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Floating translucent dock elevated comfortably above the page bottom
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color.Black.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.testTag("fullscreen_thumbnail_strip")
                            ) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    itemsIndexed(displayImages) { idx, url ->
                                        val isSelected = lightboxPagerState.currentPage == idx
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.Transparent,
                                            border = BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) SteelBlue else Color.White.copy(alpha = 0.40f)
                                            ),
                                            modifier = Modifier
                                                .size(width = 58.dp, height = 40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    coroutineScope.launch {
                                                        lightboxPagerState.animateScrollToPage(idx)
                                                    }
                                                }
                                        ) {
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Thumbnail ${idx + 1}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .graphicsLayer(alpha = if (isSelected) 1.0f else 0.60f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
