package core.components;

public abstract class State {

	private int g;
	private int h;
	
	private boolean searched;
	private boolean onPath;
	private boolean isStart;
	private boolean isGoal;
	
	public State() {
		g = Integer.MAX_VALUE;
		h = 0;
		
		searched = false;
		onPath = false;
		isStart = false;
		isGoal = false;
	}
	
	public boolean isSearched() {
		return searched;
	}
	public void setSearched(boolean searched) {
		this.searched = searched;
	}
	public boolean isOnPath() {
		return onPath;
	}
	public void setOnPath(boolean onPath) {
		this.onPath = onPath;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public boolean isGoal() {
		return isGoal;
	}
	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getF() {
		return g + h;
	}
	
	/**
	 * Generic equality test for two states. Should be 
	 * over riden if used with a state with additional details.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof State)) {
			return false;
		}
		State otherState = (State)obj;
		
		return this == otherState;
	}
	
	/**
	 * To handle printing once a solution is found, reports
	 * information about the state.
	 */
	public abstract String toString();
	
}
