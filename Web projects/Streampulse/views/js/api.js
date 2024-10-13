//https://www.w3schools.com/js/js_strict.asp
'use strict'

const apiKey = "25d460cf30347f90078e0781c3b5084c";
const imageBaseURL = "https://image.tmdb.org/t/p/";

/**
 * Preiau si modific datele de pe server folosind "url"
 * si trimit rezultatele intr-o fila JSON la o functie de "callback"
 */

const fetchData = function(url, callback, optional) {
    fetch(url)
        .then(response => response.json())
        .then(data => callback(data, optional));
};

export {imageBaseURL, apiKey, fetchData};