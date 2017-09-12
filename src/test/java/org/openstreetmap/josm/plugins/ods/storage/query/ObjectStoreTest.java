package org.openstreetmap.josm.plugins.ods.storage.query;

import static org.junit.Assert.assertTrue;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.ATTR;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.EQUALS;
import static org.openstreetmap.josm.plugins.ods.storage.query.Query.TRUE;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.josm.plugins.ods.storage.Repository;
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
        repo.add(foo);
        Query<Foo> query = repo.query(Foo.class, TRUE);
        ResultSet<Foo> results = repo.run(query);
        Iterator<? extends Foo> allFoos = results.iterator();
        assertTrue(allFoos.hasNext());
        Foo f = allFoos.next();
        assertTrue(f == foo);
    }

    @Test
    public void testStoreAndRetreiveAll2() {
        repo.add(foo);
        repo.add(bar);
        Query<Foo> query = repo.query(Foo.class);
        ResultSet<Foo> results = repo.run(query);
        List<Foo> list = new LinkedList<>();
        results.iterator().forEachRemaining(f -> list.add(f));
        assert list.size() == 2;
        assert list.contains(foo);
        assert list.contains(bar);
    }

    @Test
    public void testStoreAndRetreiveAll3() {
        repo.add(foo);
        repo.add(bar);
        Query<Foo> query = repo.query(Foo.class, EQUALS("test", ATTR("s")));
        ResultSet<Foo> results = repo.run(query);
        List<Foo> list = new LinkedList<>();
        results.iterator().forEachRemaining(f -> list.add(f));
        assert list.size() == 2;
        assert list.contains(foo);
        assert list.contains(bar);
    }

    @Test
    public void testStoreAndRetreiveByKey() {
        repo.add(foo);
        Query<Foo> query = repo.query(Foo.class, EQUALS(7, ATTR("x")));
        ResultSet<Foo> results = repo.run(query);
        Iterator<? extends Foo> it = results.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.next() == foo);
        assertTrue(!it.hasNext());
    }

    @Test
    public void testStoreAndRetreiveSubClassByKey() {
        repo.add(bar);
        Query<Foo> query = repo.query(Foo.class, EQUALS(45, ATTR("x")));
        ResultSet<Foo> results = repo.run(query);
        Iterator<? extends Foo> it = results.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.next() == bar);
        assertTrue(!it.hasNext());
    }
}
