'use strict'

import { apiKey, imageBaseURL, fetchData } from "./api.js"
import { sideBar } from "./sidebar.js"
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

fetchData(` https://api.themoviedb.org/3/movie/${movieId}}/reviews?api_key=${apiKey}`, function(movie) {
    const {
        results,
        total_results
    } = movie;
    // Extrag informatii despre film si il adaug in pagina
    fetchData(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases`, function(temp) {
        const {
            backdrop_path,
            poster_path,
            runtime,
            vote_average,
            release_date,
            releases: { countries: [{certification}]},
            genres,
            overview,
            title
        } = temp;
        document.title = `${title} - Reviews`;
        const movieDetail = document.createElement("div");
        movieDetail.classList.add("movieDetail");
        movieDetail.innerHTML = `
        <div class="movieDetail">
            <div style="background-image: url('${imageBaseURL}${'w1280' || 'original'}
                ${backdrop_path || poster_path}');"></div>
        
                <img src="${imageBaseURL}w342${poster_path}"
                     alt="${title} poster" class="imageCover" loading="lazy" draggable="false" style="border-radius: 36px; padding: 16px;">

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
                </div>
            </div>
        </div>            
        `;
        pageContent.appendChild(movieDetail);

        var h3 = document.createElement("h3");
        h3.innerHTML = `Total external reviews: ${total_results}`;
        pageContent.appendChild(h3);

        if (total_results > 0 && Array.isArray(results)) {
            const h1 = document.createElement("h1");
            h1.classList.add("movieHeading");
            h1.style.textDecoration = "underline";
            h1.innerHTML = "Reviews";
            pageContent.appendChild(h1);

            const reviewsList = document.createElement("div");
            reviewsList.classList.add("metaList");

            for (const review of results) {
                const { 
                    author, 
                    author_details, 
                    content, 
                    created_at, 
                    updated_at 
                } = review;
                if (author_details) {
                    var {
                        username,
                        rating 
                    } = author_details;
                    if(rating === null) {
                        rating = "Not specified";
                    }
                    const reviewElement = document.createElement("div");
                    reviewElement.classList.add("review");
                    reviewElement.innerHTML = `
                        <p class="metaItem">Author name: ${author}</p>
                        <p class="metaItem">Author username: ${username}</p>
                        <p class="metaItem">Movie Rating: ${rating}</p>
                        <p class="metaItem">Review: ${content}</p>
                        <p class="metaItem">Created At: ${new Date(created_at).toLocaleString()}</p>
                        <p class="metaItem">Updated At: ${new Date(updated_at).toLocaleString()}</p>
                    `;
                    const br = document.createElement("br");
                    br.style.color = "white";
                    reviewElement.appendChild(br);
                    reviewsList.appendChild(reviewElement);
                } else {
                    console.error("author_details not found in review");
                }
            }
            pageContent.appendChild(reviewsList);
        }
    });
});