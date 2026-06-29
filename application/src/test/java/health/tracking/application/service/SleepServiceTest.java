package health.tracking.application.service;

import health.tracking.application.dto.SleepDTO;
import health.tracking.application.entities.Sleep;
import health.tracking.application.entities.User;
import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.SleepRepository;
import health.tracking.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SleepServiceTest {

    @Mock
    private SleepRepository sleepRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalLogRepository goalLogRepository;

    @InjectMocks
    private SleepService sleepService;

    private SleepDTO validDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validDto = new SleepDTO();
        validDto.setQuality(4);
        validDto.setHoursSlept(8);
        validDto.setStress(2);
        validDto.setMorningEnergy(4);

        mockUser = new User();
        mockUser.setUsername("alex_fit");
        mockUser.setEmail("alex_fit");
    }

    @Test
    @DisplayName("TEST-SLEEPSERVICE-1: Salvare cu succes a sesiunii de somn")
    void saveSleep_Success() {
        // Given
        when(userRepository.findByEmailOrUsername("alex_fit", "alex_fit")).thenReturn(mockUser);
        when(goalRepository.findByUserAndType(mockUser, "Sleep")).thenReturn(null);
        when(sleepRepository.save(any(Sleep.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String response = sleepService.saveSleep(validDto, "alex_fit", "2026-06-17");

        // Then
        assertNotNull(response);
        assertTrue(response.contains("succes"));
        verify(sleepRepository, times(1)).save(any(Sleep.class));
    }

    @Test
    @DisplayName("TEST-SLEEPSERVICE-2: Eșuare salvare când utilizatorul nu există")
    void saveSleep_UserNotFound() {
        // Given
        when(userRepository.findByEmailOrUsername("user_inexistent", "user_inexistent")).thenReturn(null);

        // When
        String response = sleepService.saveSleep(validDto, "user_inexistent", "2026-06-17");

        // Then
        assertTrue(response.contains("Eroare"));
        verify(sleepRepository, never()).save(any(Sleep.class));
    }

    @Test
    @DisplayName("TEST-SLEEPSERVICE-3: Verificare mapare corectă a noilor atribute (Stress și Energie)")
    void saveSleep_VerifyFieldsMapping() {
        // Given
        // Folosim consecvent mockUser configurat în @BeforeEach
        when(userRepository.findByEmailOrUsername("rusuandrei", "rusuandrei")).thenReturn(mockUser);
        when(goalRepository.findByUserAndType(mockUser, "Sleep")).thenReturn(null);

        // When
        sleepService.saveSleep(validDto, "rusuandrei", "2026-06-14");

        // Then
        verify(sleepRepository).save(argThat(sleep ->
                sleep.getStress() == 2 &&
                        sleep.getMorningEnergy() == 4 &&
                        sleep.getQuality() == 4
        ));
    }
} // Toate testele sunt acum corect încadrate în interiorul acestei acolade finale