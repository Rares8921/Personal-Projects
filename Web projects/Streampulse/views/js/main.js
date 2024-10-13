// Cand apas pe un card, stochez in local storage acel film
const getMovieDetail = function(movieId) {
    window.localStorage.setItem("movieId", String(movieId));
}

const getMovieList = function(urlParam, genreName) {
    window.localStorage.setItem("urlParam", String(urlParam));
    window.localStorage.setItem("genreName", String(genreName));
}