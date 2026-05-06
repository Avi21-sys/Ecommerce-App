import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL, fetchWithAuth } from "../utils/api";
import { triggerCartUpdate } from "../context/CartContext";
import { getProductMedia } from "../utils/productMedia";

const formatPrice = (value) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0
    }).format(value);

const Checkout = () => {
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(true);
    const [placingOrder, setPlacingOrder] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        fetchWithAuth(`${API_BASE_URL}/api/cart`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`Cart request failed with status ${res.status}`);
                }
                return res.json();
            })
            .then((data) => setCart(data))
            .catch((err) => console.error(err))
            .finally(() => setLoading(false));
    }, []);

    const total = cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0);

    const placeOrder = async () => {
        try {
            setPlacingOrder(true);

            const order = {
                totalAmount: cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0),
                items: cart.map((item) => ({
                    productId: item.productId,
                    productName: item.productName,
                    price: item.price,
                    quantity: item.quantity
                }))
            };

            const res = await fetchWithAuth(`${API_BASE_URL}/api/orders`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(order)
            });

            if (!res.ok) {
                throw new Error("Order failed");
            }

            await Promise.all(
                cart.map((item) =>
                    fetchWithAuth(`${API_BASE_URL}/api/cart/${item.id}`, {
                        method: "DELETE"
                    })
                )
            );

            setCart([]);
            triggerCartUpdate();
            alert("Order placed successfully!");
            navigate("/");
        } catch (err) {
            console.error(err);
            alert("Something went wrong!");
        } finally {
            setPlacingOrder(false);
        }
    };

    return (
        <div className="page page--checkout">
            <section className="section-header">
                <span className="eyebrow">Checkout</span>
                <h1 className="section-title">Review the order, then place it in one clean step</h1>
                <p className="section-copy">Your cart stays connected to your account, so nothing slips away while you review.</p>
            </section>

            {loading ? (
                <div className="state-panel">
                    <h3>Preparing checkout</h3>
                    <p>Your order summary is almost ready.</p>
                </div>
            ) : cart.length === 0 ? (
                <div className="state-panel">
                    <h3>No items to check out</h3>
                    <p>Add something to your cart first and we will pick it up here.</p>
                    <button onClick={() => navigate("/")} className="button button--primary">Go shopping</button>
                </div>
            ) : (
                <div className="checkout-layout">
                    <section className="checkout-list">
                        {cart.map((item) => {
                            const { image } = getProductMedia({ productName: item.productName });

                            return (
                                <article key={item.id} className="checkout-item">
                                    <img src={image} alt={item.productName} className="checkout-item__image" />
                                    <div className="checkout-item__copy">
                                        <h3>{item.productName}</h3>
                                        <p>{item.quantity} x {formatPrice(item.price)}</p>
                                    </div>
                                    <strong>{formatPrice(Number(item.price) * item.quantity)}</strong>
                                </article>
                            );
                        })}
                    </section>

                    <aside className="summary-panel">
                        <span className="eyebrow">Payment summary</span>
                        <h2>Ready when you are</h2>
                        <div className="summary-row">
                            <span>Items total</span>
                            <strong>{formatPrice(total)}</strong>
                        </div>
                        <div className="summary-row">
                            <span>Delivery</span>
                            <strong>Free</strong>
                        </div>
                        <div className="summary-row summary-row--total">
                            <span>Final total</span>
                            <strong>{formatPrice(total)}</strong>
                        </div>
                        <button
                            onClick={placeOrder}
                            className="button button--primary button--full"
                            disabled={placingOrder}
                        >
                            {placingOrder ? "Placing order..." : "Place order"}
                        </button>
                    </aside>
                </div>
            )}
        </div>
    );
};

export default Checkout;
