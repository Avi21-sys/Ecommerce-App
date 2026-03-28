import { useContext } from "react";
import { CartContext } from "../context/CartContext";

const ProductCard = ({product}) => {
    const { addToCart } = useContext(CartContext);

    return (
        <div style={{
            border: "1px solid #ddd",
            padding: "15px",
            borderRadius: "12px",
            textAlign: "center",
            background: "#fff",
            boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
            transition: "0.3s",
            cursor: "pointer"
        }}>
            <img src={product.image} alt={product.name}
            style={{
                width: "100%",
                height: "150px",
                objectFit: "cover",
                borderRadius: "10px"
            }}/>

            <h3 style={{margin: "10px 0"}}>{product.name}</h3>
            <p style={{ fontWeight: "bold", color: "#333" }}>₹{product.price}</p>
            <button 
                onClick={() => addToCart(product)}
                style={{
                marginTop: "10px",
                padding: "8px 12px",
                border: "none",
                background: "#007bff",
                color: "#fff",
                borderRadius: "6px",
                cursor: "pointer"
            }}>Add To Cart</button>
        </div>
    )
};

export default ProductCard;