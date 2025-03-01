package com.lartimes.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Dictionary;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/3/1 23:27
 */
public class BundleClient implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        ServiceReference<OsgiBundle> serviceReference = bundleContext.getServiceReference(OsgiBundle.class);
        Dictionary<String, Object> properties = serviceReference.getProperties();
        System.out.println(properties);

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
