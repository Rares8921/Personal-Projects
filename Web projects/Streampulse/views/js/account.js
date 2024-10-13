import {sideBar} from "./sidebar.js"


document.getElementById("loginButton").onclick = function() {
    localStorage.setItem("accountEmail", document.getElementById("lEmail").value);
}

sideBar();