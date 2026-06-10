import { ref } from "vue";

// Folosim useCookie în loc de ref simple pentru a sincroniza serverul cu clientul
export function useAuth() {
  const router = useRouter();


  const user = useCookie("auth_user", { maxAge: 60 * 60 * 24 * 7 }); // se va pastra o saptamana
  const token = useCookie("auth_token", { maxAge: 60 * 60 * 24 * 7 });

  function login(username, userToken, remember = false) { //se va prelua checkboxul remember pentru a verifica daca se mentine sau nu autentificare

    const cookieOptions = remember
        ? { maxAge: 60 * 60 * 24 * 30, path: '/' } // 30 de zile
        : { path: '/' };

    const uCookie = useCookie("auth_user", cookieOptions);
    const tCookie = useCookie("auth_token", cookieOptions);

    uCookie.value = username;
    tCookie.value = userToken;

    user.value = username;
    token.value = userToken;
  }

  function logout() {
    const uCookie = useCookie("auth_user");
    const tCookie = useCookie("auth_token");

    uCookie.value = null;
    tCookie.value = null;
    user.value = null;
    token.value = null;

    router.push("/");
  }

  return { user, token, login, logout };
}