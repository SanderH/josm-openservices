import java.lang.reflect.Method;

import org.junit.Test;

public class MethodTest {
    interface FooBar {
        Integer getX();
    }
    class Foo implements FooBar {
        private Integer x;

        @Override
        public Integer getX() {
            return x;
        }
    }

    class Bar extends Foo {
    }

    @SuppressWarnings("static-method")
    @Test
    public void methodTest() throws NoSuchMethodException, SecurityException {
        Method getFooBar = FooBar.class.getMethod("getX");
        Method getFoo = Foo.class.getMethod("getX");
        Method getBar = Bar.class.getMethod("getX");
        int i=0;
    }
}
