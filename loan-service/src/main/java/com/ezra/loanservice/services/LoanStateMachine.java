package com.ezra.loanservice.services;

import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class LoanStateMachine {
    private static final Map<LoanState, Set<LoanState>> VALID_TRANSITIONS = Map.of(
            LoanState.OPEN, Set.of(LoanState.CLOSED, LoanState.CANCELLED, LoanState.OVERDUE),
            LoanState.OVERDUE, Set.of(LoanState.CLOSED, LoanState.WRITTEN_OFF),
            LoanState.CLOSED, Set.of(),
            LoanState.CANCELLED, Set.of(),
            LoanState.WRITTEN_OFF, Set.of()
    );

    public boolean isValidTransition(LoanState currentState, LoanState newState) {
        Set<LoanState> validTransitions = VALID_TRANSITIONS.get(currentState);
        return validTransitions != null && validTransitions.contains(newState);
    }

    public boolean isTerminalState(LoanState state) {
        Set<LoanState> validTargets = VALID_TRANSITIONS.get(state);
        return validTargets != null && validTargets.isEmpty();
    }

    public LoanState transition(LoanState currentState, LoanState newState) {
        if (!isValidTransition(currentState, newState)) {
            throw new InvalidStateTransitionException("Invalid state transition from " + currentState + " to " + newState);
        }
        return newState;
    }
}
