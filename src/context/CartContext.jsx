import { createContext, useState } from "react";

export const CartContext = createContext();

const CartProvider = ({children}) => {
    const[cart, setCart] = useState([]);

    console.log(cart);
    const addToCart = (product) => {
        setCart(prev => {
            const exists = prev.find(item => item.id === product.id);
            if(exists) return prev.map(item => 
                item.id === product.id 
                ? {...item, qty: item.qty +1} 
                :item
            );
            return [...prev, {...product, qty: 1}];
        });
    };
    
    return(
        <CartContext.Provider value = {{cart, setCart, addToCart}}>
            {children}
        </CartContext.Provider>
    )

};

export default CartProvider;