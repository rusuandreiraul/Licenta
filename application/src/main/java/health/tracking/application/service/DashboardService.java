package health.tracking.application.service;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.dto.DashboardDailyDTO;
import health.tracking.application.dto.DashboardWeekDTO;
import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Activity;
import health.tracking.application.entities.Alimentation;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.mapper.ActivityMapper;
import health.tracking.application.mapper.AlimentationMapper;
import health.tracking.application.mapper.SleepMapper;
import health.tracking.application.repository.ActivityRepository;
import health.tracking.application.repository.AlimentationRepository;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    SleepRepository sleepRepository;

    @Autowired
    AlimentationRepository alimentationRepository;

    @Autowired
    AlimentationMapper alimentationMapper;

    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    SleepMapper sleepMapper;

    public DashboardDailyDTO getDailyData(String username, String selectedDate) {
        int totalDuration=0;
        double totalCalories=0.0;
        double totalFat=0.0; //string
        double totalCarbos=0.0; //string
        double totalProtein=0.0;

        User u=userRepository.findByEmailOrUsername(username, username);
        if(u!=null){
            LocalDate d= LocalDate.parse(selectedDate);
            DashboardDailyDTO dailyDTO=new DashboardDailyDTO();
            List<Activity> activityList= activityRepository.findActivitiesByActivityDateAndUser(d,u);
            dailyDTO.setActivityDetails(activityList.stream().map(activityMapper::toDto).toList());
            for(Activity a:activityList){
                totalDuration+=a.getDuration();
            }
            dailyDTO.setTotalActivityDuration(totalDuration);

            Sleep s=sleepRepository.findSleepByDateSleepAndUser(d,u);
            if(s!=null) {
                dailyDTO.setSleepDetails(sleepMapper.toDto(s));
                dailyDTO.setTotalHoursSleep(s.getHoursSlept());
            }
            else{
                dailyDTO.setSleepDetails(null);
                dailyDTO.setTotalHoursSleep(0);
            }
            List<Alimentation> a=alimentationRepository.findByUserAndMealDate(u, d);
            List<String> nameProduct=new ArrayList<>();
            if(a!=null){
                for(Alimentation alimentation: a){
                    totalCalories+=alimentation.getCalories();
                    totalFat+=Double.parseDouble(alimentation.getFat());
                    totalCarbos+=Double.parseDouble(alimentation.getCarbohydrates());
                    totalProtein+=alimentation.getProteins();
                    nameProduct.add(alimentation.getNameProduct());
                }

                dailyDTO.setTotalCaloriesConsumed(totalCalories);
                dailyDTO.setTotalCarbosConsumed(totalCarbos);
                dailyDTO.setAlimentationName(nameProduct);
                dailyDTO.setTotalFatConsumed(totalFat);
                dailyDTO.setTotalProteinConsumed(totalProtein);
            }
            return dailyDTO;
        }
        return null;
    }



    public DashboardWeekDTO getWeekData(String username, String selectedDate) {
        User u = userRepository.findByEmailOrUsername(username, username);
        if (u != null) {
            DashboardWeekDTO weekData = new DashboardWeekDTO();
            LocalDate endDate = LocalDate.parse(selectedDate);
            LocalDate startDate = endDate.minusDays(6);

            // 1. Preluăm listele brute din baza de date pentru întreg intervalul
            List<Activity> activities = activityRepository.findByUserAndDateRange(u, startDate, endDate);
            List<SleepDTO> sleeps = sleepRepository.findSleepByDateRange(startDate, endDate, u.getUsername());
            List<Alimentation> foods = alimentationRepository.findByUserAndDateRange(u, startDate, endDate);

            // 2. Pregătim listele finale aliniate de exact 7 elemente
            List<Integer> alignedCalories = new ArrayList<>();
            List<Integer> alignedQualitySleep = new ArrayList<>();
            List<Double> alignedCaloriesConsumed = new ArrayList<>();

            // 3. Iterăm prin fiecare zi din interval (de la startDate la endDate)
            // ATENȚIE: Mergem crescător (startDate -> endDate). Pe front-end avem deja .reverse() care le va întoarce cum ai vrut
            for (LocalDate current = startDate; !current.isAfter(endDate); current = current.plusDays(1)) {
                final LocalDate dateCursor = current;

                // Aliniere Calorii Arse
                int caloriesSum = activities.stream()
                        .filter(a -> a.getActivityDate().equals(dateCursor)) // înlocuiește cu getter-ul tău de dată
                        .mapToInt(Activity::getCalories)               // înlocuiește cu getter-ul tău de calorii
                        .sum();
                alignedCalories.add(caloriesSum);

                // Aliniere Ore/Calitate Somn
                int sleepVal = sleeps.stream()
                        .filter(s -> s.getDateSleep().equals(dateCursor))
                        .mapToInt(SleepDTO::getHoursSlept)
                        .findFirst()
                        .orElse(0);
                alignedQualitySleep.add(sleepVal);

                // Aliniere Calorii Consumate
                double caloriesConsumedSum = foods.stream()
                        .filter(f -> f.getMealDate().equals(dateCursor))
                        .mapToDouble(Alimentation::getCalories)
                        .sum();
                alignedCaloriesConsumed.add(caloriesConsumedSum);
            }

            // 4. Setăm listele perfect structurate în DTO
            weekData.setCalories(alignedCalories);
            weekData.setQualitySleep(alignedQualitySleep);
            weekData.setCaloriesConsumed(alignedCaloriesConsumed);

            return weekData;
        }
        return null;
    }
}
