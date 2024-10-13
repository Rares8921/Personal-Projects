const localStrategy = require('passport-local').Strategy
const bcrypt = require('bcrypt')

var userID = -1;

function initialize(passport, getUserByEmail, getUserById) {
    const authenticateUser = async (email, password, done) => {
        const user = getUserByEmail(email);
        if(user == null) {
            return done(null, false, {message: 'No user with this email!'});
        }
        try {
            if(await bcrypt.compare(password, user.password)) {
                return done(null, user)
            } else {
                return done(null, false, {message: 'The password is incorrect!'})
            }
        } catch(error) {
            return done(error)
        }
    }
    passport.use(new localStrategy({usernameField: 'lEmail', passwordField: 'lPassword'}, authenticateUser))
    passport.serializeUser((user, done) => done(null, user.id))
    passport.deserializeUser((id, done) => {
        done(null, getUserById(id))
    })
}
module.exports = initialize

const getUserID = () => {
    return userID;
}