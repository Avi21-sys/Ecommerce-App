import { useContext } from "react";
import { CartContext } from "../context/CartContext";
import { getProductMedia } from "../utils/productMedia";

const formatPrice = (value) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0
    }).format(value);

const ProductCard = ({ product }) => {
    const { addToCart } = useContext(CartContext);
    const { image, label } = getProductMedia(product);

    return (
        <article className="product-card">
            <div className="product-card__media">
                <img src={image} alt={product.name} className="product-card__image" />
                <span className="product-card__label">{label}</span>
            </div>

            <div className="product-card__body">
                <div className="product-card__copy">
                    <h3 className="product-card__title">{product.name}</h3>
                    <p className="product-card__subtitle">Handpicked for everyday rotation</p>
                </div>

                <div className="product-card__footer">
                    <p className="product-card__price">{formatPrice(product.price)}</p>
                    <button onClick={() => addToCart(product)} className="button button--primary button--full">
                        Add to cart
                    </button>
                </div>
            </div>
        </article>
    );
};

export default ProductCard;
