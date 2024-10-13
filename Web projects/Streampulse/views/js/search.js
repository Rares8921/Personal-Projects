'use strict'

import { apiKey, fetchData } from "./api.js"
import { createMovieCard } from "./movieCard.js"

export function search(option = 0) {
    const searchWrapper = document.querySelector(".navSearchBar");
    const searchField = document.querySelector(".searchBar");
    const searchResultModal = document.createElement("div");
    searchResultModal.classList.add("searchModal");
    document.getElementById("homePageMain").appendChild(searchResultModal);
    let searchTimeout;
    searchField.addEventListener("input", function() {
        if(!searchField.value.trim()) {
            searchResultModal.classList.remove("active");
            searchWrapper.classList.remove("searching");
            clearTimeout(searchTimeout);
        } else {
            searchWrapper.classList.add("searching");
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(function() {
                fetchData(`https://api.themoviedb.org/3/search/movie?api_key=${apiKey}&page=1&include_adult=false&query=${searchField.value}`,
                    function({results: movieList}) {
                        searchWrapper.classList.remove("searching");
                        searchResultModal.classList.remove("active");
                        searchResultModal.innerHTML = "";
                        searchResultModal.innerHTML = `
                            <p class="movieHeading">Results for:</p>
                            <h1 class="movieHeading" style="margin-left: 5%; text-decoration: underline;"> ${searchField.value} </h1>
                            <div class="sliderList">
                                <div class="sliderInner"></div>
                            </div>
                        `;
                        for(const movie of movieList) {
                            const movieCard = createMovieCard(movie, option);
                            searchResultModal.querySelector(".sliderInner").appendChild(movieCard);
                        }
                    }
                )
            }, 500);
        }
    })
}