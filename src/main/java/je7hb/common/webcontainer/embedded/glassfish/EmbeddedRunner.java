package je7hb.common.webcontainer.embedded.glassfish;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.glassfish.embeddable.BootstrapProperties;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

public class EmbeddedRunner {

 private int port;
 private AtomicBoolean initialized = new AtomicBoolean();
 private GlassFish glassfish;

 public EmbeddedRunner(int port) {
   this.port = port;
 }

 public EmbeddedRunner init() throws Exception{
   if ( initialized.get() ) {
     throw new RuntimeException("runner was already initialized");
   }
   BootstrapProperties bootstrapProperties = new BootstrapProperties();
   GlassFishRuntime glassfishRuntime = GlassFishRuntime.bootstrap(bootstrapProperties);

   GlassFishProperties glassfishProperties = new GlassFishProperties();
   glassfishProperties.setPort("http-listener", port);
   String [] paths = System.getProperty("java.class.path").split(File.pathSeparator);
   for (int j=0; j<paths.length; ++j) {
     System.out.printf("classpath[%d] = %s\n", j, paths[j]);
   }
   glassfish = glassfishRuntime.newGlassFish(glassfishProperties);
   initialized.set(true);
   return this;
 }

  private void check() {
    if ( !initialized.get() ) {
      throw new RuntimeException("runner was not initialised");
    }
  }

  public EmbeddedRunner start() throws Exception{
    check();
    glassfish.start();
    return this;
  }

  public EmbeddedRunner stop() throws Exception{
    check();
    glassfish.stop();
    return this;
  }

  public static void main(String args[]) throws Exception {
    EmbeddedRunner runner = new EmbeddedRunner(8080).init().start();
    Thread.sleep(1000);
    runner.stop();
  }
}
