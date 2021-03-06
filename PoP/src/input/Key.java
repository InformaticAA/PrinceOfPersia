package input;

public class Key {
	
	public static final String UP = "UP";
	public static final String DOWN = "DOWN";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String ENTER = "ENTER";
	public static final String SHIFT = "SHIFT";
	public static final String ESCAPE = "ESCAPE";
	public static final String CONTROL = "CONTROL";
	public static final String W = "W";
	public static final String A = "A";
	public static final String S = "S";
	public static final String D = "D";
	public static final String M = "M";
	public static final String C = "C";
	public static final String R = "R";
	public static final String SPACE = "SPACE";
	
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
