package org.openstreetmap.josm.plugins.ods.storage;

public class TestClasses {
    public static class Foo {
        private final Integer x;
        private final String s;

        public Foo(Integer x, String s) {
            super();
            this.x = x;
            this.s = s;
        }

        public Integer getX() {
            return x;
        }

        public String getS() {
            return s;
        }
    }

    public static class Bar extends Foo {

        public Bar(Integer x, String s) {
            super(x, s);
        }
    }
}
