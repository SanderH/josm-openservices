package org.openstreetmap.josm.plugins.ods.storage;

public class TestClasses {
    public static interface Foo {
        public Integer getX();
        public String getS();
    }

    public abstract static class AbstractFoo implements Foo {
        private final Integer x;
        private final String s;

        public AbstractFoo(Integer x, String s) {
            super();
            this.x = x;
            this.s = s;
        }

        @Override
        public Integer getX() {
            return x;
        }

        @Override
        public String getS() {
            return s;
        }
    }

    public static class FooFoo extends AbstractFoo {

        public FooFoo(Integer x, String s) {
            super(x, s);
        }
    }

    public static class Bar implements Foo {
        private final Integer x;
        private final String s;

        public Bar(Integer x, String s) {
            super();
            this.x = x;
            this.s = s;
        }

        @Override
        public Integer getX() {
            return x;
        }

        @Override
        public String getS() {
            return s;
        }
    }
}
