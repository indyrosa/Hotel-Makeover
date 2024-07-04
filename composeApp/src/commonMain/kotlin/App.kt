import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.hintergrund
import idle_game.composeapp.generated.resources.muschel
import idle_game.composeapp.generated.resources.waterline
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel


@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {

    val CustomFontFamily = FontFamily(
        Font(Res.font.waterline, FontWeight.Normal),
    )

    Scaffold(
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }

            Image(
                painterResource(Res.drawable.hintergrund),
                contentDescription = "A square",
                modifier = Modifier.offset(

                ).width(40000.dp).height(1400.dp)
            )


            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    "Hotel Makeover",
                    style = MaterialTheme.typography.h1,
                    fontFamily = CustomFontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))



                gameState?.let { state ->
                    Text(
                        "Konto: ${currentMoney?.toHumanReadableString()} ",
                        style = MaterialTheme.typography.h4,
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(226, 122, 1), // Background color
                            contentColor = Color.White   // Text color
                        ),
                    ) {
                        Text("sammeln")
                    }

                    Image(
                        painterResource(Res.drawable.muschel),
                        contentDescription = "A square",
                        modifier = Modifier.offset(
                            x = 250.dp, y = -90.dp
                        ).width(45.dp).height(45.dp)
                    )


                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                    }
                }
                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(226, 122, 1), // Background color
                        contentColor = Color.White   // Text color
                    ),

                    ) {
                    Text("Spielstand zurücksetzen")
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(10.dp)
            .background(Color(255, 181, 127), RoundedCornerShape(8.dp))
            .padding(8.dp),
    ) {

        Column {
            if (gameJob.id == 1) {
                Text("Zimmer ${gameJob.id}")
            }
            if (gameJob.id == 2) {
                Text("Doppel Zimmer ${gameJob.id}")
            }
            if (gameJob.id == 3) {
                Text("Suite ${gameJob.id}")
            }
            if (gameJob.id == 4) {
                Text("Pool ${gameJob.id}")
            }
            if (gameJob.id == 5) {
                Text("Restaurant ${gameJob.id}")
            }
            Text("Level: ${gameJob.level.level}")
            Text("Kosten: ${gameJob.level.cost.toHumanReadableString()} Muscheln")
            Text("Gewinne: ${gameJob.level.earn.toHumanReadableString()} Muscheln")
            Text("Dauer: ${gameJob.level.duration.inWholeSeconds} Sekunden")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(226, 122, 1), // Background color
                    contentColor = Color.White   // Text color
                ),
            ) {

                Text("Kaufen")
            }
        } else {
            Text("Gekauft")
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(255, 164, 57), // Background color
                contentColor = Color.White   // Text color
            ),
        ) {
            Text("Upgraden")
        }
    }
}