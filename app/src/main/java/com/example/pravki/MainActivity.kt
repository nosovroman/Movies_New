package com.example.pravki

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.pravki.dataClasses.Result
import com.example.pravki.common.Constants
import com.example.pravki.extensions.formatDate
import com.example.pravki.repository.Repository
import com.example.pravki.retrofit.RetrofitBuilder
import com.example.pravki.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MvvmViewModel(private val mainRepository: Repository) : ViewModel() {
    var movies by mutableStateOf(mutableListOf<Result>())
        private set
    var searchLineState by mutableStateOf("")
        private set
    var letShowErrorDialog by mutableStateOf("")
        private set
    var resultOfLoad by mutableStateOf(Constants.LOAD_STATE_LOADING)
        private set


    fun setMovieList(newMovies: MutableList<Result>) {
        movies = newMovies
    }
    fun setSearchLine(newTextSearchLine: String) {
        searchLineState = newTextSearchLine
    }
    fun setLetShowED(newErrorDialogState: String) {
        letShowErrorDialog = newErrorDialogState
    }
    fun setResOfLoad(newResultOfLoad: Int) {
        resultOfLoad = newResultOfLoad
    }

    fun drawProgressBar() {
        setResOfLoad(Constants.LOAD_STATE_LOADING)
    }

    fun getMovies() {
        val repo = Repository(RetrofitBuilder.apiService)
        GlobalScope.launch(Dispatchers.IO) {
            //Log.d("twer", "LOAD_STATE_NOTHING___$resultOfLoad")
            try {
                val response = if (searchLineState.isEmpty()) repo.getDiscover() else repo.getSearchDiscover(searchLineState)
                if (response.isSuccessful) {
                    //Log.d("twer", "BODY!!!")
                    setMovieList(response.body()!!.results as MutableList<Result>)
                    setResOfLoad(Constants.LOAD_STATE_DONE)
                    setLetShowED("")
                    //Log.d("twer", "LOAD_STATE_SOMETHING___$resultOfLoad")
                } else {
                    setLetShowED(R.string.error.toString() + " " + response.code())
                }
            } catch (e: Exception) {
                setLetShowED(e.message ?: R.string.unknown_error.toString())
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    private val mvvmViewModel = MvvmViewModel(Repository(RetrofitBuilder.apiService))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PravkiTheme {
                AppNavigator(mvvmViewModel)
            }
        }
    }
}

@Composable
fun AppNavigator(mvvmViewModel: MvvmViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Constants.VIEW_MOVIES
    ) {
        composable(Constants.VIEW_MOVIES) { MainScreen(navController, mvvmViewModel) }
//        composable(
//            "detailMoviesView/{movieId}",
//            arguments = listOf(
//                navArgument("movieId") { type = NavType.IntType }
//            )
//        ) {
//                backStackEntry ->
//            backStackEntry?.arguments?.getInt("movieId")?.let { movieId ->
//                DetailMovieScreen(movieId = movieId)
//            }
//        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, mvvmViewModel: MvvmViewModel) {

    // получение списка фильмов
    mvvmViewModel.getMovies()

    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))
        SearchFieldComponent(mvvmViewModel)
        Spacer(modifier = Modifier.height(10.dp))
        if (mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_LOADING) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LinearProgressIndicator(color = MaterialTheme.colors.secondary, backgroundColor = Color.White)
            }
        }
        ShowErrorDialog(mvvmViewModel)
        MovieListComponent(mvvmViewModel = mvvmViewModel, navController = navController)
//        if (mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_DONE) {
//            MovieListComponent(mvvmViewModel = mvvmViewModel, navController = navController)
//        } else {
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                LinearProgressIndicator(color = MaterialTheme.colors.secondary, backgroundColor = Color.White)
//                //CircularProgressIndicator(color = MaterialTheme.colors.secondary)
//            }
//        }
    }
}

// строка поиска
@Composable
fun SearchFieldComponent(mvvmViewModel: MvvmViewModel) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Invisible,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = HintColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(10.dp)
                ),
            value = mvvmViewModel.searchLineState,
            onValueChange = {
                mvvmViewModel.setSearchLine(it)
                mvvmViewModel.drawProgressBar()
                mvvmViewModel.getMovies()
            },
            singleLine = true,
            textStyle = TextStyle(color = MaterialTheme.colors.primaryVariant, fontSize = 20.sp),
            placeholder = { Text(text = stringResource(R.string.enter_movie_name), color = HintColor) }
        )
    }
}

