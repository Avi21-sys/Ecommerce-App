import { Navigate } from "react-router-dom";
import { getValidToken, isAdmin } from "../utils/api";

const PrivateRoute = ({ children, requireAdmin = false }) => {
    const token = getValidToken();

    if (!token) {
        return <Navigate to="/login" />;
    }

    if (requireAdmin && !isAdmin()) {
        return <Navigate to="/" />;
    }

    return children;
};

export default PrivateRoute;
