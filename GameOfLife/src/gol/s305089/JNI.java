package gol.s305089;

/**
 * @author John Kasper
 */
public class JNI {
    public static native int test();

    public static native int test1(int a);

    //public static native void nextGen(Board activeBoard);
    public void out(){
        System.out.println("hello");
    }
    
    public static void main(String[] args) {
        String filename = "C:\\Users\\JohnKasper\\Google Drive\\HIOA 15-16\\DATS1600 - programutvikling\\Kode\\GameOfLifeJNI\\dist\\Debug\\Cygwin-Windows\\libGameOfLifeJNI.dll";
        System.load(filename);
        System.out.println(test1(5));
        System.out.println(test());
    }
}
