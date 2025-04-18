package com.example.jetsnack.widget.data

import androidx.annotation.DrawableRes
import androidx.glance.GlanceId
import com.example.jetsnack.R
import com.example.jetsnack.widget.layout.ImageTextListItemData
import com.example.jetsnack.widget.computeIfAbsent as computeIfAbsentExt
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

/**
 * A fake in-memory implementation of repository that produces list of [ImageTextListItemData]
 */
class FakeImageTextListDataRepository {
  private val data = MutableStateFlow(listOf<ImageTextListItemData>())
  private var items = demoItems.take(MAX_ITEMS)

  /**
   * Flow of [ImageTextListItemData]s that can be listened to during a Glance session.
   */
  fun data(): Flow<List<ImageTextListItemData>> = data

  /**
   * Reloads items (which due to shuffling) helps mimic a refresh
   */
  fun refresh() {
    val showData = Random.nextInt(50) < 5

    items = if (showData) {
        demoItems.take(MAX_ITEMS).shuffled()
    } else {
      listOf()
    }
    this.load()
  }

  /**
   * Loads the list of [ImageTextListItemData]s.
   */
  fun load(): List<ImageTextListItemData> {
    data.value = if (items.isNotEmpty()) {
      processImagesAndBuildData(items)
    } else {
      listOf()
    }

    return data.value
  }

  private fun processImagesAndBuildData(
    items: List<DemoDataItem>,
  ): List<ImageTextListItemData> {

    val mappedItems = runBlocking {
      items.map { item ->
          return@map ImageTextListItemData(
              key = item.key,
              title = item.title,
              supportingText = item.supportingText,
              supportingImage = item.supportingImage,
              trailingIconButton = R.drawable.add_shopping_cart,
              trailingIconButtonContentDescription = "Add to Shopping Cart"
          )
        }
    }

    return mappedItems
  }

  private data class DemoDataItem(
    val key: String,
    val title: String,
    val supportingText: String,
    @DrawableRes val supportingImage: Int,
    @DrawableRes val trailingIconButton: Int? = null,
    val trailingIconButtonContentDescription: String? = null,
  )

  companion object {
      private const val MAX_ITEMS = 10

      private val demoItems = listOf(
          DemoDataItem(
              key = "1",
              title = "Cupcakes",
              supportingText = "Cupcakes, almonds, bananas, apples",
              supportingImage = R.drawable.cupcake
          ),
          DemoDataItem(
              key = "1",
              title = "Donut",
              supportingText = "Donuts, milk, tea",
              supportingImage = R.drawable.donut
          ),
          DemoDataItem(
              key = "1",
              title = "Eclair",
              supportingText = "Eclairs, green tea, sugar",
              supportingImage = R.drawable.eclair
          ),
          DemoDataItem(
              key = "1",
              title = "Froyo",
              supportingText = "Froyo",
              supportingImage = R.drawable.froyo
          ),
          DemoDataItem(
              key = "1",
              title = "Gingerbread",
              supportingText = "Gingerbread",
              supportingImage = R.drawable.gingerbread
          )
      )

    private val repositories = mutableMapOf<GlanceId, FakeImageTextListDataRepository>()

    /**
     * Returns the repository instance for the given widget represented by [glanceId].
     */
    fun getImageTextListDataRepo(glanceId: GlanceId): FakeImageTextListDataRepository {
      return synchronized(repositories) {
        repositories.computeIfAbsentExt(glanceId) { FakeImageTextListDataRepository() }!!
      }
    }

    /**
     * Cleans up local data associated with the provided [glanceId].
     */
    fun cleanUp(glanceId: GlanceId) {
      synchronized(repositories) {
        repositories.remove(glanceId)
      }
    }
  }
}