import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAuth } from "../utils/api";

const Cart = () => {

    const [cart, setCart] = useState([]);

    const fetchCart = () => {
        fetchWithAuth("http://localhost:8081/api/cart")
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Cart request failed with status ${res.status}`);
                }
                return res.json();
            })
            .then(data => setCart(data))
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchCart();
    }, []);

    const removeItem = (id) => {
        fetchWithAuth(`http://localhost:8081/api/cart/${id}`, {
            method: "DELETE"
        })
        .then(() => fetchCart());
    };

    const increaseQty = (item) => {
        fetchWithAuth("http://localhost:8081/api/cart", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                productId: item.productId,
                productName: item.productName,
                price: item.price,
                quantity: 1
            })
        }).then(() => fetchCart());
    };

    const decreaseQty = (item) => {
        if (item.quantity === 1) {
            removeItem(item.id);
            return;
        }

        fetchWithAuth("http://localhost:8081/api/cart", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                productId: item.productId,
                productName: item.productName,
                price: item.price,
                quantity: -1
            })
        }).then(() => fetchCart());
    };

    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    const navigate = useNavigate();

    return(
        <div style={{padding: "20px"}}>
            <h2>Cart Items</h2>

            {cart.length === 0 ?(
                <p>Your cart is empty</p>
            ):(<>
                {cart.map(item => (
                    <div key={item.id} style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        padding: "15px",
                        margin: "15px 0",
                        background: "#fff",
                        borderRadius: "10px",
                        boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
                    }}>

                        <div>
                            <h4>{item.productName}</h4>
                            <p>₹{item.price}</p>
                        </div>

                        <div>
                            <button onClick={() => decreaseQty(item)}>-</button>
                            <span style={{ margin: "0 10px" }}>{item.quantity}</span>
                            <button onClick={() => increaseQty(item)}>+</button>
                        </div>

                        <button onClick={() => removeItem(item.id)}>
                            Remove
                        </button>
                    </div>
                ))}

                <div style={{
                    marginTop: "20px",
                    padding: "20px",
                    background: "#fff",
                    borderRadius: "10px"
                }}>
                    <h2>Total: ₹{total}</h2>
                </div>

                <button onClick={() => navigate("/checkout")}>
                    Checkout
                </button>
            </>)}
        </div>
    );
};

export default Cart;
