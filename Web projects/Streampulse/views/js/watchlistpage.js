'use strict'

import { apiKey, fetchData } from "./api.js"
import { sideBar } from "./sidebar.js"
import { createMovieCard } from "./movieCard.js"
import { search } from "./search.js"

const genreName = window.localStorage.getItem("genreName");
const urlParam = window.localStorage.getItem("urlParam");
var movieId = window.localStorage.getItem("movieId");
const pageContent = document.querySelector(".sliderInner");
var movies = []

sideBar();
search(1);

let lastValue = localStorage.getItem('movieId');
var ids = document.getElementById("filme");

function checkForChanges() {
    const currentValue = localStorage.getItem('movieId');
    if (currentValue !== lastValue) {
        lastValue = currentValue;
        movieId = currentValue;
        fetchData(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases`, function(movie) {
            const {
                poster_path,
                title,
                vote_average,
                release_date,
                id
            } = movie;
            const userEmail = localStorage.getItem("accountEmail");
            document.getElementById("emailCont").value = userEmail;
            const movieCard = createMovieCard(movie);
            ids.value = ids.value + ` ${id}`;
            pageContent.appendChild(movieCard);
        });
    }
}

// Check for changes every second
setInterval(checkForChanges, 1000);