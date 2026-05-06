import { useEffect, useState, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { API_BASE_URL, fetchWithAuth, isLoggedIn as hasValidSession } from "../utils/api";
import { CartContext } from "../context/CartContext";

const Navbar = () => {
    const [cartCount, setCartCount] = useState(0);
    const navigate = useNavigate();
    useContext(CartContext);

    const isLoggedIn = hasValidSession();

    const fetchCartCount = () => {
        if (!isLoggedIn) return;

        fetchWithAuth(`${API_BASE_URL}/api/cart`)
            .then((res) => {
                if (res.status === 401 || res.status === 403) {
                    localStorage.removeItem("token");
                    throw new Error("Session expired. Please login again.");
                }
                if (!res.ok) throw new Error(`Cart request failed with status ${res.status}`);
                return res.json();
            })
            .then((data) => {
                const count = data.reduce((sum, item) => sum + item.quantity, 0);
                setCartCount(count);
            })
            .catch((err) => {
                console.error(err);
                setCartCount(0);
            });
    };

    useEffect(() => {
        fetchCartCount();
        window.addEventListener("cart-updated", fetchCartCount);
        return () => window.removeEventListener("cart-updated", fetchCartCount);
    }, [isLoggedIn]);

    const handleLogout = () => {
        localStorage.removeItem("token");
        setCartCount(0);
        navigate("/");
    };

    return (
        <nav className="navbar">
            <div className="navbar__brand-group">
                <Link to="/" className="navbar__brand">ModeMint</Link>
                <span className="navbar__tag">Fresh edits, ready to wear</span>
            </div>

            <div className="navbar__links">
                <Link to="/" className="navbar__link">Home</Link>
                {isLoggedIn && (
                    <Link to="/cart" className="navbar__link navbar__cart-link">
                        Cart <span className="navbar__badge">{cartCount}</span>
                    </Link>
                )}
            </div>

            <div className="navbar__actions">
                {isLoggedIn ? (
                    <button onClick={handleLogout} className="button button--ghost">
                        Logout
                    </button>
                ) : (
                    <Link to="/login" className="button button--primary">Login</Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
