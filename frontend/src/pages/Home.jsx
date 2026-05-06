import { useState, useEffect } from "react";
import ProductCard from "../components/ProductCard";
import { API_BASE_URL } from "../utils/api";
import { heroImage } from "../utils/productMedia";

const categoryOptions = ["all", "shoes", "shirt", "watch", "trouser"];

const Home = () => {
    const [search, setSearch] = useState("");
    const [category, setCategory] = useState("all");
    const [sort, setSort] = useState("");
    const [products, setProducts] = useState([]);
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearch(search);
            setPage(0);
        }, 500);

        return () => clearTimeout(timer);
    }, [search]);

    useEffect(() => {
        setPage(0);
    }, [category, sort]);

    useEffect(() => {
        const params = new URLSearchParams({
            page,
            size: 4,
            keyword: debouncedSearch,
            category
        });

        if (sort === "low") params.append("sortBy", "price");
        if (sort === "high") {
            params.append("sortBy", "price");
            params.append("sortDir", "desc");
        }

        const url = `${API_BASE_URL}/api/products/filter?${params.toString()}`;
        setLoading(true);
        setError("");

        fetch(url)
            .then((res) => {
                if (!res.ok) throw new Error(`Products request failed with status ${res.status}`);
                return res.json();
            })
            .then((data) => {
                setProducts(data.content || []);
                setTotalPages(Math.max(data.totalPages || 1, 1));
            })
            .catch((err) => {
                console.error(err);
                setError("Products are taking a break right now.");
                setProducts([]);
            })
            .finally(() => setLoading(false));
    }, [debouncedSearch, category, sort, page]);

    return (
        <div className="page page--home">
            <section
                className="home-hero"
                style={{
                    backgroundImage: `linear-gradient(90deg, rgba(26, 17, 13, 0.84), rgba(26, 17, 13, 0.42)), url(${heroImage})`
                }}
            >
                <div className="home-hero__content">
                    <span className="eyebrow">New season edit</span>
                    <h1>Move from browse to checkout with a sharper storefront.</h1>
                    <p>
                        Search, filter, and build your cart without losing the thread. Every pick stays tied to your account.
                    </p>
                </div>
            </section>

            <section className="home-controls">
                <div className="featured-banner">
                    <div className="featured-banner__copy">
                        <span className="eyebrow">Featured products</span>
                        <h2 className="section-title">Fresh arrivals for work, weekends, and everything between</h2>
                        <p className="section-copy">
                            Curated essentials with clean filters, fast paging, and a cart that stays in step with your account.
                        </p>
                    </div>
                    <div className="featured-banner__stats">
                        <div className="featured-stat">
                            <strong>{products.length}</strong>
                            <span>Picks on this page</span>
                        </div>
                        <div className="featured-stat">
                            <strong>{page + 1}</strong>
                            <span>Current page</span>
                        </div>
                        <div className="featured-stat">
                            <strong>{totalPages}</strong>
                            <span>Total pages</span>
                        </div>
                    </div>
                </div>

                <div className="filters-grid">
                    <label className="field">
                        <span className="field__label">Search</span>
                        <input
                            type="text"
                            placeholder="Search products"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="input"
                        />
                    </label>

                    <label className="field field--select">
                        <span className="field__label">Sort</span>
                        <select value={sort} onChange={(e) => setSort(e.target.value)} className="select">
                            <option value="">Featured first</option>
                            <option value="low">Price: Low to high</option>
                            <option value="high">Price: High to low</option>
                        </select>
                    </label>
                </div>

                <div className="chip-row">
                    {categoryOptions.map((cat) => (
                        <button
                            key={cat}
                            onClick={() => setCategory(cat)}
                            className={`chip ${category === cat ? "chip--active" : ""}`}
                        >
                            {cat === "all" ? "All picks" : cat.charAt(0).toUpperCase() + cat.slice(1)}
                        </button>
                    ))}
                </div>
            </section>

            <section className="catalog-section">
                {loading ? (
                    <div className="state-panel">
                        <h3>Loading the rack</h3>
                        <p>Your next picks are on the way.</p>
                    </div>
                ) : error ? (
                    <div className="state-panel">
                        <h3>Could not load products</h3>
                        <p>{error}</p>
                    </div>
                ) : products.length === 0 ? (
                    <div className="state-panel">
                        <h3>No matches yet</h3>
                        <p>Try a different keyword or switch the category to open the catalog back up.</p>
                    </div>
                ) : (
                    <div className="product-grid">
                        {products.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                )}
            </section>

            <div className="pagination">
                <button
                    onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                    disabled={page === 0 || loading}
                    className="button button--ghost"
                >
                    Previous
                </button>

                <span className="pagination__label">Page {page + 1} of {totalPages}</span>

                <button
                    onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
                    disabled={page >= totalPages - 1 || loading}
                    className="button button--ghost"
                >
                    Next
                </button>
            </div>
        </div>
    );
};

export default Home;
