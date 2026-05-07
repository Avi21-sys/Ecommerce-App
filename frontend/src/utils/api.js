export const API_BASE_URL = "";

const decodeTokenPayload = (token) => {
    try {
        const [, payload] = token.split(".");
        if (!payload) return null;

        const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
        const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "=");
        return JSON.parse(window.atob(padded));
    } catch (err) {
        console.error("Failed to decode token payload", err);
        return null;
    }
};

export const getTokenPayload = () => {
    const token = localStorage.getItem("token");
    if (!token || token.split(".").length !== 3) {
        return null;
    }

    return decodeTokenPayload(token);
};

export const getValidToken = () => {
    const token = localStorage.getItem("token");
    if (!token || token.split(".").length !== 3) {
        localStorage.removeItem("token");
        return null;
    }

    const payload = decodeTokenPayload(token);
    if (!payload || !payload.userId) {
        localStorage.removeItem("token");
        return null;
    }

    const now = Math.floor(Date.now() / 1000);
    if (payload.exp && payload.exp <= now) {
        localStorage.removeItem("token");
        return null;
    }

    return token;
};

export const isLoggedIn = () => !!getValidToken();
export const getUserRole = () => getTokenPayload()?.role || "";
export const isAdmin = () => getUserRole().toUpperCase() === "ADMIN";

export const fetchWithAuth = (url, options = {}) => {
    const token = getValidToken();
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    return fetch(url, {
        ...options,
        headers
    });
};
