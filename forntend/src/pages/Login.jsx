import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            const res = await fetch("http://localhost:8083/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) {
                throw new Error("Invalid credentials");
            }

            const data = await res.json();

            // ✅ store token
            localStorage.setItem("token", data.token);

            alert("Login successful!");

            navigate("/"); // go to home

        } catch (err) {
            console.error(err);
            alert("Login failed");
        }
    };

    return (
        <div style={{ padding: "40px", maxWidth: "400px", margin: "auto" }}>
            <h2>Login</h2>

            <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                style={{ width: "100%", marginBottom: "10px", padding: "10px" }}
            />

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={{ width: "100%", marginBottom: "10px", padding: "10px" }}
            />

            <button onClick={handleLogin}>
                Login
            </button>
        </div>
    );
};

export default Login;
