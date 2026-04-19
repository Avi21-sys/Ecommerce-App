import { createContext, useState } from "react";
import { fetchWithAuth } from "../utils/api";

export const CartContext = createContext();

const CartProvider = ({children}) => {
    const[cart, setCart] = useState([]);

    console.log(cart);
    const addToCart = async(product) => {
        try{
            if (!localStorage.getItem("token")) {
                alert("Please login to add items to cart");
                window.location.href = "/login";
                return;
            }

            const res = await fetchWithAuth("http://localhost:8081/api/cart", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    productId: product.id,
                    productName: product.name,
                    price: product.price,
                    quantity: 1
                })
            });

            if (!res.ok) {
                throw new Error(`Add to cart failed with status ${res.status}`);
            }

            const data = await res.json();
            console.log("Added:", data);
        } catch(err){
            console.error("Error: ",err)
        }
    };

    const removeFromCart = async (id) => {
        try {
            await fetchWithAuth(`http://localhost:8081/api/cart/${id}`, {
            method: "DELETE"
        });

        console.log("Item removed");

        } catch (err) {
            console.error("Error:", err);
        }
    };
    
    return(
        <CartContext.Provider value = {{cart, setCart, addToCart}}>
            {children}
        </CartContext.Provider>
    )

};

export default CartProvider;
