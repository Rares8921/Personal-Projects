'use strict'

import { apiKey, imageBaseURL, fetchData } from "./api.js"
import { sideBar } from "./sidebar.js"
import { createMovieCard } from "./movieCard.js"
import { search } from "./search.js"

const movieId = window.localStorage.getItem("movieId");
const pageContent = document.querySelector(".movieContainer");

sideBar();

const getGenres = function(genres) {
    const genreList = [];
    for(const {name} of genres) {
        genreList.push(name);
    }
    return genreList.join(", ");
};

const getCasting = function(casts) {
    const castsList = [];
    for(const {name} of casts) {
        castsList.push(name);
        if(castsList.length == 10) {
            break;
        }
    }
    return castsList.join(", ");
};

const getDirectors = function(crew) {
    const directors = crew.filter(({job}) => job === "Director");
    const directorsList = [];
    for(const {name} of directors) {
        directorsList.push(name);
    }
    return directorsList.join(", ");
};

const getVideos = function(videos) {
    return videos.filter(({type, site}) => (type === 'Trailer' || type == 'Teaser') 
                        && site === "YouTube");
};

// Extrag date despre filmul selectat
fetchData(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases`,
    function(movie) {
        const {
            backdrop_path,
            poster_path,
            title,
            release_date,
            runtime,
            vote_average,
            releases: { countries: [{certification}]},
            genres,
            overview,
            casts: {cast, crew},
            videos: {results: videos}
        } = movie;
        document.title = `${title} - Streampulse`

        const movieDetail = document.createElement("div");
        movieDetail.classList.add("movieDetail");
        movieDetail.innerHTML = `
        <div class="movieDetail">
            <div style="background-image: url('${imageBaseURL}${'w1280' || 'original'}
                ${backdrop_path || poster_path}');"></div>
        
            <figure class="posterBox moviePoster">
                <img src="${imageBaseURL}w342${poster_path}"
                     alt="${title} poster" class="imageCover" loading="lazy" draggable="false">
            </figure>

            <div class="detailBox">
                <div class="detailContent">
                    <h1 class="movieHeading">${title}</h1>
                    <div class="metaList">
                        <div class="metaItem">
                            <img src="/imgs/starIcon.png" alt="Rating" width="20" height="20" loading="lazy">
                            <span class="sliderButtonSpan">${vote_average.toFixed(1)}</span>
                        </div>

                        <div class="metaSeparator"></div>
                        <p class="metaItem">${runtime} min</p>

                        <div class="metaSeparator"></div>
                        <p class="metaItem">${release_date.split("-")[0]}</p> 
                    
                        <div class="metaItem cardBadge">${certification}</div>

                    </div>
                    <p class="genre">${getGenres(genres)}</p>
                    <p class="overview">${overview}</p>

                    <ul class="detailList">
                        <!-- Personaje -->
                        <li><div class="listItem">
                            <p class="listName">Starring</p>
                            <p>
                                ${getCasting(cast)};
                            </p>
                        </div>
                        </li>

                        <!-- Regie -->
                        <li>
                        <div class="listItem">
                            <p class="listName">Directed by</p>
                            <p>
                                ${getDirectors(crew)};
                            </p>
                        </div>
                        </li>
                    </ul>
                </div>

                <div class="titleWrapper">
                    <h3 class="movieListTitle">Trailers and clips</h3>
                </div>

                <div class="sliderList">
                    <div class="sliderInner"></div>
                </div>

            </div>
        </div>  
        `;

        for(const {key, value} of getVideos(videos)) {
            const videoCard = document.createElement("div");
            videoCard.classList.add("videoCard");
            videoCard.innerHTML = `
                <iframe height="294" width="500" frameborder="0" allowedfullscreen="1" title="${value}" class="imgCover" loading="eager"
                    src = "https://www.youtube.com/embed/${key}?theme=dark&color=white&rel=0" allowFullScreen>
                </iframe>
            `;
            movieDetail.querySelector(".sliderInner").appendChild(videoCard);
        }
        pageContent.appendChild(movieDetail);

        fetchData(`https://api.themoviedb.org/3/movie/${movieId}/recommendations?api_key=${apiKey}&page=1`, addRecommendations);
    } 
);

const addRecommendations = function({results: movieList}, title) {
    const elem = document.createElement("section");
    elem.classList.add("movieList");
    elem.ariaLabel = `You may also like`;
    elem.innerHTML = `
        <div class="titleWrapper">
            <h3 class="movieListTitle">You may also like </h3>
        </div>
        <div class="sliderList">
            <div class="sliderInner"></div>
        </div>
    `;
    for(const movie of movieList) {
        const movieCard = createMovieCard(movie);
        elem.querySelector(".sliderInner").appendChild(movieCard);
    }
    pageContent.appendChild(elem);
}

search();

// Format link
// https://api.themoviedb.org/3/movie/${ID}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases