document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.getElementById("themeToggle");
    const html = document.documentElement;

    function applyTheme(theme) {
        html.classList.remove("light", "dark");
        html.classList.add(theme);
        if (toggleBtn) {
            toggleBtn.textContent = theme === "dark" ? "☀️" : "🌙";
        }
        localStorage.setItem("theme", theme);
    }

    const savedTheme = localStorage.getItem("theme") || "light";
    applyTheme(savedTheme);

    if (toggleBtn) {
        toggleBtn.addEventListener("click", () => {
            const newTheme = html.classList.contains("dark") ? "light" : "dark";
            applyTheme(newTheme);
        });
    }
});
