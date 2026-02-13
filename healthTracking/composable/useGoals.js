import { useAuth } from "./useAuth";


export function useGoals(){
    const {user} = useAuth();
    const dataGoals=ref(null);
    async function getGoals(){
        if(!user.value){
            return "Nu exista un utilizator cu acest username";
        }
        try{
            const response=await fetch(`http://localhost:8080/goals/${user.value}`);
            if(response.ok){
                 dataGoals.value=await response.json();
            }
        }
        catch(e){
            console.error("Eroare: ", e);
        }
    };
    return {
        dataGoals,
        getGoals
    };
};