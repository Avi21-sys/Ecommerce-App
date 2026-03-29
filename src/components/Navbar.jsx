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
            padding: "20px 40px",
            background: "#111",
            color: "#fff",
            position: "sticky",
            top: 0,
            zIndex: 100
        }}>
            <h2 style={{letterSpacing: "1px"}}>E-Commerce</h2>

            <Link to="/" style={{ color: "#fff",textDecoration: "none",fontWeight: "bold"}}>
              
            </Link>

            <Link to="/cart" style={{color: "#fff",textDecoration: "none",fontWeight: "bold"}}>
                Cart ({cart.length})
            </Link>
        </div>
    );
};

export default Navbar;