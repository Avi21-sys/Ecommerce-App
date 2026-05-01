import ProductCard from "../components/ProductCard";
import { useState, useEffect } from "react";
import { API_BASE_URL } from "../utils/api";

const Home = () => {
    const [search, setSearch] = useState("");
    const [category, setCategory] = useState("all");
    const [sort, setSort] = useState("");
    const [products, setProducts] = useState([]);
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearch(search);
            setPage(0); // reset to first page on new search
        }, 500);
        return () => clearTimeout(timer);
    }, [search]);

    // Reset page when category or sort changes
    useEffect(() => {
        setPage(0);
    }, [category, sort]);

    useEffect(() => {
        // Build URL — pass sort to backend so ordering is consistent across pages
        const params = new URLSearchParams({
            page,
            size: 5,
            keyword: debouncedSearch,
            category,
        });

        // Only add sort param when selected so backend can handle it
        if (sort === "low") params.append("sortBy", "price");
        if (sort === "high") { params.append("sortBy", "price"); params.append("sortDir", "desc"); }

        const url = `${API_BASE_URL}/api/products/filter?${params.toString()}`;

        fetch(url)
            .then(res => {
                if (!res.ok) throw new Error(`Products request failed with status ${res.status}`);
                return res.json();
            })
            .then(data => {
                setProducts(data.content);
                setTotalPages(data.totalPages);
            })
            .catch(err => console.error(err));
    }, [debouncedSearch, category, sort, page]);

    return (
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

            <div style={{ padding: "0 30px 20px", display: "flex", gap: "10px" }}>
                {["all", "shoes", "shirt", "watch", "trouser"].map(cat => (
                    <button
                        key={cat}
                        onClick={() => setCategory(cat)}
                        style={{
                            padding: "8px 12px",
                            borderRadius: "6px",
                            border: "none",
                            background: category === cat ? "#111" : "#ddd",
                            color: category === cat ? "#fff" : "#000",
                            cursor: "pointer"
                        }}
                    >
                        {cat.toUpperCase()}
                    </button>
                ))}
            </div>

            <div style={{ padding: "0 30px 20px" }}>
                <select
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                    style={{ padding: "10px", borderRadius: "6px" }}
                >
                    <option value="">Sort By</option>
                    <option value="low">Price: Low to High</option>
                    <option value="high">Price: High to Low</option>
                </select>
            </div>

            <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
                gap: "25px",
                padding: "30px",
                background: "#f5f5f5",
                alignItems: "stretch"
            }}>
                {(products || []).map(product => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </div>

            <div style={{ textAlign: "center", marginBottom: "20px" }}>
                <button
                    onClick={() => setPage(prev => Math.max(prev - 1, 0))}
                    disabled={page === 0}
                >
                    Prev
                </button>

                <span style={{ margin: "0 10px" }}>
                    Page {page + 1} of {totalPages}
                </span>

                <button
                    onClick={() => setPage(prev => Math.min(prev + 1, totalPages - 1))}
                    disabled={page === totalPages - 1}
                >
                    Next
                </button>
            </div>
        </div>
    );
};

export default Home;