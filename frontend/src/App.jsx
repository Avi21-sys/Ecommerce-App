import { BrowserRouter, Route, Routes } from "react-router-dom";
import Cart from "./pages/Cart";
import Home from "./pages/Home";
import Navbar from "./components/Navbar";
import Checkout from "./pages/Checkout";
import Login from "./pages/Login";
import PrivateRoute from "./components/PrivateRoute";
import Admin from "./pages/Admin";

function App() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <Navbar />
        <main className="app-main">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route
              path="/cart"
              element={
                <PrivateRoute>
                  <Cart />
                </PrivateRoute>
              }
            />
            <Route
              path="/checkout"
              element={
                <PrivateRoute>
                  <Checkout />
                </PrivateRoute>
              }
            />
            <Route path="/login" element={<Login />} />
            <Route
              path="/admin"
              element={
                <PrivateRoute requireAdmin>
                  <Admin />
                </PrivateRoute>
              }
            />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
