package tests;

public class RandomStuffTest {
	
	public static void main(String[] args) {
		
		int x = 460;
		int y = 260;
		int[] square = pixelToSquare(x, y);
		
		System.out.printf("Square: ");
		for (int i = 0; i < square.length; i++) {
			System.out.printf(square[i] + ", ");
		}
		
		int[] pixels = squareToPixel(square[0], square[1]);
		
		System.out.println();
		System.out.printf("Pixels: ");
		for (int i = 0; i < pixels.length; i++) {
			System.out.printf(pixels[i] + ", ");
		}
	}
	
	private static int[] pixelToSquare(int xx, int yy) {
		int i = yy - 6;
		
		if (i <= 0) i = 0;
		else i = (i / 126) + 1;
		
		int j = xx / 64;
		return new int[]{i, j};
	}
	
	private static int[] squareToPixel(int x, int y) {
		int px = 64 + y * 64;
		int py = (int)(6 + x * 126);
		
		return new int[]{px, py};
	}

}
