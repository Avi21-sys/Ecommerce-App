import products from "../data/products";
import ProductCard from "../components/ProductCard";

const Home = () =>{
    return(
        <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
            gap: "20px",
            padding: "20px"
        }}>
            {products.map(product => (
                <ProductCard key={product.id} product={product}/>
            ))}

        </div>
    )
};

export default Home;