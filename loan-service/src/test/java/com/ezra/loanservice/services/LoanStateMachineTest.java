package com.ezra.loanservice.services;

import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanStateMachineTest {

    private LoanStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new LoanStateMachine();
    }

    @Test
    void openLoan_canTransitionToClosed() {
        assertThat(stateMachine.isValidTransition(LoanState.OPEN, LoanState.CLOSED)).isTrue();
    }

    @Test
    void openLoan_canTransitionToOverdue() {
        assertThat(stateMachine.isValidTransition(LoanState.OPEN, LoanState.OVERDUE)).isTrue();
    }

    @Test
    void openLoan_canTransitionToCancelled() {
        assertThat(stateMachine.isValidTransition(LoanState.OPEN, LoanState.CANCELLED)).isTrue();
    }

    @Test
    void openLoan_cannotTransitionToWrittenOff() {
        assertThat(stateMachine.isValidTransition(LoanState.OPEN, LoanState.WRITTEN_OFF)).isFalse();
    }

    @Test
    void overdueLoan_canTransitionToClosed() {
        assertThat(stateMachine.isValidTransition(LoanState.OVERDUE, LoanState.CLOSED)).isTrue();
    }

    @Test
    void overdueLoan_canTransitionToWrittenOff() {
        assertThat(stateMachine.isValidTransition(LoanState.OVERDUE, LoanState.WRITTEN_OFF)).isTrue();
    }

    @Test
    void overdueLoan_cannotTransitionToOpen() {
        assertThat(stateMachine.isValidTransition(LoanState.OVERDUE, LoanState.OPEN)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = LoanState.class, names = {"CLOSED", "CANCELLED", "WRITTEN_OFF"})
    void terminalStates_cannotTransition(LoanState terminalState) {
        assertThat(stateMachine.isTerminalState(terminalState)).isTrue();
        for (LoanState target : LoanState.values()) {
            assertThat(stateMachine.isValidTransition(terminalState, target)).isFalse();
        }
    }

    @Test
    void transition_validTransition_returnsNewState() {
        LoanState result = stateMachine.transition(LoanState.OPEN, LoanState.CLOSED);
        assertThat(result).isEqualTo(LoanState.CLOSED);
    }

    @Test
    void transition_invalidTransition_throwsException() {
        assertThatThrownBy(() -> stateMachine.transition(LoanState.CLOSED, LoanState.OPEN))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("Invalid state transition");
    }
}
