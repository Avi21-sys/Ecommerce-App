import { useEffect, useState } from "react";
import { fetchWithAuth } from "../utils/api";

const Checkout = () => {

    const [cart, setCart] = useState([]);

    useEffect(() => {
        fetchWithAuth("http://localhost:8081/api/cart")
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Cart request failed with status ${res.status}`);
                }
                return res.json();
            })
            .then(data => setCart(data));
    }, []);

    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    const placeOrder = async () => {

        try{
            const order = {
                totalAmount: cart.reduce((sum, item) => sum + item.price * item.quantity, 0),
                items: cart.map(item => ({
                    productId: item.productId,
                    productName: item.productName,
                    price: item.price,
                    quantity: item.quantity
                }))
            };

            const res = await fetchWithAuth("http://localhost:8082/api/orders", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(order)
            });

             if (!res.ok) {
                throw new Error("Order failed");
            }

            // Clear cart in parallel (faster)
            await Promise.all(
                cart.map(item =>
                    fetchWithAuth(`http://localhost:8081/api/cart/${item.id}`, {
                        method: "DELETE"
                    })
                )
            );

            alert("Order placed successfully!");
        }
        catch (err) {
            console.error(err);
            alert("Something went wrong!");
        }
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
