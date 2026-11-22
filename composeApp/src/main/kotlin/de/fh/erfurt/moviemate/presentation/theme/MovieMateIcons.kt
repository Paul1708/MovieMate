package de.fh.erfurt.moviemate.presentation.theme

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import de.fh.erfurt.moviemate.R

/**
 * Rounded Material Symbols used by the app.
 *
 * Keeping the resource mapping in one place gives every screen the same icon
 * style without coupling render components to individual drawable names.
 */
object MovieMateIcons {
    @DrawableRes val add = R.drawable.ic_add_24
    @DrawableRes val addCircle = R.drawable.ic_add_circle_24
    @DrawableRes val back = R.drawable.ic_arrow_back_24
    @DrawableRes val backup = R.drawable.ic_backup_24
    @DrawableRes val calendar = R.drawable.ic_calendar_month_24
    @DrawableRes val category = R.drawable.ic_category_24
    @DrawableRes val chevronRight = R.drawable.ic_chevron_right_24
    @DrawableRes val close = R.drawable.ic_close_24
    @DrawableRes val delete = R.drawable.ic_delete_24
    @DrawableRes val download = R.drawable.ic_download_24
    @DrawableRes val edit = R.drawable.ic_edit_24
    @DrawableRes val home = R.drawable.ic_home_24
    @DrawableRes val image = R.drawable.ic_image_24
    @DrawableRes val rating = R.drawable.ic_star_24
    @DrawableRes val runtime = R.drawable.ic_schedule_24
    @DrawableRes val search = R.drawable.ic_search_24
    @DrawableRes val filter = R.drawable.ic_tune_24
    @DrawableRes val uploadFile = R.drawable.ic_upload_file_24
    @DrawableRes val watched = R.drawable.ic_check_circle_24
}

@Composable
fun MovieMateIcon(
    @DrawableRes icon: Int,
    tint: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
