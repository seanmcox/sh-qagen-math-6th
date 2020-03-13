package com.shtick.apps.sh.qagen.math.sixth.bundle;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.shtick.apps.sh.core.SubjectQuestionGenerator;
import com.shtick.apps.sh.qagen.math.sixth.SixthGradeMathQuestionGenerator;

/**
 **/
public class Activator implements BundleActivator {
	private ServiceRegistration<?> subjectRegistration;
	
    /**
     * Implements BundleActivator.start(). Prints
     * a message and adds itself to the bundle context as a service
     * listener.
     * @param context the framework context for the bundle.
     **/
    @Override
	public void start(BundleContext context){
		System.out.println(this.getClass().getCanonicalName()+": Starting.");
		subjectRegistration=context.registerService(SubjectQuestionGenerator.class.getName(), new SixthGradeMathQuestionGenerator(),new Hashtable<String, String>());
    }

    /**
     * Implements BundleActivator.stop(). Prints
     * a message and removes itself from the bundle context as a
     * service listener.
     * @param context the framework context for the bundle.
     **/
    @Override
	public void stop(BundleContext context){
		System.out.println(this.getClass().getCanonicalName()+": Stopping.");
		if(subjectRegistration!=null)
			subjectRegistration.unregister();
    }

}