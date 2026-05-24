package com.rohitneel.todomaster.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.*

@Composable
fun OnboardingScreen(navController: NavHostController, taskViewModel: TaskViewModel) {
    val pages = listOf(OnBoardingModel.First, OnBoardingModel.Second, OnBoardingModel.Third, OnBoardingModel.Fourth, OnBoardingModel.Fifth)
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val (selectedPage, setSelectedPage) = remember { mutableStateOf(0) }
    
    LaunchedEffect(pagerState.currentPage) {
        setSelectedPage(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(0.5f)
        ) { page ->
            PagerScreen(onBoardingModel = pages[page])
        }
        HorizontalPagerIndicator(
            pages = pages,
            selectedPage = selectedPage
        )
        FinishButton(
            selectedPage = selectedPage,
            pages = pages,
            pagerState = pagerState,
            navController = navController,
            taskViewModel = taskViewModel
        )
    }
}

@Composable
fun FinishButton(
    selectedPage: Int,
    pages: List<OnBoardingModel>,
    pagerState: PagerState,
    navController: NavHostController,
    taskViewModel: TaskViewModel
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (selectedPage != pages.size - 1) {
            TextButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pages.size - 1)
                    }
                },
            ) {
                Text(
                    text = stringResource(Res.string.skip),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            Button(
                onClick = {
                    scope.launch {
                        val nextPage = selectedPage + 1
                        pagerState.animateScrollToPage(nextPage)
                    }
                },
            ) {
                Text(text = stringResource(Res.string.next))
            }
        } else {
            Button(
                onClick = {
                    taskViewModel.saveOnBoardingState(completed = true)
                    navController.popBackStack()
                    navController.navigate(NavDestinations.TaskDetail.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(text = stringResource(Res.string.get_started))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun PagerScreen(onBoardingModel: OnBoardingModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 200.dp) // Restrict the height of the image
                .padding(horizontal = 16.dp),
            painter = painterResource(onBoardingModel.image),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Text(
            modifier = Modifier
                .padding(top = 20.dp),
            text = onBoardingModel.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(top = 20.dp),
            text = onBoardingModel.description,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun HorizontalPagerIndicator(pages: List<OnBoardingModel>, selectedPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        for (i in pages.indices) {
            Box(
                modifier = Modifier
                    .padding(end = if (i == pages.size - 1) 0.dp else 5.dp)
                    .width(if (i == selectedPage) 20.dp else 10.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (i == selectedPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.1f
                        )
                    )
            )
        }
    }
}
