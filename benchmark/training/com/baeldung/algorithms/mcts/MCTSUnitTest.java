package com.baeldung.algorithms.mcts;


import Board.DRAW;
import Board.P1;
import com.baeldung.algorithms.mcts.montecarlo.MonteCarloTreeSearch;
import com.baeldung.algorithms.mcts.montecarlo.State;
import com.baeldung.algorithms.mcts.montecarlo.UCT;
import com.baeldung.algorithms.mcts.tictactoe.Board;
import com.baeldung.algorithms.mcts.tictactoe.Position;
import com.baeldung.algorithms.mcts.tree.Tree;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MCTSUnitTest {
    private Tree gameTree;

    private MonteCarloTreeSearch mcts;

    @Test
    public void givenStats_whenGetUCTForNode_thenUCTMatchesWithManualData() {
        double uctValue = 15.79;
        Assert.assertEquals(UCT.uctValue(600, 300, 20), uctValue, 0.01);
    }

    @Test
    public void giveninitBoardState_whenGetAllPossibleStates_thenNonEmptyList() {
        State initState = gameTree.getRoot().getState();
        List<State> possibleStates = initState.getAllPossibleStates();
        Assert.assertTrue(((possibleStates.size()) > 0));
    }

    @Test
    public void givenEmptyBoard_whenPerformMove_thenLessAvailablePossitions() {
        Board board = new Board();
        int initAvailablePositions = board.getEmptyPositions().size();
        board.performMove(P1, new Position(1, 1));
        int availablePositions = board.getEmptyPositions().size();
        Assert.assertTrue((initAvailablePositions > availablePositions));
    }

    @Test
    public void givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw() {
        Board board = new Board();
        int player = Board.P1;
        int totalMoves = (Board.DEFAULT_BOARD_SIZE) * (Board.DEFAULT_BOARD_SIZE);
        for (int i = 0; i < totalMoves; i++) {
            board = mcts.findNextMove(board, player);
            if ((board.checkStatus()) != (-1)) {
                break;
            }
            player = 3 - player;
        }
        int winStatus = board.checkStatus();
        Assert.assertEquals(winStatus, DRAW);
    }

    @Test
    public void givenEmptyBoard_whenLevel1VsLevel3_thenLevel3WinsOrDraw() {
        Board board = new Board();
        MonteCarloTreeSearch mcts1 = new MonteCarloTreeSearch();
        mcts1.setLevel(1);
        MonteCarloTreeSearch mcts3 = new MonteCarloTreeSearch();
        mcts3.setLevel(3);
        int player = Board.P1;
        int totalMoves = (Board.DEFAULT_BOARD_SIZE) * (Board.DEFAULT_BOARD_SIZE);
        for (int i = 0; i < totalMoves; i++) {
            if (player == (Board.P1))
                board = mcts3.findNextMove(board, player);
            else
                board = mcts1.findNextMove(board, player);

            if ((board.checkStatus()) != (-1)) {
                break;
            }
            player = 3 - player;
        }
        int winStatus = board.checkStatus();
        Assert.assertTrue(((winStatus == (Board.DRAW)) || (winStatus == (Board.P1))));
    }
}
