package com.example.pravki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import com.example.pravki.dataClasses.Discover
import com.example.pravki.dataClasses.Result
import com.example.pravki.common.Constants
import com.example.pravki.extensions.formatDate
import com.example.pravki.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MvvmViewModel : ViewModel() {
    var movies by mutableStateOf(mutableListOf<Result>())
        private set
    var searchLineState by mutableStateOf("")
        private set
    var letShowErrorDialog by mutableStateOf("")
        private set
    var resultOfLoad by mutableStateOf(Constants.LOAD_STATE_NOTHING)
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

    // получение фильмов
    fun getMyDiscover() {
        //state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>

        val movies = mutableListOf<Result>()

        // получение всех фильмов
        if (searchLineState == "") {
            Constants.retrofitService.getDiscover().enqueue(
                object : Callback<Discover> {
                    override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
                        val responseBody = response.body()!!.results
                        val myStringBuilder = StringBuilder()
                        for (myData in responseBody) {
                            myStringBuilder.append("${myData.title}\n")
                            movies.add(myData)
                        }
                        setMovieList(movies) //state.value = movies

                        if (movies.isEmpty()) setResOfLoad(Constants.LOAD_STATE_NOTHING) //resultOfLoad.value = Constants.LOAD_STATE_NOTHING
                        else setResOfLoad(Constants.LOAD_STATE_SOMETHING)  //resultOfLoad.value = Constants.LOAD_STATE_SOMETHING
                    }

                    override fun onFailure(call: Call<Discover>, t: Throwable) {
                        setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
                    }
                }
            )
        }
        // получение фильмов по запросу
        else {
            Constants.retrofitService.getSearchDiscover(query = searchLineState).enqueue( //Constants.retrofitService.getSearchDiscover(query = request).enqueue(
                object : Callback<Discover> {
                    override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
                        val responseBody = response.body()!!.results
                        val myStringBuilder = StringBuilder()
                        for (myData in responseBody) {
                            myStringBuilder.append("${myData.title}\n")
                            movies.add(myData)
                        }
                        setMovieList(movies) //state.value = movies
                    }

                    override fun onFailure(call: Call<Discover>, t: Throwable) {
                        setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
                    }
                }
            )
        }
    }
}

class MainActivity : ComponentActivity() {

    private val mvvmViewModel = MvvmViewModel()

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
    //mvvmViewModel.movies
    //mvvmViewModel.searchLineState
    //mvvmViewModel.letShowErrorDialog
    //mvvmViewModel.resultOfLoad

    // получение списка фильмов
    //getMyDiscover(mvvmViewModel)
    mvvmViewModel.getMyDiscover()

    ShowErrorDialog(mvvmViewModel)

    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))
        SearchFieldComponent(mvvmViewModel)
        Spacer(modifier = Modifier.height(10.dp))
        if (mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_SOMETHING) {
            MovieListComponent(movieList = mvvmViewModel.movies, navController = navController, resultOfLoad = mvvmViewModel.resultOfLoad)
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = MaterialTheme.colors.secondary)
            }
        }
    }
}

// строка поиска
@Composable
fun SearchFieldComponent(mvvmViewModel: MvvmViewModel) {
    //state: MutableState<MutableList<Result>>, resultOfLoad: MutableState<Int>, letShowDialog: MutableState<String>, searchLineState: MutableState<String>
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
                mvvmViewModel.setSearchLine(it) //searchLineState.value = it
//                if (mvvmViewModel.searchLineState.trim() == "")  {
//                    getMyDiscover(state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad)
//                }
//                else { getMySearchDiscover(request = searchLineState.value, state = state, letShowDialog = letShowDialog) }
            },
            singleLine = true,
            textStyle = TextStyle(color = MaterialTheme.colors.primaryVariant, fontSize = 20.sp),
            placeholder = { Text(text = stringResource(R.string.enter_movie_name), color = HintColor) }
        )
    }
}

// список фильмов ------- обработку пустого списка чекнуть, лишняя первая проверка(?)
@Composable
fun MovieListComponent(movieList: MutableList<Result>, navController: NavHostController, resultOfLoad: Int) {

    if (resultOfLoad == Constants.LOAD_STATE_NOTHING || movieList.isEmpty()) {
        Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.empty_results),
                modifier = Modifier.padding(top = 10.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }

    if (movieList.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(movieList) { index, movie ->
                MovieCardComponent(movie = movie, navController = navController)
                if (index < movieList.size - 1) {
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

    if (mvvmViewModel.letShowErrorDialog != "") {
        AlertDialog(
            title = { Text(text = stringResource(R.string.error)) },
            text = { Text(mvvmViewModel.letShowErrorDialog) },
            confirmButton = {
                Button(onClick = {
                    mvvmViewModel.setLetShowED("")
                    //getMyDiscover(mvvmViewModel)
                    mvvmViewModel.getMyDiscover()
                }) {
                    Text(stringResource(R.string.update))
                }
            },
            onDismissRequest = {
            }
        )
    }
}
