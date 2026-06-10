import { ref } from "vue"; // Nu uita să imporți ref
import { useAuth } from "./useAuth";


const dataGoals = ref(null);

export function useGoals() {
    const { user, token } = useAuth();

    async function getGoals() {
        // Dacă avem deja date, nu mai facem fetch (opțional, depinde de logica ta)
        if (dataGoals.value) return;

        if (!user.value) {
            console.warn("Nu exista un utilizator logat");
            return;
        }



        try {
            const response = await fetch(`http://localhost:8080/goals/${user.value}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token.value}`
                }
            });
            if (response.ok) {
                dataGoals.value = await response.json();

            }
        } catch (e) {
            console.error("Eroare fetch goals: ", e);
        }
    }

    return {
        dataGoals,
        getGoals
    };
}