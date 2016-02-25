package input;

public class Key {
	
	public static final String UP = "UP";
	public static final String DOWN = "DOWN";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String ENTER = "ENTER";
	
	private boolean pressed;
	private int keycode;
	
	public Key(boolean pressed, int keycode){
		this.pressed = pressed;
		this.keycode = keycode;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public int getKeycode() {
		return keycode;
	}

	public void setKeycode(int keycode) {
		this.keycode = keycode;
	}
}
