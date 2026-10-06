import { createContext,useContext,useEffect,useState } from "react";
import { authApi } from "../services/api";
const AuthContext=createContext(null);
export function AuthProvider({children}){
 const [user,setUser]=useState(()=>JSON.parse(localStorage.getItem("leave_user")||"null"));
 const login=async data=>{const r=await authApi.login(data);localStorage.setItem("leave_token",r.data.token);localStorage.setItem("leave_user",JSON.stringify(r.data));setUser(r.data);return r.data;};
 const register=async data=>authApi.register(data);
 const logout=()=>{localStorage.removeItem("leave_token");localStorage.removeItem("leave_user");setUser(null);};
 useEffect(()=>{if(!localStorage.getItem("leave_token"))setUser(null)},[]);
 return <AuthContext.Provider value={{user,login,register,logout,isAuthenticated:!!user}}>{children}</AuthContext.Provider>;
}
export const useAuth=()=>useContext(AuthContext);
