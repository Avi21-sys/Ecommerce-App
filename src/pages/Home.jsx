import products from "../data/products";
import ProductCard from "../components/ProductCard";
import { useState } from "react";

const Home = () =>{
    const [search, setSearch] = useState("");
    
    return(
        <div style={{ background: "#f5f5f5", minHeight: "100vh" }}>
            <div style={{ padding: "20px 30px" }}>
                <h2>Featured Products</h2>
            </div>


            <div style={{ padding: "0 30px 20px" }}>
                <input
                    type="text"
                    placeholder="Search products..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "1px solid #ccc",
                    fontSize: "16px"
                    }}
                />
            </div>

            <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
                gap: "25px",
                padding: "30px",
                background: "#f5f5f5",
                alignItems: "stretch"
            }}>
                {products.filter(product => 
                product.name.toLowerCase().includes(search.toLowerCase())
            ).map(product => (
                    <ProductCard key={product.id} product={product}/>
                ))}

            </div>
        </div>
    )
};

export default Home;