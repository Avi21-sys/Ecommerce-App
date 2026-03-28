import { useContext } from "react";
import { CartContext } from "../context/CartContext";
import { Link } from "react-router-dom";

const Navbar = () => {
    const {cart} = useContext(CartContext);

    return(
        <div style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            padding: "15px 30px",
            background: "#111",
            color: "#fff",
            position: "sticky",
            top: 0
        }}>
            <h2>E-Commerce</h2>

            <Link to="/" style={{ color: "#fff", textDecoration: "none" }}>
              
            </Link>

            <Link to="/cart" style={{ color: "#fff", textDecoration: "none" }}>
                Cart ({cart.length})
            </Link>
        </div>
    );
};

export default Navbar;