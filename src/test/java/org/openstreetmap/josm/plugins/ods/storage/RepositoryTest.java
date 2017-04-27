package org.openstreetmap.josm.plugins.ods.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Foo;

public class RepositoryTest {
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
    public void testStoreAndRetrieveByIdentity() {
        repo.add(foo);
        Foo f =repo.getByPrimary(Foo.class, foo);
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetrieveByKey() {
        repo.register(Foo.class, "x");
        repo.add(foo);
        Foo f = repo.getByPrimary(Foo.class, 7);
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetrieveSubClassByKey() {
        repo.register(Foo.class, "x");
        repo.add(bar);
        Bar b = repo.getByPrimary(Bar.class, 45);
        assertTrue(b == bar);
    }

    @Test
    public void testRetrieveAll() {
        repo.register(Foo.class, "x");
        repo.add(foo);
        repo.add(bar);
        Set<Object> set = new IdentitySet<>();
        repo.getAll().forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));
    }

    @Test
    public void testRetrieveAllByType() {
        repo.register(Foo.class, "x");
        repo.add(foo);
        repo.add(bar);
        Set<Foo> set = new IdentitySet<>();
        repo.getAll(Foo.class).forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));
    }

    @Test
    public void testRetrieveAllBySupertype() {
        repo.register(Foo.class, "x");
        repo.add(foo);
        repo.add(bar);
        Set<Object> set = new IdentitySet<>();
        repo.getAll(Object.class).forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));
    }

    @Test
    public void testRetrieveAllByQuery() {
        repo.register(Foo.class, "x");
        repo.addIndex(Foo.class, "s");
        repo.add(foo);
        repo.add(bar);
        Set<Object> set = new IdentitySet<>();
        repo.query(Foo.class, "s", "test").forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));
    }
}
