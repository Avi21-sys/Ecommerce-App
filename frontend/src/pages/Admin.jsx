import { useEffect, useMemo, useState } from "react";
import { API_BASE_URL, fetchWithAuth } from "../utils/api";

const emptyForm = {
    name: "",
    price: "",
    category: "shoes",
    image: ""
};

const formatPrice = (value) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0
    }).format(Number(value || 0));

const Admin = () => {
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const loadAdminData = async () => {
        try {
            setLoading(true);
            setError("");

            const [productsRes, ordersRes] = await Promise.all([
                fetchWithAuth(`${API_BASE_URL}/api/products`),
                fetchWithAuth(`${API_BASE_URL}/api/orders/admin/all`)
            ]);

            if (!productsRes.ok || !ordersRes.ok) {
                throw new Error("Failed to load admin data");
            }

            const [productsData, ordersData] = await Promise.all([
                productsRes.json(),
                ordersRes.json()
            ]);

            setProducts(productsData);
            setOrders(ordersData);
        } catch (err) {
            console.error(err);
            setError("Admin data could not be loaded.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadAdminData();
    }, []);

    const stats = useMemo(() => {
        const totalRevenue = orders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0);
        const activeProducts = products.length;
        const placedOrders = orders.filter((order) => order.status === "PLACED").length;
        const cancelledOrders = orders.filter((order) => order.status === "CANCELLED").length;

        return {
            totalRevenue,
            activeProducts,
            placedOrders,
            cancelledOrders,
            totalOrders: orders.length
        };
    }, [orders, products]);

    const resetForm = () => {
        setForm(emptyForm);
        setEditingId(null);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            setSaving(true);

            const payload = {
                name: form.name.trim(),
                price: Number(form.price),
                category: form.category,
                image: form.image.trim()
            };

            const url = editingId
                ? `${API_BASE_URL}/api/products/${editingId}`
                : `${API_BASE_URL}/api/products`;

            const method = editingId ? "PUT" : "POST";

            const res = await fetchWithAuth(url, {
                method,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                throw new Error("Failed to save product");
            }

            resetForm();
            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Could not save the product.");
        } finally {
            setSaving(false);
        }
    };

    const handleEdit = (product) => {
        setEditingId(product.id);
        setForm({
            name: product.name,
            price: product.price,
            category: product.category,
            image: product.image || ""
        });
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const handleDelete = async (id) => {
        try {
            const res = await fetchWithAuth(`${API_BASE_URL}/api/products/${id}`, {
                method: "DELETE"
            });

            if (!res.ok) {
                throw new Error("Failed to delete product");
            }

            if (editingId === id) {
                resetForm();
            }

            await loadAdminData();
        } catch (err) {
            console.error(err);
            alert("Could not delete the product.");
        }
    };

    return (
        <div className="page page--admin">
            <section className="section-header">
                <span className="eyebrow">Admin dashboard</span>
                <h1 className="section-title">Run the store from one place</h1>
                <p className="section-copy">
                    Track order flow, monitor revenue, and keep the product catalog sharp without leaving the app.
                </p>
            </section>

            {loading ? (
                <div className="state-panel">
                    <h3>Loading admin workspace</h3>
                    <p>Pulling products and orders into the dashboard.</p>
                </div>
            ) : error ? (
                <div className="state-panel">
                    <h3>Admin data unavailable</h3>
                    <p>{error}</p>
                </div>
            ) : (
                <>
                    <section className="admin-stats">
                        <article className="admin-stat-card">
                            <span>Total orders</span>
                            <strong>{stats.totalOrders}</strong>
                        </article>
                        <article className="admin-stat-card">
                            <span>Revenue</span>
                            <strong>{formatPrice(stats.totalRevenue)}</strong>
                        </article>
                        <article className="admin-stat-card">
                            <span>Active products</span>
                            <strong>{stats.activeProducts}</strong>
                        </article>
                        <article className="admin-stat-card">
                            <span>Placed orders</span>
                            <strong>{stats.placedOrders}</strong>
                        </article>
                    </section>

                    <section className="admin-layout">
                        <div className="admin-panel">
                            <div className="admin-panel__head">
                                <div>
                                    <span className="eyebrow">Manage products</span>
                                    <h2>{editingId ? "Edit product" : "Add new product"}</h2>
                                </div>
                            </div>

                            <form className="admin-form" onSubmit={handleSubmit}>
                                <label className="field">
                                    <span className="field__label">Product name</span>
                                    <input
                                        className="input"
                                        value={form.name}
                                        onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))}
                                        placeholder="Classic sneakers"
                                        required
                                    />
                                </label>

                                <div className="admin-form__grid">
                                    <label className="field">
                                        <span className="field__label">Price</span>
                                        <input
                                            className="input"
                                            type="number"
                                            min="1"
                                            value={form.price}
                                            onChange={(e) => setForm((prev) => ({ ...prev, price: e.target.value }))}
                                            placeholder="2999"
                                            required
                                        />
                                    </label>

                                    <label className="field">
                                        <span className="field__label">Category</span>
                                        <select
                                            className="select"
                                            value={form.category}
                                            onChange={(e) => setForm((prev) => ({ ...prev, category: e.target.value }))}
                                        >
                                            <option value="shoes">Shoes</option>
                                            <option value="shirt">Shirt</option>
                                            <option value="watch">Watch</option>
                                            <option value="trouser">Trouser</option>
                                        </select>
                                    </label>
                                </div>

                                <label className="field">
                                    <span className="field__label">Image URL</span>
                                    <input
                                        className="input"
                                        value={form.image}
                                        onChange={(e) => setForm((prev) => ({ ...prev, image: e.target.value }))}
                                        placeholder="Optional image URL"
                                    />
                                </label>

                                <div className="admin-form__actions">
                                    <button className="button button--primary" type="submit" disabled={saving}>
                                        {saving ? "Saving..." : editingId ? "Update product" : "Add product"}
                                    </button>
                                    {editingId && (
                                        <button className="button button--secondary" type="button" onClick={resetForm}>
                                            Cancel edit
                                        </button>
                                    )}
                                </div>
                            </form>

                            <div className="admin-products">
                                {products.map((product) => (
                                    <article key={product.id} className="admin-product-card">
                                        <div>
                                            <h3>{product.name}</h3>
                                            <p>{product.category} · {formatPrice(product.price)}</p>
                                        </div>
                                        <div className="admin-product-card__actions">
                                            <button className="button button--ghost-dark" onClick={() => handleEdit(product)}>
                                                Edit
                                            </button>
                                            <button className="button button--danger" onClick={() => handleDelete(product.id)}>
                                                Delete
                                            </button>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        </div>

                        <div className="admin-panel">
                            <div className="admin-panel__head">
                                <div>
                                    <span className="eyebrow">All orders</span>
                                    <h2>Live order stream</h2>
                                </div>
                            </div>

                            <div className="admin-orders">
                                {orders.length === 0 ? (
                                    <div className="state-panel">
                                        <h3>No orders yet</h3>
                                        <p>Orders will show up here as soon as customers check out.</p>
                                    </div>
                                ) : (
                                    orders.map((order) => (
                                        <article key={order.id} className="admin-order-card">
                                            <div className="admin-order-card__top">
                                                <div>
                                                    <h3>Order #{order.id}</h3>
                                                    <p>User ID: {order.userId}</p>
                                                </div>
                                                <div className={`status-pill status-pill--${(order.status || "").toLowerCase()}`}>
                                                    {order.status}
                                                </div>
                                            </div>
                                            <div className="admin-order-card__items">
                                                {order.items?.map((item, index) => (
                                                    <div key={`${order.id}-${item.productId}-${index}`} className="admin-order-line">
                                                        <span>{item.productName}</span>
                                                        <span>{item.quantity} x {formatPrice(item.price)}</span>
                                                    </div>
                                                ))}
                                            </div>
                                            <div className="admin-order-card__footer">
                                                <strong>{formatPrice(order.totalAmount)}</strong>
                                            </div>
                                        </article>
                                    ))
                                )}
                            </div>
                        </div>
                    </section>
                </>
            )}
        </div>
    );
};

export default Admin;
