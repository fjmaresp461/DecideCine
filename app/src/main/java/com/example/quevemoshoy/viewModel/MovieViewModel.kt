package com.example.quevemoshoy.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quevemoshoy.model.Movie
import com.google.firebase.firestore.auth.User

class MovieViewModel : ViewModel() {
    var movies: MutableLiveData<MutableList<Movie>> = MutableLiveData(mutableListOf())
}



