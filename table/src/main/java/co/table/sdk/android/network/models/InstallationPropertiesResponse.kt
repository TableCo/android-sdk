package co.table.sdk.android.network.models

import android.graphics.Color
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal class InstallationPropertiesResponse {

    @SerializedName("theme_overrides")
    @Expose
    var themeOverrides: ThemeOverrides? = null

}

internal class SemanticPalette {

    @SerializedName("primary")
    @Expose
    var primary: String? = null

    val asColor: Int?
        get() {
            if (primary == null) return null
            return Color.parseColor(primary)
        }

}

internal class ThemeOverrides {

    @SerializedName("semanticPalette")
    @Expose
    var semanticPalette: SemanticPalette? = null

}