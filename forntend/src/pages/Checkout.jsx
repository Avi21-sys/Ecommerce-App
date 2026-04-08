import { useEffect, useState } from "react";

const Checkout = () => {

    const [cart, setCart] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8081/api/cart")
            .then(res => res.json())
            .then(data => setCart(data));
    }, []);

    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    const placeOrder = () => {

        alert("Order placed successfully!");

    // Clear cart (call delete for each item)
        cart.forEach(item => {
            fetch(`http://localhost:8081/api/cart/${item.id}`, {
                method: "DELETE"
            });
        });

        setCart([]);
    };

    return (
        <div style={{ padding: "30px" }}>
            <h2>Checkout</h2>

            {cart.map(item => (
                <div key={item.id} style={{
                    display: "flex",
                    justifyContent: "space-between",
                    marginBottom: "10px"
                }}>
                    <span>{item.productName}</span>
                    <span>{item.quantity} x ₹{item.price}</span>
                </div>
            ))}

            <h3>Total: ₹{total}</h3>

            <button onClick={placeOrder} style={{
                marginTop: "20px",
                padding: "10px 20px",
                background: "#28a745",
                color: "#fff",
                border: "none",
                borderRadius: "6px"
            }}>
                Place Order
            </button>
        </div>
    );
};

export default Checkout;