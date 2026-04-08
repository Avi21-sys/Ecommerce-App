import { Link } from "react-router-dom";
import { useEffect, useState } from "react";

const Navbar = () => {

    const [count, setCount] = useState(0);

    const fetchCartCount = () => {
        fetch("http://localhost:8081/api/cart")
            .then(res => res.json())
            .then(data => {
                const totalQty = data.reduce((sum, item) => sum + item.quantity, 0);
                setCount(totalQty);
            });
    };

    useEffect(() => {
        fetchCartCount();
    }, []);

    useEffect(() => {
    const interval = setInterval(fetchCartCount, 1000);
    return () => clearInterval(interval);
    }, []);

    return (
        <div style={{
            position: "sticky",
            top: 0,
            zIndex: 1000,
            display: "flex",
            justifyContent: "space-between",
            padding: "15px 30px",
            background: "#111",
            color: "#fff",
            boxShadow: "0 2px 8px rgba(0,0,0,0.3)",
            transition: "all 0.3s ease"
            
        }}>
            <Link to="/" style={{ color: "#fff", textDecoration: "none" }}>
                Home
            </Link>

            <Link to="/cart" style={{ color: "#fff", textDecoration: "none" }}>
                Cart ({count})
            </Link>
        </div>
    );
};

export default Navbar;