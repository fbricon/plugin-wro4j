/**
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wro4j.forge;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import static org.junit.Assert.*;

public class Wro4JPluginTest extends AbstractShellTest
{
   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment().addPackages(true, Wro4jPlugin.class.getPackage());
   }

   @Test
   public void testInstallWro4j() throws Exception
   {
      // Create a new barebones Java project
      initializeJavaProject();
      
      // Queue input lines to be read as the Shell executes.
      queueInputLines("y");
 
      // Execute a command. If any input is required, it will be read from queued input.
      getShell().execute("setup "+Aliases.wro4j);
 
      Project project = getProject();
      
      //Check the Wro4j facet was added
      assertNotNull(project.getFacet(Wro4jFacet.class));
      
      WebResourceFacet webFacet = project.getFacet(WebResourceFacet.class);
      
      //Check the Wro4j descriptor was added
      FileResource<?> wroXml = webFacet.getWebResource("WEB-INF/wro.xml");
      assertTrue(wroXml.exists());

      //Check the Wro4j properties file was added
      FileResource<?> wroProperties = webFacet.getWebResource("WEB-INF/wro.properties");
      assertTrue(wroProperties.exists());
   }

}
