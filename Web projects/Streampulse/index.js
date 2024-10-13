// Daca inca sunt pe development mode, incarc manual toate variabilele din enviroment
if(process.env.NODE_ENV !== 'production') {
    require('dotenv').config()
}

// Link la modulul express
const express = require("express");
const path = require("path");
const bodyParser = require("body-parser");

const app = express();
const apiKey = "";
const PORT = 4000; // Port pentru https
const bcrypt = require("bcrypt") // Modulul pentru criptare, pentru register/log-in system
const flash = require("express-flash")
const session = require("express-session")
const fs = require("fs")
const ejs = require('ejs');

// O parola de lungime minim 8, maxim 40
// Ce trebuie sa contina macar: o litera mica, o litera mare, o cifra si macar un simbol
const passwordRegex = /^(?!.*\s)(?=.{8,40}$)(?=.*[a-z])(?=.*[A-Z])((?=.*[0-9])|(?=.*[~!@#$%^&*()_+-=[\]{}|;:,./<>?])).*$/;

const passport = require("passport")
const initPassport = require("./passport-config")
initPassport(
    passport,
    email => users.find(user => user.email === email),
    id => users.find(user => user.id === id)
)

app.set("view engine", "ejs");
app.use(express.urlencoded({extended: false}))
app.use(flash())
// secret: cheia secreta pentru encriptie
app.use(session({
    secret: process.env.SESSION_SECRET,
    resave: false,
    saveUninitialized: false
}))
app.use(passport.initialize())
app.use(passport.session())

// Post request pt API: https://api.themoviedb.org/3/configuration?api_key=25d460cf30347f90078e0781c3b5084c

app.use(express.static(path.join(__dirname, "views")));
// Post request cu app
app.use(bodyParser.json());
// Post request cu textu
app.use(express.text());

// Vectorul cu toti userii
var users = []
var sessionID = 0;

const usersFilePath = path.join(__dirname, 'views', 'js', 'accounts.json');
const readUsersFromFile = () => {
    if (!fs.existsSync(usersFilePath)) {
        return [];
    }
    const data = fs.readFileSync(usersFilePath, 'utf-8');
    return JSON.parse(data);
};

users = readUsersFromFile();

const writeUsersToFile = (users) => {
    fs.writeFileSync(usersFilePath, JSON.stringify(users, null, 2), 'utf-8');
};

app.get("/", (request, result) => {
    result.status(200);
    result.redirect("/main/");
})

app.get("/main/", (request, result) => {
    result.status(200);
    result.render("index");
})

app.get("/main/about", (request, result) => {
    result.status(200);
    result.render("about");
})

app.get("/detail", (request, result) => {
    result.status(200);
    result.render("detail");
})

app.get("/movielist", (request, result) => {
    result.status(200);
    result.render("movielist");
})

app.get("/watchlist", checkAuthenticated, (request, result) => {
    fs.readFile(path.join(__dirname, 'views/js/accounts.json'), 'utf8', (error, data) => {
        if (error) {
            console.error('Eroare la citirea fișierului:', error);
            return result.status(500).send('Eroare la citirea fișierului');
        }
        let accounts = JSON.parse(data);
        const user = accounts.find(user => user.email === request.user.email);
        if (!user) {
            return result.status(404).send('Utilizatorul nu a fost gasit.');
        }
        // Preluarea filme
        var movies = [];
        var promises = []
        user.watchlist.forEach(movieId => {
            var promise = fetch(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases`)
                .then(response => response.json())
                .then(function(movie, optional) {
                    const {
                        poster_path,
                        title,
                        vote_average,
                        release_date,
                        id
                    } = movie;
                    movies.push(movie);
                })
            promises.push(promise);
        });
        result.status(200);
        Promise.all(promises).then(() => {
            ejs.renderFile(path.join(__dirname, 'views/watchlist.ejs'), { movies: movies, user: user }, function(error, str) {
                if (error) {
                    console.log(error);
                    return;
                }
                result.end(str);
            });
        });
    });
})

app.get("/reviews", (request, result) => {
    result.status(200);
    result.render("reviews");
})

app.get("/account", checkNotAuthenticated, (request, result) => {
    result.status(200);
    result.render("account");
})

app.get("/main/detail", (request, result) => {
    result.status(200);
    result.render("detail");
})

app.get("/main/movielist", (request, result) => {
    result.status(200);
    result.render("movielist");
})

app.get("/main/reviews", (request, result) => {
    result.status(200);
    result.render("reviews");
})

app.get("/main/watchlist", checkAuthenticated, (request, result) => {
    fs.readFile(path.join(__dirname, 'views/js/accounts.json'), 'utf8', (error, data) => {
        if (error) {
            console.error('Eroare la citirea fișierului:', error);
            return result.status(500).send('Eroare la citirea fișierului');
        }
        let accounts = JSON.parse(data);
        const user = accounts.find(user => user.email === request.user.email);
        if (!user) {
            return result.status(404).send('Utilizatorul nu a fost gasit.');
        }
        // Preluarea filme
        var movies = [];
        var promises = []
        user.watchlist.forEach(movieId => {
            var promise = fetch(`https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US&page=1&append_to_response=casts%2Cvideos%2Cimages%2Creleases`)
                .then(response => response.json())
                .then(function(movie, optional) {
                    const {
                        poster_path,
                        title,
                        vote_average,
                        release_date,
                        id
                    } = movie;
                    movies.push(movie);
                })
            promises.push(promise);
        });
        result.status(200);
        Promise.all(promises).then(() => {
            ejs.renderFile(path.join(__dirname, 'views/watchlist.ejs'), { movies: movies, user: user }, function(error, str) {
                if (error) {
                    console.log(error);
                    return;
                }
                result.end(str);
            });
        });
    });
})

app.get("/main/account", checkNotAuthenticated, (request, result) => {
    result.status(200);
    result.render("account");
})

app.post('/add-movie', (request, result) => {
    const { emailCont, filme } = request.body;
    // Citire
    fs.readFile(path.join(__dirname, 'views/js/accounts.json'), 'utf8', (error, data) => {
        if (error) {
            console.error('Eroare', error);
            return result.status(500).send('Eroare');
        }
        let accounts = JSON.parse(data);
        const user = accounts.find(user => user.email === emailCont);
        if (!user) {
            return result.status(404).send('Utilizatorul nu a fost gasit.');
        }
        // Adaugare
        var newMovies = filme.trim().split(" ");
        for(let newMovie of newMovies) {
            user.watchlist.push(newMovie);
        }
        // Elimin duplicate
        //user.watchlist = user.watchlist.filter((item, index) => {user.watchlist.indexOf(item) === index});
        fs.writeFile(path.join(__dirname, 'views/js/accounts.json'), JSON.stringify(accounts, null, 2), error => {
            if (error) {
                console.error('Eroare: ', error);
                return result.status(500).send('Eroare');
            }
            result.send('Film adaugat');
        });
    });
});

app.post('/remove-movie', (request, result) => {
    const { mailCont, idMovie } = request.body;
    // Citire
    fs.readFile(path.join(__dirname, 'views/js/accounts.json'), 'utf8', (error, data) => {
        if (error) {
            console.error('Eroare', error);
            return result.status(500).send('Eroare');
        }
        let accounts = JSON.parse(data);
        const user = accounts.find(user => user.email === mailCont);
        if (!user) {
            return result.status(404).send('Utilizatorul nu a fost găsit.');
        }
        // Elimin film
        user.watchlist = user.watchlist.filter(item => item !== idMovie)
        // Update
        fs.writeFile(path.join(__dirname, 'views/js/accounts.json'), JSON.stringify(accounts, null, 2), error => {
            if (error) {
                console.error('Eroare', error);
                return result.status(500).send('Eroare');
            }
            result.send('Film sters');
        });
    });
});

// Async pentru a putea folosi try/catch inauntru
// Todo: In try se va da flip la card catre login
// In catch se va reincarca pagina
// Dupa login se va trimite pe pagina de account
// Daca esti deja logat, cand accesezi pagina de log-in iti da redirect pe account
// Nu te poti loga cu parola gresita sau cu un cont inexisten
// Nu poti crea o parola care sa nu respecte regex-ul
// Nu te poti inregistra cu un email deja folosit sau alt username folosit
// Nu te poti loga cand esti deja logat
app.post('/register', checkNotAuthenticated, async (request, result) => {
    try {
        // Verific daca exista deja un cont cu acest mail
        if(users.some(user => user.email.trim().toLowerCase() === request.body.email.trim().toLowerCase())) {
            console.log("CONT DEJA EXISTENT!");
        } else if(passwordRegex.test(request.body.password)) {
            // Hash-ul se realizeaza asincron, deci astept pentru a avea rezultatul
            const hashedPassword = await bcrypt.hash(request.body.password, 10)
            users.push({    
                id: Date.now().toString(),
                fullname: request.body.fullname,
                username: request.body.username,
                email: request.body.email,
                password: hashedPassword,
                reviews: [],
                watchlist: []
            })
            writeUsersToFile(users);
            // Log-in
            result.redirect("/main");
            console.log("Cont creat cu succes!\n");
        } else {
            console.log("Parola incorecta\n");
        }
    } catch(error) {
        console.log(error);
        // Register
        //result.redirect("/main/account")
    }
})

app.post('/login', checkNotAuthenticated, passport.authenticate("local", {
        successRedirect: "/main/",
        failureRedirect: "/main/account",
        failureFlash: true
}))

app.delete('/logout', (request, result) => {
    request.logOut()
    result.redirect('/main/account')
})

app.use((request, result) => {
    result.status(404);
    result.render('error');
})

function checkAuthenticated(request, result, next) {
    if (request.isAuthenticated()) {
        return next()
    }
    result.redirect('/main/account')
}

function checkNotAuthenticated(request, result, next) {
    if (request.isAuthenticated()) {
        return result.redirect('/main')
    }
    next()
}

app.listen(PORT, (error) => {
    console.log("Ruleaza!");
})

// Install-uri pentru register/log-in:
/**
 * npm install bcrypt
 * npm i passport passport-local express-session express-flash
 * npm i dotenv
 */
