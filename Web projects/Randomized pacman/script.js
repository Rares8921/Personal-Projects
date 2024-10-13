let upPressed = false;
let downPressed = false;
let leftPressed = false;
let rightPressed = false;
let playerTop = 1;
let playerLeft = 8;

const main = document.querySelector('main');

let gameStarted = false;
let score = 0;

// Player = 2, Wall = 1, Enemy = 3, Point = 0
let maze = [
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    [1, 0, 0, 1, 0, 0, 0, 0, 2, 1],
    [1, 0, 0, 0, 0, 0, 0, 1, 1, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 1, 1, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 1, 1, 1],
    [1, 0, 0, 1, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 1, 0, 1],
    [1, 0, 1, 0, 0, 0, 0, 0, 0, 1],
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
];

// Generez n pozitii pentru enemy-uri dintre cele accesibile
function generateRandomPositions(numberOfEnemies) {
    const positions = [];
    const freePositions = [];
    for (let y = 0; y < maze.length; y++) {
        for (let x = 0; x < maze[y].length; x++) {
            if (maze[y][x] === 0) {
                freePositions.push({ x, y });
            }
        }
    }
    if (numberOfEnemies > freePositions.length) {
        throw new Error("Too much enemies");
    }
    while (positions.length < numberOfEnemies) {
        const randomIndex = Math.floor(Math.random() * freePositions.length);
        const position = freePositions.splice(randomIndex, 1)[0];
        positions.push(position);
    }
    return positions;
}

function inMat(i, j, n, m) {
    return i >= 0 && i < n && j >= 0 && j < m;
}

// Verific daca toate punctele pot fi colectate cu un bfs
function isAccessible(maze, stX, stY) {
    const directions = [
        { x: 0, y: 1 },
        { x: 1, y: 0 },
        { x: 0, y: -1 },
        { x: -1, y: 0 }
    ];
    const v = Array.from({length: maze.length}, () => Array(maze[0].length).fill(false));
    const Q = [{ x: stX, y: stY }];
    v[stY][stX] = true;
    while (Q.length > 0) {
        const {x, y} = Q.shift(); // Pop si get
        for (const {x: dx, y: dy} of directions) {
            const newX = x + dx;
            const newY = y + dy;
            if (inMat(newX, newY, maze[0].length, maze.length) && !v[newY][newX] && maze[newY][newX] !== 1 && maze[newY][newX] !== 3) {
                v[newY][newX] = true;
                Q.push({x: newX, y: newY});
            }
        }
    }
    for (let y = 0; y < maze.length; y++) {
        for (let x = 0; x < maze[y].length; x++) {
            if (maze[y][x] === 0 && !v[y][x]) {
                return false;
            }
        }
    }
    return true;
}

// Plasarea efectiva a enemy-urilor
function placeEnemies(numberOfEnemies) {
    let enemyPositions;
    while (true) {
        // Daca configuratia nu este corecta, resetez
        maze = maze.map(row => row.map(cell => (cell === 3 ? 0 : cell)));
        enemyPositions = generateRandomPositions(numberOfEnemies);
        for (const { x, y } of enemyPositions) {
            maze[y][x] = 3;
        }
        if (isAccessible(maze, playerLeft, playerTop)) {
            break;
        }
    }
    enemyPositions.forEach(({ x, y }) => {
        maze[y][x] = 3;
    });
}

placeEnemies(3);

// Populates the maze in the HTML
for (let y = 0; y < maze.length; y++) {
    for (let x = 0; x < maze[y].length; x++) {
        let block = document.createElement('div');
        block.classList.add('block');
        block.dataset.x = x;
        block.dataset.y = y;
        switch (maze[y][x]) {
            case 1:
                block.classList.add('wall');
                break;
            case 2:
                block.id = 'player';
                let mouth = document.createElement('div');
                mouth.classList.add('mouth');
                block.appendChild(mouth);
                break;
            case 3:
                block.classList.add('enemy');
                break;
            default:
                block.classList.add('point');
                block.style.height = '1vh';
                block.style.width = '1vh';
        }

        main.appendChild(block);
    }
}

const startButton = document.getElementById('startButton');
startButton.addEventListener('click', function() {
    startButton.style.display = 'none';
    startGame();
});

function startGame() {
    gameStarted = true;
}

function canMoveTo(x, y) {
    return (x >= 0 && x < maze[0].length && y >= 0 && y < maze.length && maze[y][x] !== 1);
}

var player = document.querySelector('#player');
var playerMouth = player.querySelector('.mouth');

function checkCollision() {
    const block = maze[playerTop][playerLeft];
    if (block === 0) {
        score++;
        document.querySelector('.score p').textContent = score;
        maze[playerTop][playerLeft] = null;
        document.querySelector(`.block[data-x='${playerLeft}'][data-y='${playerTop}']`).classList.remove('point');
    }
}

function checkGameOver() {
    if (document.querySelectorAll('.point').length === 0) {
        alert('Game Over! You collected all points.');
        gameStarted = false;
    }
}

function checkEnemyCollision() {
    const block = maze[playerTop][playerLeft];
    if (block === 3) {
        alert('Game Over! You collided with an enemy.');
        gameStarted = false;
        player.classList.add('dead');
    }
}

let leftPercentage = 0;
let topPercentage = 0;

setInterval(function() {
    if (!gameStarted) return;

    let newTop = playerTop;
    let newLeft = playerLeft;

    if (downPressed && canMoveTo(playerLeft, playerTop + 1)) {
        newTop++;
        topPercentage += 100;
        player.style.top = topPercentage + "%";
        playerMouth.classList = 'mouth down';
    } else if (upPressed && canMoveTo(playerLeft, playerTop - 1)) {
        newTop--;
        topPercentage -= 100;
        player.style.top = topPercentage + "%";
        playerMouth.classList = 'mouth up';
    } else if (leftPressed && canMoveTo(playerLeft - 1, playerTop)) {
        newLeft--;
        leftPercentage -= 100;
        player.style.left = leftPercentage + "%";
        playerMouth.classList = 'mouth left';
    } else if (rightPressed && canMoveTo(playerLeft + 1, playerTop)) {
        newLeft++;
        leftPercentage += 100;
        player.style.left = leftPercentage + "%";
        playerMouth.classList = 'mouth right';
    }

    if (newTop !== playerTop || newLeft !== playerLeft) {
        playerTop = newTop;
        playerLeft = newLeft;
        checkCollision();
        checkEnemyCollision();
        checkGameOver();
    }
}, 200);

function keyUp(event) {
    if (event.key === 'ArrowUp') {
        upPressed = false;
    } else if (event.key === 'ArrowDown') {
        downPressed = false;
    } else if (event.key === 'ArrowLeft') {
        leftPressed = false;
    } else if (event.key === 'ArrowRight') {
        rightPressed = false;
    }
}

function keyDown(event) {
    if (event.key === 'ArrowUp') {
        upPressed = true;
    } else if (event.key === 'ArrowDown') {
        downPressed = true;
    } else if (event.key === 'ArrowLeft') {
        leftPressed = true;
    } else if (event.key === 'ArrowRight') {
        rightPressed = true;
    }
}

document.addEventListener('keydown', keyDown);
document.addEventListener('keyup', keyUp);
