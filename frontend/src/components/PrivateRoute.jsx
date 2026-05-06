import { Navigate } from "react-router-dom";
import { getValidToken } from "../utils/api";

const PrivateRoute = ({ children }) => {
    const token = getValidToken();

    if (!token) {
        return <Navigate to="/login" />;
    }

    return children;
};

export default PrivateRoute;
