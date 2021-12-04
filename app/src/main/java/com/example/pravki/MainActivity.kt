package com.example.pravki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
//        if (searchLineState.trim() == "")  { getMyDiscover(this) }
//        else { getMySearchDiscover(this) }
    }
    fun setLetShowED(newErrorDialogState: String) {
        letShowErrorDialog = newErrorDialogState
    }
    fun setResOfLoad(newResultOfLoad: Int) {
        resultOfLoad = newResultOfLoad
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
        startDestination = "moviesView"
    ) {
        composable("moviesView") { MainScreen(navController, mvvmViewModel) }
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
//    var textState = remember { mutableStateOf(mutableListOf<Result>()) }
//    var resultOfLoad = remember { mutableStateOf(Constants.LOAD_STATE_NOTHING) }
//    var searchLineState = remember { mutableStateOf("") }
//    var letShowErrorDialog =  remember { mutableStateOf("") }

    //mvvmViewModel.movies
    //mvvmViewModel.searchLineState
    //mvvmViewModel.letShowErrorDialog
    //mvvmViewModel.resultOfLoad

    // получение списка фильмов
    getMyDiscover(mvvmViewModel)

    //ShowErrorDialog(state = textState, resultOfLoad = resultOfLoad, letShowDialog = letShowErrorDialog, request = searchLineState)

    Column (modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        Spacer(modifier = Modifier.height(10.dp))
        SearchFieldComponent(mvvmViewModel)
        Spacer(modifier = Modifier.height(10.dp))
        if (mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_SOMETHING) {
            MovieListComponent(movieList = mvvmViewModel.movies, navController = navController, resultOfLoad = mvvmViewModel.resultOfLoad)
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator(color = MaterialTheme.colors.onSurface)
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
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = SearchLineColorEnd,
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
            textStyle = TextStyle(color = SearchLineColorEnd, fontSize = 20.sp),
            placeholder = { Text(text = "Введите название фильма", color = HintColor) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = Purple500,
                    contentDescription = "Search"
                )
//                IconButton(onClick = {
//                    if (searchLineState.value.trim() != "") {
////                        getMySearchDiscover(request = searchLineState.value, state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad)
//                    }
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        tint = Purple500,
//                        contentDescription = "Search"
//                    )
//                }
            }
        )
    }
}

