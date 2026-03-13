package com.ezra.loanservice.services;

import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.exceptions.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Finite state machine governing loan lifecycle transitions.
 *
 * <p>Valid state transitions:
 * <ul>
 *   <li>OPEN → CLOSED (fully paid), CANCELLED (manually), OVERDUE (past due date)</li>
 *   <li>OVERDUE → CLOSED (paid after overdue), WRITTEN_OFF (unrecoverable)</li>
 *   <li>CLOSED, CANCELLED, WRITTEN_OFF → terminal states, no further transitions</li>
 * </ul>
 */
@Component
public class LoanStateMachine {

    /** Maps each loan state to its set of valid target states. Terminal states map to empty sets. */
    private static final Map<LoanState, Set<LoanState>> VALID_TRANSITIONS = Map.of(
            LoanState.OPEN, Set.of(LoanState.CLOSED, LoanState.CANCELLED, LoanState.OVERDUE),
            LoanState.OVERDUE, Set.of(LoanState.CLOSED, LoanState.WRITTEN_OFF),
            LoanState.CLOSED, Set.of(),
            LoanState.CANCELLED, Set.of(),
            LoanState.WRITTEN_OFF, Set.of()
    );

    /** Checks whether transitioning from currentState to newState is allowed. */
    public boolean isValidTransition(LoanState currentState, LoanState newState) {
        Set<LoanState> validTransitions = VALID_TRANSITIONS.get(currentState);
        return validTransitions != null && validTransitions.contains(newState);
    }

    /** Returns true if the state is terminal (no further transitions possible). */
    public boolean isTerminalState(LoanState state) {
        Set<LoanState> validTargets = VALID_TRANSITIONS.get(state);
        return validTargets != null && validTargets.isEmpty();
    }

    /** Performs a state transition, throwing InvalidStateTransitionException if not allowed. */
    public LoanState transition(LoanState currentState, LoanState newState) {
        if (!isValidTransition(currentState, newState)) {
            throw new InvalidStateTransitionException("Invalid state transition from " + currentState + " to " + newState);
        }
        return newState;
    }
}
