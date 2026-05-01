import { createContext, useState } from "react";
import { API_BASE_URL, fetchWithAuth } from "../utils/api";

export const CartContext = createContext();

// Custom event for cart updates — avoids polling
export const triggerCartUpdate = () => {
    window.dispatchEvent(new CustomEvent("cart-updated"));
};

const CartProvider = ({ children }) => {
    const [cart, setCart] = useState([]);

    const addToCart = async (product) => {
        try {
            if (!localStorage.getItem("token")) {
                alert("Please login to add items to cart");
                window.location.href = "/login";
                return;
            }

            const res = await fetchWithAuth(`${API_BASE_URL}/api/cart`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    productId: product.id,
                    productName: product.name,
                    price: product.price,
                    quantity: 1
                })
            });

            if (!res.ok) throw new Error(`Add to cart failed with status ${res.status}`);

            const data = await res.json();
            console.log("Added:", data);

            // Fire event so Navbar updates immediately — no polling needed
            triggerCartUpdate();
        } catch (err) {
            console.error("Error:", err);
        }
    };

    const removeFromCart = async (id) => {
        try {
            await fetchWithAuth(`${API_BASE_URL}/api/cart/${id}`, {
                method: "DELETE"
            });
            console.log("Item removed");
            triggerCartUpdate();
        } catch (err) {
            console.error("Error:", err);
        }
    };

    return (
        <CartContext.Provider value={{ cart, setCart, addToCart, removeFromCart }}>
            {children}
        </CartContext.Provider>
    );
};

export default CartProvider;