import { useContext } from "react";
import { CartContext } from "../context/CartContext";

const Cart = () => {
    const {cart, setCart} = useContext(CartContext);

    const removeItem = (indexToRemove) => {
        const updatedCart = cart.filter((_, index) => index !== indexToRemove);
        setCart(updatedCart);
    };

    const total = cart.reduce((sum, item) => sum + item.price * item.qty, 0);

    const increaseQty = (id) => {
        setCart(prev => 
            prev.map(item =>
                item.id === id ?
                {...item, qty: item.qty + 1} :item
            )
        );
    };

    const decreaseQty = (id) => {
        setCart(prev => 
            prev.map(item => 
                item.id === id ?
                {...item, qty: item.qty - 1} :item
            )
            .filter(item => item.qty > 0)
        );
    };

    return(
        <div style={{padding: "20px"}}>
            <h2>Cart Items</h2>

            {cart.length === 0 ?(
                <p>Your cart is empty</p>
            ):(
                <>
                    {cart.map((item, index) => (
                        <div key={index} style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            padding: "15px",
                            margin: "15px 0",
                            borderRadius: "10px",
                            background: "#fff",
                            boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
                        }}>

                        {/* LEFT: Image + Name */}
                        <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
                            <img 
                                src={item.image} 
                                alt={item.name}
                                style={{
                                width: "80px",
                                height: "80px",
                                objectFit: "cover",
                                borderRadius: "8px"
                                }}
                            />

                            <div>
                                <h4>{item.name}</h4>
                                <p>₹{item.price}</p>
                            </div>
                        </div>

                            <span>{item.name}</span>
                            <span>₹{item.price}</span>
                            <span>Qty: {item.qty}</span>

                            <button onClick={() => removeItem(index)}>
                                Remove
                            </button>

                            <button onClick={() => increaseQty(item.id)}>+</button>
                                <span style={{ margin: "0 10px" }}>{item.qty}</span>
                            <button onClick={() => decreaseQty(item.id)}>-</button>
                        </div>
                    ))}
                    <div style={{
                        marginTop: "20px",
                        padding: "20px",
                        background: "#fff",
                        borderRadius: "10px",
                        boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
                        }}>
                        <h2>Total: ₹{total}</h2>
                        <button style={{
                            marginTop: "10px",
                            padding: "10px 20px",
                            background: "#28a745",
                            color: "#fff",
                            border: "none",
                            borderRadius: "6px",
                            cursor: "pointer"
                        }}>
                            Checkout
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default Cart; 