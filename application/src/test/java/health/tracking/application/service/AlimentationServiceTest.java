package health.tracking.application.service;

import health.tracking.application.dto.AlimentationDTO;
import health.tracking.application.entities.*;
import health.tracking.application.mapper.AlimentationMapper;
import health.tracking.application.repository.AlimentationRepository;
import health.tracking.application.repository.GoalLogRepository;
import health.tracking.application.repository.GoalRepository;
import health.tracking.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlimentationServiceTest {

    @Mock
    private AlimentationRepository alimentationRepository;

    @Mock
    private AlimentationMapper alimentationMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalLogRepository goalLogRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private AlimentationService alimentationService;

    private User mockUser;
    private String username = "andrei123";
    private String dateStr = "2026-06-28";
    private LocalDate localDate = LocalDate.parse(dateStr);

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setEmail("andrei@test.com");
    }

    @Test
    @DisplayName("TEST-NUTRIȚIE-1: Returnează lista de alimente dacă user-ul există")
    void testFindAlimentationDate_Success() {
        // Arrange
        Alimentation alimentation = new Alimentation();
        alimentation.setNameProduct("Mere");

        AlimentationDTO dto = new AlimentationDTO();
        dto.setNameProduct("Mere");

        when(userRepository.findByEmailOrUsername(username, username)).thenReturn(mockUser);
        when(alimentationRepository.findByUserAndMealDate(mockUser, localDate)).thenReturn(List.of(alimentation));
        when(alimentationMapper.toDto(alimentation)).thenReturn(dto);

        // Act
        List<AlimentationDTO> result = alimentationService.findAlimentationDate(username, dateStr);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mere", result.get(0).getNameProduct());

        verify(userRepository, times(1)).findByEmailOrUsername(username, username);
        verify(alimentationRepository, times(1)).findByUserAndMealDate(mockUser, localDate);
    }

    @Test
    @DisplayName("TEST-NUTRIȚIE-2: Salvare date nutriție și goalLog")
    void testSaveAlimentation_WithExistingGoal() {
        // Arrange
        AlimentationDTO inputDto = new AlimentationDTO();
        inputDto.setNameProduct("Banana");
        inputDto.setCalories(200.0);

        Goal mockGoal = new Goal();
        mockGoal.setType("Alimentation");

        GoalLog mockGoalLog = new GoalLog();
        mockGoalLog.setCurrentValue(500);

        when(userRepository.findByEmailOrUsername(username, username)).thenReturn(mockUser);
        when(goalRepository.findByUserAndType(mockUser, "Alimentation")).thenReturn(mockGoal);
        when(goalLogRepository.findByUserAndGoalAndDate(mockUser, mockGoal, localDate)).thenReturn(mockGoalLog);

        // Act
        String response = alimentationService.saveAlimentation(inputDto, username, dateStr);

        // Assert
        assertEquals("alimentare si goal log adaugate cu success!", response);
        assertEquals(700, mockGoalLog.getCurrentValue()); // 500 inițial + 200 din DTO

        verify(alimentationRepository, times(1)).save(any(Alimentation.class));
        verify(goalLogRepository, times(1)).save(mockGoalLog);
    }

    @Test
    @DisplayName("TEST-NUTRIȚIE-3: Eliminarea unui aliment dacă nu mai există entitatea")
    void testDeleteAlimentationById_Success() {
        // Arrange
        Long targetId = 1L;
        // Simulăm că după ștergere, findById returnează Empty
        when(alimentationRepository.findById(targetId)).thenReturn(Optional.empty());

        // Act
        boolean isDeleted = alimentationService.deleteAlimentationById(targetId);

        // Assert
        assertTrue(isDeleted);
        verify(alimentationRepository, times(1)).deleteById(targetId);
        verify(alimentationRepository, times(1)).findById(targetId);
    }

    @Test
    @DisplayName("Should throw exception when deletion fails")
    void testDeleteAlimentationById_ThrowsException() {
        // Arrange
        Long targetId = 1L;
        // Simulăm că entitatea încă există în baza de date după deleteById (eroare)
        when(alimentationRepository.findById(targetId)).thenReturn(Optional.of(new Alimentation()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alimentationService.deleteAlimentationById(targetId);
        });

        assertEquals("Eroare la stergerea datelor de nutritie", exception.getMessage());
        verify(alimentationRepository, times(1)).deleteById(targetId);
    }
}