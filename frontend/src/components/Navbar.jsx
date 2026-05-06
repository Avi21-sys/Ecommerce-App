import { useEffect, useState, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { API_BASE_URL, fetchWithAuth, isLoggedIn as hasValidSession } from "../utils/api";
import { CartContext } from "../context/CartContext";

const Navbar = () => {
    const [cartCount, setCartCount] = useState(0);
    const navigate = useNavigate();
    const { cart } = useContext(CartContext);

    const isLoggedIn = hasValidSession();

    const fetchCartCount = () => {
        if (!isLoggedIn) return;
        fetchWithAuth(`${API_BASE_URL}/api/cart`)
            .then(res => {
                if (res.status === 401 || res.status === 403) {
                    localStorage.removeItem("token");
                    throw new Error("Session expired. Please login again.");
                }
                if (!res.ok) throw new Error(`Cart request failed with status ${res.status}`);
                return res.json();
            })
            .then(data => {
                const count = data.reduce((sum, item) => sum + item.quantity, 0);
                setCartCount(count);
            })
            .catch(err => {
                console.error(err);
                setCartCount(0);
            });
    };

    useEffect(() => {
        fetchCartCount();
        // Re-fetch if cart updates
        window.addEventListener("cart-updated", fetchCartCount);
        return () => window.removeEventListener("cart-updated", fetchCartCount);
    }, [isLoggedIn]);

    const handleLogout = () => {
        localStorage.removeItem("token");
        setCartCount(0);
        navigate("/");
    };

    return (
        <nav style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            padding: "10px 20px",
            background: "#333",
            color: "#fff"
        }}>
            <div>
                <Link to="/" style={{ color: "#fff", textDecoration: "none", marginRight: "20px" }}>Home</Link>
                {isLoggedIn && (
                    <Link to="/cart" style={{ color: "#fff", textDecoration: "none" }}>
                        Cart ({cartCount})
                    </Link>
                )}
            </div>
            <div>
                {isLoggedIn ? (
                    <button onClick={handleLogout} style={{ background: "none", color: "#fff", border: "none", cursor: "pointer" }}>
                        Logout
                    </button>
                ) : (
                    <Link to="/login" style={{ color: "#fff", textDecoration: "none" }}>Login</Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
