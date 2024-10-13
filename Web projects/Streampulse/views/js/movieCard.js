'use strict'

import { imageBaseURL } from "./api.js"

export function createMovieCard(movie, option = 0) {
    const {
        poster_path,
        title,
        vote_average,
        release_date,
        id
    } = movie;
    const card = document.createElement("div");
    card.classList.add("movieCard");
    card.innerHTML = `
    <figure class="posterBox cardBanner">
        <img src="${imageBaseURL}w342${poster_path}" alt="${title}" class="imageCover">
    </figure>
    <h4 class="movieTitle">${title}</h4>
    <div class="metaList">
        <div class="metaItem">
            <img src="/imgs/starIcon.png" alt="Rating" width="20" height="20" loading="lazy">
            <span class="sliderButtonSpan">${vote_average.toFixed(1)}</span>
        </div>
        <p class="cardBadge">${release_date.split("-")[0]}</p>
    </div>
    <a href="${option === 0 ? "/detail" : "#"}" class="cardButton" title="${title}" onclick="getMovieDetail(${id})">
        
    </a>
    `;
    return card;
};