package de.zalando.antlr;


public class CompilerApplication {

    public static void main(String[] args) throws Exception {

        String inputText = "<html id=\"{{aaaa}}\">Hello world</html>";
        String inputText2 = "<html id=\"{{aaaa}}\"><body>{{hello}}</body>\n</html>";
        String inputText3 = "ala bala {{aaaa}}";
        String inputText4 = "ala bala {{aaaa}}";
        String inputText5 = "ala bala {{aaaa}}";
        if (args != null && args.length > 0) {
            inputText = args[0];
        }

        TheBeard beard = new TheBeard();
        System.out.println(beard.parse(inputText));
        System.out.println(beard.parse(inputText2));
        System.out.println(beard.parse(inputText3));
        System.out.println(beard.parse(inputText4));
        System.out.println(beard.parse(inputText5));
    }
}