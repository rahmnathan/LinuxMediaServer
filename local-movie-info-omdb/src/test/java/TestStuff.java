import org.junit.Assert;
import org.junit.Test;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class TestStuff {

    @Test
    public void test(){
        Deque<Character> characters = new LinkedBlockingDeque<>();
        for(char character : "({{{]}lkj".toCharArray()){
            characters.add(character);
        }

        int x = characters.size()/2;
        for(int i = 0; i < x; i++){
            char first = characters.removeFirst();
            char last = characters.removeLast();
            System.out.println(first + " " + last);
            if(getOpposite(first) != last) {
                System.out.println("Not Balanced");
                Assert.fail();
                break;
            }
        }
    }

    private char getOpposite(char character){
        switch (character){
            case '{':
                return '}';
            case '}':
                return '{';
            case '[':
                return ']';
            case ']':
                return '[';
            case '(':
                return ')';
            case ')':
                return '(';
        }

        return '.';
    }
}
