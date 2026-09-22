import { Routes,Route,Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import Login from "./pages/Login"; import Register from "./pages/Register"; import Dashboard from "./pages/Dashboard"; import Leaves from "./pages/Leaves"; import LeaveForm from "./pages/LeaveForm"; import Employees from "./pages/Employees"; import Departments from "./pages/Departments";
function Private({children,roles}){return <ProtectedRoute roles={roles}><Layout>{children}</Layout></ProtectedRoute>}
export default function App(){return <AuthProvider><Routes><Route path="/login" element={<Login/>}/><Route path="/register" element={<Register/>}/><Route path="/dashboard" element={<Private><Dashboard/></Private>}/><Route path="/leaves" element={<Private><Leaves/></Private>}/><Route path="/leaves/apply" element={<Private roles={['EMPLOYEE']}><LeaveForm/></Private>}/><Route path="/employees" element={<Private roles={['HR']}><Employees/></Private>}/><Route path="/departments" element={<Private roles={['HR']}><Departments/></Private>}/><Route path="*" element={<Navigate to="/dashboard" replace/>}/></Routes></AuthProvider>}
