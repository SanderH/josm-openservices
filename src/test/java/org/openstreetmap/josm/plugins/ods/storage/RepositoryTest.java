package org.openstreetmap.josm.plugins.ods.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.ATTR;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.EQUALS;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Foo;
import org.openstreetmap.josm.plugins.ods.storage.query.Query;

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
        Query<Object> query = repo.query();
        repo.run(query).stream().forEach(f->{set.add(f);});
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
        repo.query(Foo.class).forEach(f->{set.add(f);});
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
        repo.query(Object.class).forEach(f->{set.add(f);});
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
        Set<Foo> set = new IdentitySet<>();
        Query<Foo> query = repo.query(Foo.class, EQUALS(ATTR("s"), "test"));
        repo.run(query).stream().forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(foo));
        assertTrue(set.contains(bar));
    }
}
