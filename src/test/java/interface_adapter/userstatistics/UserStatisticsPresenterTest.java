package interface_adapter.userstatistics;

import org.junit.jupiter.api.Test;
import use_case.userstatistics.UserStatisticsOutputData;

import javax.swing.ImageIcon;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserStatisticsPresenterTest {

    private UserStatisticsOutputData sampleOutput() {
        UserStatisticsOutputData.GameStatData game = new UserStatisticsOutputData.GameStatData(
                "Game A", 10, 2, new ImageIcon());
        UserStatisticsOutputData.PlaytimePoint point = new UserStatisticsOutputData.PlaytimePoint(5, 1);

        return new UserStatisticsOutputData(
                "name",
                99L,
                new ImageIcon(),
                42,
                "message",
                game,
                "most played msg",
                List.of(game),
                new int[] {1, 2, 3, 4, 5, 6},
                new String[] {"a", "b", "c", "d", "e", "f"},
                game,
                30,
                "recent msg",
                List.of(game),
                List.of(point),
                List.of(game),
                List.of(game)
        );
    }

    @Test
    void prepareSuccessViewPopulatesState() {
        UserStatisticsViewModel viewModel = new UserStatisticsViewModel();
        UserStatisticsPresenter presenter = new UserStatisticsPresenter(viewModel);

        presenter.prepareSuccessView(sampleOutput());

        UserStatisticsState state = viewModel.getState();
        assertNotNull(state.getStatistics());
        assertEquals("name", state.getStatistics().getUsername());
        assertEquals("", state.getErrorMessage());
        assertEquals(1, state.getStatistics().getTopFiveGames().size());
    }

    @Test
    void prepareFailureViewSetsError() {
        UserStatisticsViewModel viewModel = new UserStatisticsViewModel();
        UserStatisticsPresenter presenter = new UserStatisticsPresenter(viewModel);

        presenter.prepareFailureView("boom");

        UserStatisticsState state = viewModel.getState();
        assertNull(state.getStatistics());
        assertEquals("boom", state.getErrorMessage());
    }
}
