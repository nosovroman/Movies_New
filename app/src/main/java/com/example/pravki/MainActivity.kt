package com.example.pravki

import android.app.Application
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.pravki.dataClasses.generalMovie.Result
import com.example.pravki.common.Constants
import com.example.pravki.extensions.formatDate
import com.example.pravki.repository.Repository
import com.example.pravki.repository.RepositoryRoom
import com.example.pravki.retrofit.RetrofitBuilder
import com.example.pravki.room.database.DatabaseFavorites
import com.example.pravki.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelRoom(application: Application) : AndroidViewModel(application) {
    var readAllData: LiveData<List<Int>>
    private var repositoryRoom: RepositoryRoom

    init {
        val x = DatabaseFavorites.getInstance(application).favoritesDao()
        repositoryRoom = RepositoryRoom(x)
        readAllData = repositoryRoom.readAllData
    }

    fun appendInFavoritesList(favoriteId: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            repositoryRoom.addInFavorites(favoriteId)
        }
        Log.d("log", "adding $favoriteId")
    }

    fun deleteFromFavoritesList(favoriteId: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            repositoryRoom.deleteFromFavorites(favoriteId)
        }
        Log.d("log", "deleting $favoriteId")
    }

    fun checkExistFavoriteById(favoriteId: Int): Boolean {
        return readAllData.value?.contains(favoriteId) ?: false
    }
}

class MvvmViewModel(private val mainRepository: Repository) : ViewModel() {
    var movies by mutableStateOf(mutableListOf<Result>())
        private set
    var searchLineState by mutableStateOf("")
        private set
    var letShowErrorDialog by mutableStateOf("")
        private set
    var resultOfLoad by mutableStateOf(Constants.LOAD_STATE_LOADING)
        private set


    private fun setMovieList(newMovies: MutableList<Result>) {
        movies = newMovies
    }
    fun setSearchLine(newTextSearchLine: String) {
        searchLineState = newTextSearchLine
    }
    fun setLetShowED(newErrorDialogState: String) {
        letShowErrorDialog = newErrorDialogState
    }
    private fun setResOfLoad(newResultOfLoad: Int) {
        resultOfLoad = newResultOfLoad
    }

    fun drawProgressBar() {
        setResOfLoad(Constants.LOAD_STATE_LOADING)
    }

    fun getMovies() {
        val repo = mainRepository
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = if (searchLineState.isEmpty()) repo.getDiscover() else repo.getSearchDiscover(searchLineState)
                if (response.isSuccessful) {
                    setMovieList(response.body()!!.results as MutableList<Result>)
                    setResOfLoad(Constants.LOAD_STATE_DONE)
                    setLetShowED("")
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

        val viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        //val x = DatabaseFavorites.getInstance(this)

        setContent {
            PravkiTheme {
                AppNavigator(mvvmViewModel, viewModelRoom)
            }
        }
    }
}

@Composable
fun AppNavigator(mvvmViewModel: MvvmViewModel, viewModelRoom: ViewModelRoom) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Constants.VIEW_MOVIES
    ) {
        composable(Constants.VIEW_MOVIES) { MainScreen(navController, mvvmViewModel, viewModelRoom) }
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
fun MainScreen(navController: NavHostController, mvvmViewModel: MvvmViewModel, viewModelRoom: ViewModelRoom) {

    val favorites = viewModelRoom.readAllData.observeAsState(listOf()).value
    Log.d("log", favorites.toString())

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
        MovieListComponent(navController, mvvmViewModel, viewModelRoom)
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
                    mvvmViewModel.drawProgressBar()
                    mvvmViewModel.getMovies()
                },
            ) {
                Text(
                    text = stringResource(R.string.update),
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
    }
}

// список фильмов
@Composable
fun MovieListComponent(navController: NavHostController, mvvmViewModel: MvvmViewModel, viewModelRoom: ViewModelRoom) {

    if (mvvmViewModel.movies.isEmpty() && mvvmViewModel.resultOfLoad == Constants.LOAD_STATE_DONE) {
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
        LazyColumn {
            itemsIndexed(mvvmViewModel.movies) { index, movie ->
                val isFavorite = viewModelRoom.checkExistFavoriteById(movie.id)
                MovieCardComponent(navController, viewModelRoom, movie, isFavorite)
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
fun MovieCardComponent(navController: NavHostController, viewModelRoom: ViewModelRoom, movie: Result, movieIsFavorite: Boolean) {
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

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
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
                    style = MaterialTheme.typography.body2,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // рейтинг
                Text(
                    text = stringResource(R.string.rating) + " " + movie.vote_average + " " + stringResource(R.string.star),
                    color = MaterialTheme.colors.onSecondary,
                    style = MaterialTheme.typography.body2,
                )

                var isFavorite = movieIsFavorite
                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = {
                        if (isFavorite) viewModelRoom.deleteFromFavoritesList(movie.id)
                        else viewModelRoom.appendInFavoritesList(movie.id)
                        isFavorite = !isFavorite },
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.image_description),
                        modifier = Modifier
                            .size(55.dp)
                            .padding(0.dp),
                        tint = MaterialTheme.colors.secondary
                    )
                }
            }
        }

    }
}

// всплывающее окно ошибки
@Composable
private fun ShowErrorDialog(mvvmViewModel: MvvmViewModel) {

    if (mvvmViewModel.letShowErrorDialog.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        SnackBar(mvvmViewModel)
    }
}
