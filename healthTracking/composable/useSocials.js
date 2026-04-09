// composables/useSocial.js
import { useAuth } from "~/composable/useAuth.js";

export const useSocial = () => {
    const { token } = useAuth();

    const triggerChallengeUpdate = async (receiverUsername) => {
        try {
            const response = await fetch(`http://localhost:8080/challenge`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`,
                },

                body: JSON.stringify({
                    receiver: receiverUsername,
                    // sender-ul e extras de backend din token,
                    // dar dacă DTO-ul tău cere sender, îl poți pune aici
                })
            });

            if (response.ok) {
                return await response.json();
            }
        } catch (error) {
            console.error("Eroare la update streak:", error);
        }
    };

    return {
        triggerChallengeUpdate
    };
};