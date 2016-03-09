package com.steeplesoft.frenchpress.test;

import java.io.File;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Created by jdlee on 9/13/13.
 */
public class AbstractServiceTestBase {
    @PersistenceContext
    protected EntityManager em;
    @Inject
    protected UserTransaction utx;

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "com.steeplesoft.frenchpress")
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsResource(new FileAsset(new File("src/main/webapp/images/okcJugLogo1.png")), "image.png")
                .addAsWebInfResource(new FileAsset(new File("src/main/webapp/WEB-INF/web.xml")), "web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    protected static int generateRandomNumber() {
        Random r = new Random();
        return Math.abs(r.nextInt()) + 1;
    }

    @Before
    public void preparePersistenceTest() throws Exception {
        utx.begin();
        em.joinTransaction();
/*    
        ContainerConfiguration configuration = new OpenShiftContainerConfiguration();
        ProtocolMetaDataParser parser = new ProtocolMetaDataParser(configuration);
        ProtocolMetaData data = parser.parse(sampleEar());

        HTTPContext context = data.getContext(HTTPContext.class);
        Assert.assertNotNull(context.getServletByName("Servlet1"));

        String contextRoot = context.getServletByName("Servlet1").getContextRoot();
        Assert.assertEquals("Context root of arquillian1.war is set correctly", "/arquillian1", contextRoot);
        */        
    }

    @After
    public void commitTransaction() throws Exception {
        utx.commit();
    }
}
