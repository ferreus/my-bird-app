import UIKit.app.Screen
import UIKit.app.data.EvsColor
import UIKit.app.interfaces.IEvsApp
import UIKit.services.AppErrorCode
import UIKit.services.IEvsAppEvents
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.everysight.evskit.EVS
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.BirdImage
import org.jetbrains.compose.resources.ExperimentalResourceApi

class HelloWorldScreen: Screen() {
    private val text = UIKit.widgets.Text()
    override fun onCreate() {

        text.setText("Hello World").setResource(UIKit.app.resources.Font.StockFont.Medium).setTextAlign(
            UIKit.app.data.Align.center)
        text.setX(getWidth()/2).setY(getHeight()/2).setForegroundColor(EvsColor.Green.rgba)
        add(text)
    }
}

val appEventListener = object  : IEvsAppEvents {
    override fun onError(errCode: AppErrorCode, description: String) {
    }

    override fun onReady() {
        EVS.instance().screens().addScreen(HelloWorldScreen())
    }

}

@Composable
fun BirdAppTheme(
    content: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        EVS.instance().registerAppEvents(appEventListener)
        with(EVS.instance().comm()){
            //registerCommunicationEvents(this@HelloWorldActivity)
            if(hasConfiguredDevice()) connect()
        }

    }
    MaterialTheme(
        colors = MaterialTheme.colors.copy(primary = Color.Black),
        shapes = MaterialTheme.shapes.copy(
            small = AbsoluteCutCornerShape(0.dp),
            medium = AbsoluteCutCornerShape(0.dp),
            large = AbsoluteCutCornerShape(0.dp)
        )
    ) {
        content()
    }
}

@Composable
fun App() {
    BirdAppTheme {
        val birdsViewModel = getViewModel(Unit, viewModelFactory { BirdsViewModel() })
        BirdsPage(birdsViewModel)
    }
}


@Composable
fun BirdsPage(viewModel: BirdsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for(category in uiState.categories) {
                Button(
                    onClick = {
                        viewModel.selectCategory(category)
                    },
                    modifier = Modifier.aspectRatio(1f).fillMaxSize().weight(1f),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, focusedElevation = 0.dp)
                ) {
                    Text(category)
                }
            }
            Button(
                onClick = {
                    EVS.instance().showUI("configure")
                }
            ) {
                Text("Configure")
            }
        }
        AnimatedVisibility(visible = uiState.selectedImages.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
            ) {
                items(uiState.selectedImages) {
                    BirdImageCell(it)
                }
            }
        }
    }
}

@Composable
fun BirdImageCell(image: BirdImage) {
    KamelImage(
        resource = asyncPainterResource("https://sebi.io/demo-image-api/${image.path}"),
        contentDescription = "${image.category} by ${image.author}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    )
}


expect fun getPlatformName(): String
