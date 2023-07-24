import com.rf.foster.ktxml.models.kotlin_resource.KotlinColorResource
import com.rf.foster.ktxml.models.kotlin_resource.KotlinDimenResource
import com.rf.foster.ktxml.mappers.KotlinFileBuilder
import org.gradle.internal.impldep.junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class KotlinFileBuilderTest {
    companion object {
        const val packageName = "com.example.package"
        const val projectName = "ProjectName"

        val expectedColors = """
        package $packageName

        import androidx.compose.ui.graphics.Color

        object ${projectName}Colors {
            val colorPrimary = Color(0xFF3F51B5)
            val colorPrimaryDark = Color(0xFF303F9F)
            val colorAccent = Color(0xFFFF4081)
            val textColorPrimary = Color(0xFF212121)
            val textColorSecondary = Color(0xFF757575)
            val dividerColor = Color(0xFFBDBDBD)
        }
        """.trimIndent()

        val kotlinColorResourcesInput: List<KotlinColorResource> = listOf(
            KotlinColorResource(name = "colorPrimary", value = "Color(0xFF3F51B5)"),
            KotlinColorResource(name = "colorPrimaryDark", value = "Color(0xFF303F9F)"),
            KotlinColorResource(name = "colorAccent", value = "Color(0xFFFF4081)"),
            KotlinColorResource(name = "textColorPrimary", value = "Color(0xFF212121)"),
            KotlinColorResource(name = "textColorSecondary", value = "Color(0xFF757575)"),
            KotlinColorResource(name = "dividerColor", value = "Color(0xFFBDBDBD)")
        )

        val expectedDimens = """
        package $packageName

        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.unit.sp

        object ${projectName}Dimens {
            val activityHorizontalMargin = 16.dp
            val activityVerticalMargin = 12.dp
            val appbarPadding = 8.dp
            val fabMargin = 16.dp
            val textSizeSmall = 12.sp
            val textSizeMedium = 16.sp
            val textSizeLarge = 20.sp
            val unitlessMargin = 10f
        }
        """.trimIndent()

        val kotlinDimensResourcesInput: List<KotlinDimenResource> = listOf(
            KotlinDimenResource(name = "activityHorizontalMargin", value = "16", unit = ".dp"),
            KotlinDimenResource(name = "activityVerticalMargin", value = "12", unit = ".dp"),
            KotlinDimenResource(name = "appbarPadding", value = "8", unit = ".dp"),
            KotlinDimenResource(name = "fabMargin", value = "16", unit = ".dp"),
            KotlinDimenResource(name = "textSizeSmall", value = "12", unit = ".sp"),
            KotlinDimenResource(name = "textSizeMedium", value = "16", unit = ".sp"),
            KotlinDimenResource(name = "textSizeLarge", value = "20", unit = ".sp"),
            KotlinDimenResource(name = "unitlessMargin", value = "10", unit = "f")
        )
    }

    private val builder = KotlinFileBuilder()

    @Test
    fun `test buildColors`() {
        val result = builder.buildColors(packageName, projectName, kotlinColorResourcesInput)
        assertEquals(expectedColors,result.trim())
    }

    @Test
    fun `test buildDimens`() {
        val result = builder.buildDimens(packageName, projectName, kotlinDimensResourcesInput)
       assertEquals(expectedDimens,result.trim(),)
    }
}
