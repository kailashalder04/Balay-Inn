package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Balay-Inn", appName)
  }

  @Test
  fun `test dark mode toggle state`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vm = com.example.ui.viewmodel.MarketplaceViewModel(app)
    val initial = vm.isDarkMode.value
    vm.toggleDarkMode()
    assertEquals(!initial, vm.isDarkMode.value)
    vm.setDarkMode(initial)
    assertEquals(initial, vm.isDarkMode.value)
  }
}
