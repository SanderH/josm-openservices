package org.openstreetmap.josm.plugins.ods.storage;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Foo;

public class ObjectStoreTest {
    private Repository repo;
    private Foo foo;
    private Bar bar;

    @Before
    public void setup() {
        repo = new Repository();
        foo = new Foo(7, "test");
        bar = new Bar(45, "test");
    }

    @Test
    public void testStoreAndRetreiveAll() {
        ObjectStore<Foo> store = new ObjectStore<>(repo, Foo.class);
        store.add(foo);
        Iterator<? extends Foo> allFoos = store.getAll().iterator();
        assertTrue(allFoos.hasNext());
        Foo f = allFoos.next();
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetreiveByIdentity() {
        ObjectStore<Foo> store = new ObjectStore<>(repo, Foo.class);
        store.add(foo);
        Foo f = store.getByPrimary(foo);
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetreiveByKey() {
        ObjectStore<Foo> store = new ObjectStore<>(repo, Foo.class, "x");
        store.add(foo);
        Foo f = store.getByPrimary(7);
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetreiveSubClassByKey() {
        ObjectStore<Foo> fooStore = new ObjectStore<>(repo, Foo.class, "x");
        fooStore.add(bar);
        Foo f = fooStore.getByPrimary(45);
        assertTrue(f == bar);
    }
}
