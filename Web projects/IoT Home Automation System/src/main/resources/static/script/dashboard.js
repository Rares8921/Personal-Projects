async function loadStats() {
    try {
        const resp = await fetch('/api/statistics');
        if (resp.ok) {
            const stats = await resp.json();
            animateCount("totalDevices", stats.totalDevices);
            animateCount("onlineDevices", stats.onlineDevices);
        }
    } catch (e) {
        console.error("Failed to load statistics", e);
    }
}

function animateCount(elementId, target) {
    const el = document.getElementById(elementId);
    if (!el) return;
    let current = 0;
    const step = Math.max(1, Math.floor(target / 30));
    const interval = setInterval(() => {
        current += step;
        if (current >= target) {
            current = target;
            clearInterval(interval);
        }
        el.textContent = current;
    }, 30);
}

loadStats();
