import android.content.Context
import androidx.compose.runtime.Composable
import com.everysight.evskit.EVS
import com.everysight.evskit.android.Evs

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = App()


fun initEvs(context: Context) {
    Evs.init(context)
}

fun configure() {
    EVS.instance().showUI("configure")
}