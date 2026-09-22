import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api", headers: { "Content-Type": "application/json" } });
api.interceptors.request.use(config => { const token = localStorage.getItem("leave_token"); if (token) config.headers.Authorization = `Bearer ${token}`; return config; });
api.interceptors.response.use(r => r, error => { if ([401,403].includes(error.response?.status) && !error.config?.url?.includes("/auth/")) { localStorage.removeItem("leave_token"); localStorage.removeItem("leave_user"); if (window.location.pathname !== "/login") window.location.href = "/login"; } return Promise.reject(error); });
export const authApi={register:d=>api.post("/auth/register",d),login:d=>api.post("/auth/login",d)};
export const employeeApi={getAll:s=>api.get(`/employees${s?`?search=${encodeURIComponent(s)}`:""}`),getMe:()=>api.get("/employees/me"),create:d=>api.post("/employees",d),update:(id,d)=>api.put(`/employees/${id}`,d),remove:id=>api.delete(`/employees/${id}`)};
export const departmentApi={getAll:()=>api.get("/departments"),create:d=>api.post("/departments",d),update:(id,d)=>api.put(`/departments/${id}`,d),remove:id=>api.delete(`/departments/${id}`)};
export const leaveApi={getAll:()=>api.get("/leaves"),getPending:()=>api.get("/leaves/pending"),getMy:()=>api.get("/leaves/my"),create:d=>api.post("/leaves",d),approve:(id,note)=>api.put(`/leaves/${id}/approve`,{note}),reject:(id,note)=>api.put(`/leaves/${id}/reject`,{note}),cancel:id=>api.delete(`/leaves/${id}`)};
export default api;
