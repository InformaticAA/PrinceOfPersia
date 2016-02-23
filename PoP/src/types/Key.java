package types;

import java.awt.event.KeyEvent;

public class Key {
	
	private boolean pressed;
	private KeyEvent event;
	
	public Key(boolean pressed, KeyEvent event){
		this.pressed = pressed;
		this.event = event;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public KeyEvent getEvent() {
		return event;
	}

	public void setEvent(KeyEvent event) {
		this.event = event;
	}
}
