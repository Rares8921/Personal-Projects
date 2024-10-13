
import { sideBar } from "./sidebar.js"

sideBar();

const particleContainer = document.querySelector('.particleContainer');
const particleCount = 150;

for (let i = 0; i < particleCount; i++) {
  const particle = document.createElement('div');
  particle.classList.add('particle');
  particleContainer.appendChild(particle);
}

const particles = document.querySelectorAll('.particle');
for(let particle of particles) {
    const x = Math.floor(Math.random() * window.innerWidth);
    const y = Math.floor(Math.random() * window.innerHeight);
    const speed = Math.floor(Math.random() * 10) + 5;
    particle.style.left = `${x}px`;
    particle.style.top = `${y}px`;
    particle.style.animationDuration = `${speed}s`;
}