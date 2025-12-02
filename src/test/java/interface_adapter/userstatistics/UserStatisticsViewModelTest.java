package interface_adapter.userstatistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsViewModelTest {

    @Test
    void initializesWithStateAndViewName() {
        UserStatisticsViewModel vm = new UserStatisticsViewModel();
        assertEquals("userstatistics", vm.getViewName());
        assertNotNull(vm.getState());

        UserStatisticsState newState = new UserStatisticsState();
        newState.setErrorMessage("x");
        vm.setState(newState);
        assertEquals("x", vm.getState().getErrorMessage());
    }
}
