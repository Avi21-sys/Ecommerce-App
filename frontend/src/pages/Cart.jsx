import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL, fetchWithAuth } from "../utils/api";
import { getProductMedia } from "../utils/productMedia";

const formatPrice = (value) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0
    }).format(value);

const Cart = () => {
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const fetchCart = () => {
        setLoading(true);
        setError("");

        fetchWithAuth(`${API_BASE_URL}/api/cart`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`Cart request failed with status ${res.status}`);
                }
                return res.json();
            })
            .then((data) => setCart(data))
            .catch((err) => {
                console.error(err);
                setError("Cart details could not be loaded.");
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchCart();
    }, []);

    const removeItem = (id) => {
        fetchWithAuth(`${API_BASE_URL}/api/cart/${id}`, {
            method: "DELETE"
        }).then(() => fetchCart());
    };

    const increaseQty = (item) => {
        fetchWithAuth(`${API_BASE_URL}/api/cart`, {
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

        fetchWithAuth(`${API_BASE_URL}/api/cart`, {
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

    const total = cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0);
    const itemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

    return (
        <div className="page page--cart">
            <section className="section-header">
                <span className="eyebrow">Your cart</span>
                <h1 className="section-title">Everything you picked, ready for checkout</h1>
                <p className="section-copy">Adjust quantities, remove anything that does not fit the mood, and keep moving.</p>
            </section>

            {loading ? (
                <div className="state-panel">
                    <h3>Loading your cart</h3>
                    <p>We are pulling your latest picks into place.</p>
                </div>
            ) : error ? (
                <div className="state-panel">
                    <h3>Cart unavailable</h3>
                    <p>{error}</p>
                </div>
            ) : cart.length === 0 ? (
                <div className="state-panel">
                    <h3>Your cart is empty</h3>
                    <p>Add a few products and they will stay with your account.</p>
                    <button onClick={() => navigate("/")} className="button button--primary">Browse products</button>
                </div>
            ) : (
                <div className="cart-layout">
                    <div className="cart-list">
                        {cart.map((item) => {
                            const { image, label } = getProductMedia({ productName: item.productName });

                            return (
                                <article key={item.id} className="cart-item">
                                    <img src={image} alt={item.productName} className="cart-item__image" />
                                    <div className="cart-item__details">
                                        <span className="cart-item__label">{label}</span>
                                        <h3>{item.productName}</h3>
                                        <p>{formatPrice(item.price)} each</p>
                                    </div>

                                    <div className="qty-control" aria-label={`Quantity for ${item.productName}`}>
                                        <button onClick={() => decreaseQty(item)} className="qty-control__button">-</button>
                                        <span className="qty-control__value">{item.quantity}</span>
                                        <button onClick={() => increaseQty(item)} className="qty-control__button">+</button>
                                    </div>

                                    <div className="cart-item__meta">
                                        <strong>{formatPrice(Number(item.price) * item.quantity)}</strong>
                                        <button onClick={() => removeItem(item.id)} className="link-button">Remove</button>
                                    </div>
                                </article>
                            );
                        })}
                    </div>

                    <aside className="summary-panel">
                        <span className="eyebrow">Order summary</span>
                        <h2>{itemCount} item{itemCount === 1 ? "" : "s"} in your bag</h2>
                        <div className="summary-row">
                            <span>Subtotal</span>
                            <strong>{formatPrice(total)}</strong>
                        </div>
                        <div className="summary-row">
                            <span>Delivery</span>
                            <strong>Free</strong>
                        </div>
                        <div className="summary-row summary-row--total">
                            <span>Total</span>
                            <strong>{formatPrice(total)}</strong>
                        </div>
                        <button onClick={() => navigate("/checkout")} className="button button--primary button--full">
                            Continue to checkout
                        </button>
                    </aside>
                </div>
            )}
        </div>
    );
};

export default Cart;
