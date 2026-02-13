import { ref } from "vue";

const user = ref(null);

export function useAuth() {
  // Când se încarcă aplicația, verificăm dacă userul e salvat în localStorage
  if (process.client && !user.value) {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      user.value = storedUser;
    }
  }

  function login(username) {
    user.value = username;
    if (process.client) {
      localStorage.setItem("user", username);
    }
  }

  function logout() {
    user.value = null;
    if (process.client) {
      localStorage.removeItem("user");
    }
  }

  function getUser() {
    return user.value;
  }

  return {
    user,
    login,
    logout,
    getUser,
  };
}
