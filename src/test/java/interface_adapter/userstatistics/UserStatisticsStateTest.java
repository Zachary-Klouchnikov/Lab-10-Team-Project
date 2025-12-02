package interface_adapter.userstatistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsStateTest {

    @Test
    void gettersAndSettersRoundTrip() {
        UserStatisticsState state = new UserStatisticsState();
        assertNull(state.getStatistics());
        assertEquals("", state.getErrorMessage());

        UserStatisticsViewData.GameStatViewData game = new UserStatisticsViewData.GameStatViewData("t", 1, 0, null);
        UserStatisticsViewData viewData = new UserStatisticsViewData(
                "u",
                "id",
                null,
                1,
                "msg",
                game,
                "mp",
                java.util.List.of(game),
                new int[]{1},
                new String[]{"a"},
                game,
                10,
                5,
                "recent",
                java.util.List.of(game),
                java.util.List.of(),
                java.util.List.of(game),
                java.util.List.of()
        );
        state.setStatistics(viewData);
        state.setErrorMessage("error");

        assertEquals(viewData, state.getStatistics());
        assertEquals("error", state.getErrorMessage());
    }
}
