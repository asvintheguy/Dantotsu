package ani.dantotsu.media.manga

import android.util.Log
import ani.dantotsu.getImageDimensions
import ani.dantotsu.parsers.MangaChapter
import ani.dantotsu.parsers.MangaImage
import eu.kanade.tachiyomi.source.model.SChapter
import java.io.Serializable
import kotlin.math.floor

data class MangaChapter(
    val number: String,
    var link: String,
    var title: String? = null,
    var description: String? = null,
    var sChapter: SChapter,
) : Serializable {
    constructor(chapter: MangaChapter) : this(chapter.number, chapter.link, chapter.title, chapter.description, chapter.sChapter)

    private val images = mutableListOf<MangaImage>()
    fun images(): List<MangaImage> = images
    fun addImages(image: List<MangaImage>) {
        if (images.isNotEmpty()) return
        image.forEach { images.add(it) }

        var i = 0
        var curidx = 0
        while (i < images.size) {
            val dualPagePair = compareDualPages(images[i], images.getOrNull(i + 1))
            if (dualPagePair.second == null) {
                dualPages.add(dualPagePair)
                pageToIndex[curidx] = i
                i++
            }
            else {
                dualPages.add(dualPagePair)
                pageToIndex[curidx] = i
                pageToIndex[curidx] = i + 1
                i += 2
            }
            curidx++
        }

        Log.d("Dual Pages", dualPages.toString())
    }

    fun getIndexFromPage(page: Int): Int? {
        return pageToIndex[page]
    }

    private fun compareDualPages(img1: MangaImage, img2: MangaImage?): Pair<MangaImage, MangaImage?> {
        if (img2 == null)
            return (img1 to null)

        val dm1 = getImageDimensions(img1.url.url)
        val dm2 = getImageDimensions(img2.url.url)

        if (dm1 == null || dm1.first < dm1.second) {
            if (dm2 == null || dm2.first < dm2.second) {
                return (img1 to img2)
            }
        }

        return (img1 to null)
    }

    private val pageToIndex = mutableMapOf<Int, Int>()
    private val dualPages = mutableListOf<Pair<MangaImage, MangaImage?>>()
    fun dualPages(): List<Pair<MangaImage, MangaImage?>> = dualPages

}
