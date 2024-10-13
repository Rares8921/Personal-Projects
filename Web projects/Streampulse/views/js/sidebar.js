'use strict'

import { apiKey, fetchData } from "./api.js"

// Link pt movie genre
// https://api.themoviedb.org/3/genre/movie/list?api_key=25d460cf30347f90078e0781c3b5084c&language=en

export function sideBar() {
    const genreList = {};
    fetchData(`https://api.themoviedb.org/3/genre/movie/list?api_key=${apiKey}`, function({genres}) {
        for(const {id, name} of genres) {
            genreList[id] = name;
        }
        genreLink();
    });

    const innerSideBar = document.createElement("div");
    innerSideBar.classList.add("innerSidebar");
    innerSideBar.innerHTML = `
        <div class="sidebarList">
            <p class="languageTitle">Genre</p>
        </div>
        <div class="sidebarList">
            <p class="languageTitle">Language</p>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=ro', 'Romanian')">Romanian</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=en', 'English')">English</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=hi', 'Hindi')">Hindi</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=it', 'Italian')">Italian</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=fr', 'French')">French</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=es', 'Spanish')">Spanish</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=de', 'German')">German</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=ru', 'Russian')">Russian</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=zh', 'Mandarin')">Mandarin</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=ja', 'Japanese')">Japanese</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=nl', 'Dutch')">Dutch</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=no', 'Norwegian')">Norwegian</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=pl', 'Polish')">Polish</a>
            <a href="./movielist" class="sidebarLink" onclick="getMovieList('with_original_language=pt', 'Portuguese')">Portuguese</a>
            <div class="sidebarList">
                <a style="margin-left: 35%;">Log out</a>
            </div>
            <a style="visibility: hidden">dummy</a>
            <a>dummy</a>
            <a>dummy</a>
            <a>dummy</a>
        </div>
    `;

    
    const genreLink = function() {
        for(const [id, name] of Object.entries(genreList)) {
            const link = document.createElement("a");
            link.classList.add("sidebarLink");
            link.setAttribute("href", "./movielist");
            link.setAttribute("close", "");
            link.setAttribute("onclick", `getMovieList("with_genres=${id}", "${name}")`);
            link.textContent = name;
        
            innerSideBar.querySelectorAll(".sidebarList")[0].appendChild(link);
        }
        const sidebar = document.getElementById("sideNavBar");
        sidebar.appendChild(innerSideBar);
        toggleSidebar(sidebar);
    }
    
    const toggleSidebar = function(sidebar) {
        const sidebarButton = document.getElementById("sidebarToggleButton");
        sidebarButton.addEventListener("click", function() {
            sidebar.classList.toggle("active");
        })
    }
    document.querySelectorAll(".sideNav a").forEach(a => {
        const width = a.offsetWidth;
        console.log(a.offsetWidth);
        a.style.setProperty("--element-width", `${width}px`);
    });
    return genreList;
}