import {BrowserRouter, Route, Routes} from "react-router-dom";
import Cart from "./pages/Cart";
import Home from "./pages/Home";
import Navbar from "./components/Navbar";
import Checkout from "./pages/Checkout";

function App() {
  return (
    <BrowserRouter>
      <Navbar />
        <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/cart" element={<Cart/>}/>
          <Route path="/checkout" element={<Checkout />} />
        </Routes>
      
    </BrowserRouter>
  );
}

export default App;