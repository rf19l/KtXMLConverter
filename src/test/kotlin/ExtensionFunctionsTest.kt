import com.rf.foster.ktxml.toCamelCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtensionFunctionsTest {

    @Test
    fun `toCamelCase transforms underscore_separated_string to camelCase`() {
        val input = "underscore_separated_string"
        val expectedOutput = "underscoreSeparatedString"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings without underscores`() {
        val input = "nounderscores"
        val expectedOutput = "nounderscores"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with trailing underscores`() {
        val input = "trailing_"
        val expectedOutput = "trailing"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with leading underscores`() {
        val input = "_leading"
        val expectedOutput = "leading"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with multiple underscores`() {
        val input = "multiple__underscores"
        val expectedOutput = "multipleUnderscores"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with numbers`() {
        val input = "size_layout_01"
        val expectedOutput = "sizeLayout01"

        assertEquals(expectedOutput, input.toCamelCase())
    }
    @Test
    fun `toCamelCase transforms Uppercase_Underscore_Separated_String to camelCase`() {
        val input = "Uppercase_Underscore_Separated_String"
        val expectedOutput = "uppercaseUnderscoreSeparatedString"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase transforms Mixed_Upper_And_Lower_Case to camelCase`() {
        val input = "Mixed_Upper_And_Lower_Case"
        val expectedOutput = "mixedUpperAndLowerCase"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with mixed cases and numbers`() {
        val input = "Size_Layout_01"
        val expectedOutput = "sizeLayout01"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with mixed cases, numbers and multiple underscores`() {
        val input = "Size__Layout_02"
        val expectedOutput = "sizeLayout02"

        assertEquals(expectedOutput, input.toCamelCase())
    }
    @Test
    fun `toCamelCase handles strings with capitalized letters`() {
        val input = "textColorPrimary"
        val expectedOutput = "textColorPrimary"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with multiple capitalized letters`() {
        val input = "colorAccent"
        val expectedOutput = "colorAccent"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with capitalized letters and underscores`() {
        val input = "color_Primary_Dark"
        val expectedOutput = "colorPrimaryDark"

        assertEquals(expectedOutput, input.toCamelCase())
    }
    @Test
    fun `toCamelCase handles strings with dots`() {
        val input = "TextAppearance.AppCompat.Headline"
        val expectedOutput = "textAppearanceAppCompatHeadline"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with multiple dots`() {
        val input = "TextAppearance.AppCompat.Subhead"
        val expectedOutput = "textAppearanceAppCompatSubhead"

        assertEquals(expectedOutput, input.toCamelCase())
    }

    @Test
    fun `toCamelCase handles strings with dots and underscores`() {
        val input = "Text.Appearance_App.Compat_Body1"
        val expectedOutput = "textAppearanceAppCompatBody1"

        assertEquals(expectedOutput, input.toCamelCase())
    }
}
