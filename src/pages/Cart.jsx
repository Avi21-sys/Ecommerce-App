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
                            justifyContent: "space-between",
                            margin: "10px 0",
                            padding: "10px",
                            border: "1px solid #ddd"
                        }}>
                            <span>{item.name}</span>
                            <span>₹{item.price}</span>
                            <span>Qty: {item.qty}</span>

                            <button onClick={() => removeItem(index)}>
                                Remove
                            </button>

                            <button onClick={() => increaseQty(item.id)}>+</button>
                                <span>{item.qty}</span>
                            <button onClick={() => decreaseQty(item.id)}>-</button>
                        </div>
                    ))}
                    <h3>Total: ₹{total}</h3>
                </>
            )}
        </div>
    );
};

export default Cart; 