// всплывающее сообщение
@Composable
fun SnackBar(mvvmViewModel: MvvmViewModel) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = Color.Red,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(5.dp)
        ) {
            Text(
                text = stringResource(R.string.error),
                color = MaterialTheme.colors.onPrimary,
            )
            TextButton(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color.Blue,
                        shape = RoundedCornerShape(10.dp),
                    ),
                onClick = {
                    mvvmViewModel.setLetShowED("")
                    //Log.d("twer", "updateButton")
                    mvvmViewModel.drawProgressBar()
                    mvvmViewModel.getMovies()
                },
                //colors = ButtonDefaults.buttonColors(backgroundColor = Invisible)
            ) {
                Text(
                    text = stringResource(R.string.update),
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
    }
}

// список фильмов ------- обработку пустого списка чекнуть, лишняя первая проверка(?)
@Composable
fun MovieListComponent(mvvmViewModel: MvvmViewModel, navController: NavHostController) {

    //Log.d("twer", "draw empty message or movie list")

    if (mvvmViewModel.movies.isEmpty() && mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_DONE) {
        //Log.d("twer", "empty_results")
        Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.empty_results, mvvmViewModel.searchLineState),
                modifier = Modifier.padding(top = 10.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }

    if (mvvmViewModel.movies.isNotEmpty()) {
        //Log.d("twer", "movie list")
        LazyColumn {
            itemsIndexed(mvvmViewModel.movies) { index, movie ->
                MovieCardComponent(movie = movie, navController = navController)
                if (index < mvvmViewModel.movies.size - 1) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Divider(color = DividerColor, thickness = 1.dp)
                }
            }
        }
    }
}

// конкретный фильм
@Composable
fun MovieCardComponent(movie: Result, navController: NavHostController) {
    // установка формата даты
    val date = movie.release_date.formatDate()

    // карточка фильма
    Row (modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth()
        .clickable {
            //navController.navigate("detailMoviesView/${movie.id}")
        }
    ) {
        // картинка фильма
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                //MANIFEST android:usesCleartextTraffic="true"
                if (movie.poster_path != null) { rememberImagePainter("${Constants.BASE_URL_IMAGES}${Constants.POSTER_SIZE_LIST}${movie.poster_path}") }
                else { painterResource(R.drawable.default_image) },
                contentDescription = stringResource(R.string.image_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(120.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 2.5.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // информация о фильме
        Column {
            // название фильма
            Text(
                text = movie.title,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // дата выхода (релиза)
            Text(
                text = stringResource(R.string.release_date) + " " + date,
                color = MaterialTheme.colors.onSecondary,
                //modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // рейтинг
            Text(
                text = stringResource(R.string.rating) + " " + movie.vote_average + " " + stringResource(R.string.star),
                color = MaterialTheme.colors.onSecondary,
                //modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

// всплывающее окно ошибки
@Composable
private fun ShowErrorDialog(mvvmViewModel: MvvmViewModel) {

    if (mvvmViewModel.letShowErrorDialog.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        SnackBar(mvvmViewModel)
//        val scaffoldState = rememberScaffoldState()
//        val snackbarCoroutineScope = rememberCoroutineScope()
//
//        Scaffold(scaffoldState = scaffoldState) {
//            snackbarCoroutineScope.launch {
//                val result = scaffoldState.snackbarHostState.showSnackbar("Smth error", "Update", SnackbarDuration.Indefinite)
//                when (result) {
//                    SnackbarResult.ActionPerformed -> {
//                        mvvmViewModel.setLetShowED("")
//                        //Log.d("twer", "updateButton")
//                        mvvmViewModel.drawProgressBar()
//                        mvvmViewModel.getMovies()
//                    }
//                }
//            }
//        }

//        AlertDialog(
//            title = { Text(text = stringResource(R.string.error)) },
//            text = { Text(mvvmViewModel.letShowErrorDialog) },
//            confirmButton = {
//                Button(onClick = {
//                    mvvmViewModel.setLetShowED("")
//                    //Log.d("twer", "updateButton")
//                    mvvmViewModel.drawProgressBar()
//                    mvvmViewModel.getMovies()
//                }) {
//                    Text(stringResource(R.string.update))
//                }
//            },
//            onDismissRequest = {
//            }
//        )
    }
}
