import { useContext } from "react";
import { CartContext } from "../context/CartContext";

const ProductCard = ({product}) => {
    const { addToCart } = useContext(CartContext);

    return (
        <div style={{
            borderRadius: "12px",
            background: "#fff",
            boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
            overflow: "hidden",
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            height: "100%",
            transition: "0.3s"
            }}
            onMouseEnter={e => e.currentTarget.style.transform = "translateY(-5px)"}
            onMouseLeave={e => e.currentTarget.style.transform = "translateY(0)"}
        >
            <img src={product.image} alt={product.name}
            style={{
                width: "100%",
                height: "180px",
                objectFit: "cover"
            }}/>

            <div style={{
                padding: "15px",
                display: "flex",
                flexDirection: "column",
                gap: "8px",
                flexGrow: 1
                }}>
                <h3 style={{ margin: 0 }}>{product.name}</h3>
                <p style={{ margin: 0, color: "#666" }}>₹{product.price}</p>
            </div>

            <div style={{ padding: "15px" }}>
                <button 
                onClick={() => addToCart(product)}
                style={{
                    width: "100%",
                    padding: "10px",
                    background: "#111",
                    color: "#fff",
                    border: "none",
                    borderRadius: "6px",
                    cursor: "pointer"
                    }}>
                    Add To Cart
                </button>
            </div>
        </div>
    )
};

export default ProductCard;