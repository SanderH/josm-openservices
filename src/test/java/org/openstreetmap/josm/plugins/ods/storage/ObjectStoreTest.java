package org.openstreetmap.josm.plugins.ods.storage;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.AbstractFoo;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.FooFoo;

public class ObjectStoreTest {
    private Repository repo;
    private AbstractFoo abstractFoo;
    private Bar bar;

    @Before
    public void setup() {
        repo = new Repository();
        abstractFoo = new FooFoo(7, "test");
        bar = new Bar(45, "test");
    }

    @Test
    public void testStoreAndRetreiveAll() {
        ObjectStore<AbstractFoo> store = new ObjectStore<>(repo, AbstractFoo.class);
        store.add(abstractFoo);
        Iterator<? extends AbstractFoo> allFoos = store.stream().iterator();
        assertTrue(allFoos.hasNext());
        AbstractFoo f = allFoos.next();
        assertTrue(f == abstractFoo);
    }

    @Test
    public void testStoreAndRetreiveByIdentity() {
        ObjectStore<AbstractFoo> store = new ObjectStore<>(repo, AbstractFoo.class);
        store.add(abstractFoo);
        AbstractFoo f = store.getByPrimary(abstractFoo);
        assertTrue(f == abstractFoo);
    }

    @Test
    public void testStoreAndRetreiveByKey() {
        ObjectStore<AbstractFoo> store = new ObjectStore<>(repo, AbstractFoo.class, "x");
        store.add(abstractFoo);
        AbstractFoo f = store.getByPrimary(7);
        assertTrue(f == abstractFoo);
    }
}
