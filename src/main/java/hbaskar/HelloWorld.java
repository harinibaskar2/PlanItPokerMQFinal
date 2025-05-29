public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        // Example: print all command line arguments
        System.out.println("You passed these arguments:");
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}
