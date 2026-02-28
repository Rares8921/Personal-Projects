const originalFetch = window.fetch;

window.fetch = async function (url, options = {}) {
    if (url.startsWith("https://eu.thingsboard.cloud/")) {
        return originalFetch(url, options);
    }

    const token = localStorage.getItem("jwt");

    options.headers = options.headers || {};

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await originalFetch(url, options);

        if (response.status === 401 || response.status === 403) {
            window.location.href = "/login";
        }

        return response;
    } catch (error) {
        console.error("Fetch error:", error);
        throw error;
    }
};


function logout() {
    localStorage.removeItem('jwt');
    window.location.href = "/login";
}