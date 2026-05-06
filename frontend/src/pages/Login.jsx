import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../utils/api";
import { heroImage } from "../utils/productMedia";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [mode, setMode] = useState("login");
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async () => {
        try {
            setSubmitting(true);
            setError("");

            const endpoint = mode === "login" ? "login" : "register";
            const res = await fetch(`${API_BASE_URL}/api/auth/${endpoint}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) {
                throw new Error("Authentication failed");
            }

            const data = await res.json();
            localStorage.setItem("token", data.token);
            navigate("/");
        } catch (err) {
            console.error(err);
            setError(
                mode === "login"
                    ? "Login failed. Check your details and try again."
                    : "Could not create your account right now."
            );
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div
            className="auth-page"
            style={{
                backgroundImage: `linear-gradient(120deg, rgba(37, 23, 18, 0.72), rgba(12, 74, 68, 0.58)), url(${heroImage})`
            }}
        >
            <div className="auth-page__inner">
                <div className="auth-copy">
                    <span className="eyebrow">Account access</span>
                    <h1>Pick up where you left off.</h1>
                    <p>Log in to sync your cart, or create a fresh account and start building your next order.</p>
                </div>

                <div className="auth-panel">
                    <div className="auth-tabs">
                        <button
                            className={`auth-tabs__button ${mode === "login" ? "auth-tabs__button--active" : ""}`}
                            onClick={() => setMode("login")}
                        >
                            Login
                        </button>
                        <button
                            className={`auth-tabs__button ${mode === "register" ? "auth-tabs__button--active" : ""}`}
                            onClick={() => setMode("register")}
                        >
                            Register
                        </button>
                    </div>

                    <div className="auth-panel__body">
                        <h2>{mode === "login" ? "Welcome back" : "Create account"}</h2>
                        <p>{mode === "login" ? "Your saved cart and orders are ready." : "Start with a new account in under a minute."}</p>

                        <label className="field">
                            <span className="field__label">Username</span>
                            <input
                                type="text"
                                placeholder="Enter username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                className="input"
                            />
                        </label>

                        <label className="field">
                            <span className="field__label">Password</span>
                            <input
                                type="password"
                                placeholder="Enter password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="input"
                            />
                        </label>

                        {error && <p className="form-error">{error}</p>}

                        <button onClick={handleSubmit} className="button button--primary button--full" disabled={submitting}>
                            {submitting ? "Please wait..." : mode === "login" ? "Login" : "Create account"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
