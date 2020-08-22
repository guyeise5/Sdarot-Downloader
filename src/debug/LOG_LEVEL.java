package debug;

public enum LOG_LEVEL {
	TRACE(0),
	DEBUG(1),
	INFORMATION(2),
	WARNING(3),
	ERROR(4),
	CRITICAL(5),
	NONE(6);
	
	private final int value;
	
	LOG_LEVEL(final int value){
		this.value = value;
	}
	
	public int getValue() { 
		return this.value;
	}

}
