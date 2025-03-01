package com.lartimes.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/3/1 23:23
 */
public class BundleB implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        System.out.println(this.getClass().getSimpleName() +"start");
        new OsgiBundle();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println(this.getClass().getSimpleName() + "stop" );
    }
}
