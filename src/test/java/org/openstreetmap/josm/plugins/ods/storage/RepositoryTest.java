package org.openstreetmap.josm.plugins.ods.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.ATTR;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.EQUALS;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.AbstractFoo;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Foo;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.FooFoo;
import org.openstreetmap.josm.plugins.ods.storage.query.Query;

public class RepositoryTest {
    private Repository repo;
    private AbstractFoo fooFoo;
    private Bar bar;
    private Bar bar2;

    @Before
    public void setup() {
        repo = new Repository();
        fooFoo = new FooFoo(7, "test");
        bar = new Bar(45, "test");
        bar2 = new Bar(45, "test");
    }

    @Test
    public void testStoreAndRetrieveByKey() {
        repo.add(fooFoo);
        Iterator<? extends FooFoo> it = repo.query(FooFoo.class, EQUALS(ATTR("x"), 7)).iterator();
        assertTrue(it.next() == fooFoo);
    }

    @Test
    public void testStoreAndRetrieveSubClassByKey() {
        repo.add(bar);
        List<? extends Bar> it = repo.query(Bar.class, EQUALS(ATTR("x"), 45)).toList();
        assertTrue(it.contains(bar));
    }

    @Test
    public void testRetrieveAll() {
        repo.add(fooFoo);
        repo.add(bar);
        repo.add(bar2);
        Query<Object> query = repo.query();
        Collection<? extends Object> result = repo.run(query).toSet();
        assertEquals(3, result.size());
        assertTrue(result.contains(fooFoo));
        assertTrue(result.contains(bar));
        assertTrue(result.contains(bar2));
    }

    @Test
    public void testRetrieveAllByType() {
        //        repo.register(Foo.class, "x");
        repo.add(fooFoo);
        repo.add(bar);
        Set<? extends Foo> set = repo.query(Foo.class).toSet();
        assertEquals(2, set.size());
        assertTrue(set.contains(fooFoo));
        assertTrue(set.contains(bar));
    }

    @Test
    public void testRetrieveAllBySupertype() {
        repo.add(fooFoo);
        repo.add(bar);
        Set<Object> set = new IdentitySet<>();
        repo.query(Object.class).forEach(f->{set.add(f);});
        assertEquals(2, set.size());
        assertTrue(set.contains(fooFoo));
        assertTrue(set.contains(bar));
    }

    @Test
    public void testRetrieveAllByQuery() {
        repo.add(fooFoo);
        repo.add(bar);
        Set<? extends Foo> set = repo.query(Foo.class, EQUALS(ATTR("s"), "test")).toSet();
        assertEquals(2, set.size());
        assertTrue(set.contains(fooFoo));
        assertTrue(set.contains(bar));
    }
}
