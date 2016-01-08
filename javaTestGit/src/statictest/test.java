package statictest;

class Test {
	public static int X = 100;
	public final static int Y = 200;

	public Test() {
		System.out.println("Test构造函数执行");
	}

	static {
		System.out.println("static语句块执行");
	}

	public static void display() {
		System.out.println("静态方法被执行");
	}

	public void display_1() {
		System.out.println("实例方法被执行");
	}

public static void main(String args[]) throws ClassNotFoundException {
			Class.forName("statictest.Test");
			Class.forName("statictest.Test");
			Test test=new Test();
			Test test2=new Test();

		}
}
