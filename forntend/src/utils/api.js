export const fetchWithAuth = (url, options = {}) => {
    const token = localStorage.getItem("token");
    const headers = {
        "Content-Type" : "application/json",
        ...(options.headers || {})
    };

    if (token && token.split(".").length === 3) {
        headers.Authorization = `Bearer ${token}`;
    } else if (token) {
        localStorage.removeItem("token");
    }

    return fetch(url, {
        ...options,
        headers
    });
}
