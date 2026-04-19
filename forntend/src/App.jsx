import {BrowserRouter, Route, Routes} from "react-router-dom";
import Cart from "./pages/Cart";
import Home from "./pages/Home";
import Navbar from "./components/Navbar";
import Checkout from "./pages/Checkout";
import Login from "./pages/Login";
import PrivateRoute from "./components/PrivateRoute";

function App() {
  return (
    <BrowserRouter>
      <Navbar />
        <Routes>
          <Route path="/" element={<Home/>}/>

          <Route path="/cart" element={
            <PrivateRoute>
              <Cart/>
            </PrivateRoute>
          }/>

          <Route path="/checkout" element={
            <PrivateRoute>
              <Checkout />
            </PrivateRoute>
          } />

          <Route path="/login" element={<Login />} />
        </Routes>
      
    </BrowserRouter>
  );
}

export default App;