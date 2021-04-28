package edu.wpi.cs3733.D21.teamE.algorithms.pathfinding.constraints;


import edu.wpi.cs3733.D21.teamE.algorithms.Node;

/**
 * A* Search Implementation
 * Contains specific implementation of A*
 * Required for use of A* as pathfinding solution
 */
public class VanillaSearch implements SearchConstraint {

    @Override
    public boolean isExcluded(Node node) {
        return false;
    }
}