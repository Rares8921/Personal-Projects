'use strict'

// Incarc sidebar-ul
import {sideBar} from "./sidebar.js"
import { apiKey, imageBaseURL, fetchData } from "./api.js";
import { createMovieCard } from "./movieCard.js"
import { search } from "./search.js";

// sideBar() returneaza un hashTable cu key=idGenre si val=genre
const genreList = sideBar();

/**
 *  {
        title: "Weekly trending movies.",
        path: "/trending/movie/week"
    },
 */
const indexSections = [
    {
        title: "Daily trending movies.",
        path: "/trending/movie/day"
    },
    {
        title: "Upcoming movies.",
        path: "/movie/upcoming"
    },
    {
        title: "Top rated movies.",
        path: "/movie/top_rated"
    }
];

const pageContent = document.querySelector(".movieContainer");
const heroBanner = function({results: movieList}) {
    const movieBanner = document.createElement("section");
    movieBanner.classList.add("movieBanner");
    movieBanner.ariaLabel = "Popular movies";
    movieBanner.innerHTML = `
        <div class="sliderBanner"></div>
        <div class="sliderControl">
            <div class="innerControl"></div>
        </div>
    `;
    let controlItemIndex = 0;
    for(const [index, movie] of movieList.entries()) {
        const {
            backdrop_path,
            title,
            release_date,
            genre_ids,
            overview,
            poster_path,
            vote_average,
            id
        } = movie;
        // Iau toate valorile posibile pentru genre
        let categories = "";
        for(const id of genre_ids) {
            categories = categories.concat(genreList[id].toString());
            if(id != genre_ids[genre_ids.length - 1]) {
                categories = categories.concat(", ");
            }
        }
        const sliderItem = document.createElement("div");
        sliderItem.classList.add("sliderItem");
        sliderItem.setAttribute("slider-item", "")
        sliderItem.innerHTML = `
                        
        <div class="bannerContent"
         style="background-image: url('${imageBaseURL}w1280${backdrop_path}'); background-repeat: no-repeat; background-size: cover; width: 75%; height: 75%;">
            <h2 class="movieHeading">${title}</h2>
            <div class="metaList">
                <p class="metaItem">${release_date.split("-")[0]}</p>
                <p class="metaItem cardBadge">${vote_average.toFixed(1)}</p>
            </div>
            <p class="genre">${categories.toString()}</p>
            <p class="movieDescription">${overview}</p>
            <a href="./detail" class="sliderButton" onclick="getMovieDetail(${id})">
                <img src="/imgs/playIcon.png" alt="P" width="24" height="24" aria-hidden="true">
                <span class="sliderButtonSpan">Watch trailers!</span>
            </a>
        </div>
        `;
        movieBanner.querySelector(".sliderBanner").appendChild(sliderItem);

        const controlItem = document.createElement("button");
        controlItem.classList.add("posterBox", "sliderItem");
        controlItem.setAttribute("control-item", `${controlItemIndex}`);
        ++controlItemIndex;
        controlItem.innerHTML = `
        <img src="${imageBaseURL}w154${poster_path}" 
        alt="Slide to ${title}" loading="lazy" draggable="false" class="imageCover">
        `;
        movieBanner.querySelector(".innerControl").appendChild(controlItem);
    }

    pageContent.appendChild(movieBanner);
    addHeroSlide();

    // Fetch la datele pentru sectiuni
    for(const {title, path} of indexSections) {
        fetchData(`https://api.themoviedb.org/3${path}?api_key=${apiKey}&language=en-US&page=1`, createMovieList, title);
    }
};

fetchData(`https://api.themoviedb.org/3/movie/popular?api_key=${apiKey}&language=en-US&page=1`, heroBanner);

const addHeroSlide = function() {
    const sliderItems = document.querySelectorAll("[slider-item]");
    const sliderControls = document.querySelectorAll("[control-item]");
    let lastSliderItem = sliderItems[0], lastSliderControl = sliderControls[0];
    lastSliderItem.classList.add("active");
    lastSliderControl.classList.add("active");
    for(const sliderControl of sliderControls) {
        sliderControl.addEventListener("click", function() {
            lastSliderItem.classList.remove("active");
            lastSliderControl.classList.remove("active");
            sliderItems[Number(sliderControl.getAttribute("control-item"))].classList.add("active");
            sliderControl.classList.add("active");
            lastSliderItem = sliderItems[Number(sliderControl.getAttribute("control-item"))];
            lastSliderControl = sliderControl;
        });
    }
};

const createMovieList = function({results: movieList}, title) {
    const elem = document.createElement("section");
    elem.classList.add("movieList");
    elem.ariaLabel = `${title}`;
    elem.innerHTML = `
        <div class="titleWrapper">
            <h3 class="movieListTitle">${title}</h3>
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

// Pentru popular movies
// https://api.themoviedb.org/3/movie/popular?language=en-US&page=1
// (popular?)api_key=apiKey(&language)