'use strict'

import { apiKey, fetchData } from "./api.js"
import { sideBar } from "./sidebar.js"
import { createMovieCard } from "./movieCard.js"
import { search } from "./search.js"

const genreName = window.localStorage.getItem("genreName");
const urlParam = window.localStorage.getItem("urlParam");
const pageContent = document.querySelector(".movieContainer");

sideBar();

let currentPage = 1, totalPages = 0;

fetchData(`https://api.themoviedb.org/3/discover/movie?api_key=${apiKey}&sort_by=popularity.desc&include_adult=false&page=${currentPage}&${urlParam}`,
    function({results: movieList, _totalPages}) {
        totalPages = _totalPages;
        document.title = `${genreName} movies - Streampulse`;
        const element = document.createElement("section");
        element.classList.add("movieList", "genreList");
        element.ariaLabel = `${genreName} movies`;
        element.innerHTML = `
        <section class="movieList genreList">
            <div class="titleWrapper">
                <h1 class="movieHeading">All ${genreName} movies</h1>
            </div>
            <div class="gridList">
            </div>

            <button class="sliderButton loadMore loading" loadMore>Load More</button>
        </section>
        `; 
        for(const movie of movieList) {
            const movieCard = createMovieCard(movie);
            element.querySelector(".gridList").appendChild(movieCard);
        }
        pageContent.appendChild(element);
        // Load more
        document.querySelector("[loadMore]").addEventListener("click", function () {
            if(currentPage >= totalPages) {
                this.style.display = "none";
            } else {
                ++currentPage;
                this.classList.add("loading");
                fetchData(`https://api.themoviedb.org/3/discover/movie?api_key=${apiKey}&sort_by=popularity.desc&include_adult=false&page=${currentPage}&${urlParam}`, ({results: movieList}) => {
                    this.classList.remove("loading");
                    for(const movie of movieList) {
                        const movieCard = createMovieCard(movie);
                        element.querySelector(".gridList").appendChild(movieCard);
                    }
                })
            }
        });
    }
);

search();