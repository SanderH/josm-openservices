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
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.AbstractFoo;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Bar;
import org.openstreetmap.josm.plugins.ods.storage.TestClasses.Foo;
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
        repo.add(abstractFoo);
        Query<AbstractFoo> query = repo.query(AbstractFoo.class, TRUE);
        ResultSet<AbstractFoo> results = repo.run(query);
        Iterator<? extends AbstractFoo> allFoos = results.iterator();
        assertTrue(allFoos.hasNext());
        AbstractFoo f = allFoos.next();
        assertTrue(f == abstractFoo);
    }

    @Test
    public void testStoreAndRetreiveAll2() {
        repo.add(abstractFoo);
        repo.add(bar);
        Query<AbstractFoo> query = repo.query(AbstractFoo.class);
        ResultSet<AbstractFoo> results = repo.run(query);
        List<AbstractFoo> list = new LinkedList<>();
        results.iterator().forEachRemaining(f -> list.add(f));
        assert list.size() == 2;
        assert list.contains(abstractFoo);
        assert list.contains(bar);
    }

    @Test
    public void testStoreAndRetreiveAll3() {
        repo.add(abstractFoo);
        repo.add(bar);
        Query<AbstractFoo> query = repo.query(AbstractFoo.class, EQUALS("test", ATTR("s")));
        ResultSet<AbstractFoo> results = repo.run(query);
        List<AbstractFoo> list = new LinkedList<>();
        results.iterator().forEachRemaining(f -> list.add(f));
        assert list.size() == 2;
        assert list.contains(abstractFoo);
        assert list.contains(bar);
    }

    @Test
    public void testStoreAndRetreiveByKey() {
        repo.add(abstractFoo);
        Query<AbstractFoo> query = repo.query(AbstractFoo.class, EQUALS(7, ATTR("x")));
        ResultSet<AbstractFoo> results = repo.run(query);
        Iterator<? extends AbstractFoo> it = results.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.next() == abstractFoo);
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