// список фильмов ------- обработку пустого списка чекнуть, лишняя первая проверка(?)
@Composable
fun MovieListComponent(movieList: MutableList<Result>, navController: NavHostController, resultOfLoad: Int) {

    if (resultOfLoad == Constants.LOAD_STATE_NOTHING || movieList.isEmpty()) {
        Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "По Вашему запросу ничего не найдено :(",
                modifier = Modifier.padding(top = 10.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
            )
        }
    }

    if (movieList.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(movieList) { index, movie ->
                MovieCardComponent(movie = movie, navController = navController)
                if (index < movieList.size - 1) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                    Divider(color = SearchLineColorStart, thickness = 1.dp)
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
                contentDescription = "Image Text",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(120.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        2.5.dp, MaterialTheme.colors.secondary, shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // информация о фильме
        Column {
            // название фильма
            Text(
                text = movie.title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // дата выхода (релиза)
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = "Дата релиза: $date",
                    modifier = Modifier.padding(all = 4.dp),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // рейтинг
            Text(
                text = "Рейтинг: ${movie.vote_average} ⭐",
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(all = 4.dp),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}


// ----------------------------------------- получение фильмов

// получение фильмов
fun getMyDiscover(mvvmViewModel: MvvmViewModel) {
    //state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>

    val movies = mutableListOf<Result>()

    // получение всех фильмов
    if (mvvmViewModel.searchLineState == "") {
        Constants.retrofitService.getDiscover().enqueue(
            object : Callback<Discover> {
                override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
                    val responseBody = response.body()!!.results
                    val myStringBuilder = StringBuilder()
                    for (myData in responseBody) {
                        myStringBuilder.append("${myData.title}\n")
                        movies.add(myData)
                    }
                    mvvmViewModel.setMovieList(movies) //state.value = movies

                    if (movies.isEmpty()) mvvmViewModel.setResOfLoad(Constants.LOAD_STATE_NOTHING) //resultOfLoad.value = Constants.LOAD_STATE_NOTHING
                    else mvvmViewModel.setResOfLoad(Constants.LOAD_STATE_SOMETHING)  //resultOfLoad.value = Constants.LOAD_STATE_SOMETHING
                }

                override fun onFailure(call: Call<Discover>, t: Throwable) {
                    mvvmViewModel.setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
                }
            }
        )
    }
    // получение фильмов по запросу
    else {
        Constants.retrofitService.getSearchDiscover(query = mvvmViewModel.searchLineState).enqueue( //Constants.retrofitService.getSearchDiscover(query = request).enqueue(
            object : Callback<Discover> {
                override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
                    val responseBody = response.body()!!.results
                    val myStringBuilder = StringBuilder()
                    for (myData in responseBody) {
                        myStringBuilder.append("${myData.title}\n")
                        movies.add(myData)
                    }
                    mvvmViewModel.setMovieList(movies) //state.value = movies
                }

                override fun onFailure(call: Call<Discover>, t: Throwable) {
                    mvvmViewModel.setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
                }
            }
        )
    }
}

//// получение всех фильмов
//fun getMyDiscover(mvvmViewModel: MvvmViewModel) { //state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>
//    val movies = mutableListOf<Result>()
//
//    Constants.retrofitService.getDiscover().enqueue(
//        object : Callback<Discover> {
//            override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
//                val responseBody = response.body()!!.results
//                val myStringBuilder = StringBuilder()
//                for (myData in responseBody) {
//                    myStringBuilder.append("${myData.title}\n")
//                    movies.add(myData)
//                }
//                mvvmViewModel.setMovies(movies) //state.value = movies
//
//                if (movies.isEmpty()) mvvmViewModel.setResOfLoad(Constants.LOAD_STATE_NOTHING) //resultOfLoad.value = Constants.LOAD_STATE_NOTHING
//                else mvvmViewModel.setResOfLoad(Constants.LOAD_STATE_SOMETHING)  //resultOfLoad.value = Constants.LOAD_STATE_SOMETHING
//            }
//
//            override fun onFailure(call: Call<Discover>, t: Throwable) {
//                mvvmViewModel.setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
//            }
//        }
//    )
//}
//
//// получение фильмов по поиску
//fun getMySearchDiscover(mvvmViewModel: MvvmViewModel) { //state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, request: String
//    val movies = mutableListOf<Result>()
//
//    Log.d("MyDiscover", "Hello bro212")
//    Constants.retrofitService.getSearchDiscover(query = mvvmViewModel.searchLineState).enqueue(
//        object : Callback<Discover> {
//            override fun onResponse(call: Call<Discover>, response: Response<Discover>) {
//                val responseBody = response.body()!!.results
//                val myStringBuilder = StringBuilder()
//                for (myData in responseBody) {
//                    myStringBuilder.append("${myData.title}\n")
//                    movies.add(myData)
//                }
//                mvvmViewModel.setMovies(movies) //state.value = movies
//            }
//
//            override fun onFailure(call: Call<Discover>, t: Throwable) {
//                mvvmViewModel.setLetShowED(t.message.toString()) // letShowDialog.value = t.message.toString()
//            }
//        }
//    )
//}







//// обновление экрана
//fun updateScreen(state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>, request: String) {
//    if (request == "") {
//        getMyDiscover(state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad)
//    } else {
//        getMySearchDiscover(state = state, letShowDialog = letShowDialog, request = request)
//    }
//}
//
//// всплывающее окно ошибки
//@Composable
//private fun ShowErrorDialog(state: MutableState<MutableList<Result>>, letShowDialog: MutableState<String>, resultOfLoad: MutableState<Int>, request: MutableState<String>) {
//
//    if (letShowDialog.value != "") {
//        AlertDialog(
//            onDismissRequest = {
//            },
//            title = {
//                Text(text = "Ошибка!")
//            },
//            text = {
//                Text("$letShowDialog")
//            },
//            confirmButton = {
//                Button(onClick = {
//                    letShowDialog.value = ""
//                    updateScreen(state = state, letShowDialog = letShowDialog, resultOfLoad = resultOfLoad, request = request.value)
//                }) {
//                    Text("Обновить")
//                }
//            }
//        )
//    }
//}