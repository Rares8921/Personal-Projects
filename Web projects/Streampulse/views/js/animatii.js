// Progress bar la inceputul site-ului
try {
    let progressBar = document.getElementById('progressBar');
    let body = document.body, htmlDocument = document.documentElement;
    let maximumHeight = Math.max(body.scrollHeight, body.offsetHeight, htmlDocument.clientHeight, 
                        htmlDocument.scrollHeight, htmlDocument.offsetHeight);

    const updateProgress = () => {
        //https://en.wikipedia.org/wiki/Short-circuit_evaluation
        let scrollFromTop = (htmlDocument.scrollTop || body.scrollTop) + htmlDocument.clientHeight;
        let width = (scrollFromTop / maximumHeight) * 100 + '%';

        progressBar.style.width = width;
    }

    window.addEventListener('scroll', updateProgress);
updateProgress();
} catch(err) {
    console.log("Nu exista elementele");
}
// Design pentru alert box si prompt box


window.onload = function() {
    try {
        document.querySelector("nav a").style.textDecoration = 'none';
    } catch(error) {
        //console.warn(error);
    }
};

function updateSideNav() {
    let width = document.getElementById("sideNavBar").offsetWidth;
    document.querySelector(".toggleButton").classList.toggle("toggleButtonChange");
    if(width === 0) {
        openSideNav();
    } else {
        closeSideNav();
    }
}

function openSideNav() {
    // Schimb side nav-ul astfel incat sa-l fac sa se afiseze sub navBar
    //console.log(document.querySelector(".navBar").offsetHeight);
    document.getElementById("sideNavBar").style.width = "250px";
    document.getElementById("homePageMain").style.marginLeft = "250px";
}

function closeSideNav() {
    document.getElementById("sideNavBar").style.width = "0";
    document.getElementById("homePageMain").style.marginLeft= "0";
}