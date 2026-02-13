import {
  getLocalTimeZone,

} from "@internationalized/date";

export function useDateWeek(){
function getLastWeekDates(selectedDate) {
    const jsDate = selectedDate.toDate(getLocalTimeZone());
    console.log("data selectata giig", jsDate);
    const days = [];
  
    for (let i = 6; i >= 0; i--) {
      const d = new Date(jsDate);
      d.setDate(jsDate.getDate() - i); //ca sa ia data mai inainte
      const dayString =
        d.getFullYear() +
        "-" +
        String(d.getMonth() + 1).padStart(2, "0") +
        "-" +
        String(d.getDate()).padStart(2, "0");
      days.push(dayString);
    }
  
    console.log("data", days);
    return days;
  }

  return {
    getLastWeekDates
  }
}