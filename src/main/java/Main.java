
public class Main {

	public static void main(String[] args) {
		String str = "HearBeat#10.114.193.158";
		
		String[] split = str.split("#");
		
		System.out.println(split[1]);
	}
}
