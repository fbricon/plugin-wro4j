package org.wro4j.forge;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.RequiresFacet;

@RequiresFacet({WebResourceFacet.class, MavenPluginFacet.class})
public class Wro4jFacet extends BaseFacet {

	private static final String WRO_PROPERTIES = "wro.properties";

	private static final String WRO_XML = "wro.xml";

	private static final String WRO_XML_CONTENTS = "<groups xmlns=\"http://www.isdc.ro/wro\"" +
													" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
													" xsi:schemaLocation=\"http://www.isdc.ro/wro wro.xsd\">\n"+
													" <group name=\"application\">\n"+
													"	<css>/*.css</css>\n"+
													"	<js>/*.js</js>\n"+
												    " </group>\n"+
												    "</groups>";
	
	private static final String WRO_PROPERTIES_CONTENTS = "preProcessors=cssImport,semicolonAppender\n"+	
			"postProcessors=lessCss,cssMinJawr,jsMin";
	
	
	public boolean install() {
		if (isInstalled()) {
			return true;
		}
		//Add ro.isdc.wro4j:wro4j-maven-plugin to the pom, if necessary
		installWro4jPlugin();
		
		//Add WEB-INF/wro.xml to src/main/webapp, if necessary
		setContents(getWro4jConfig(WRO_XML), WRO_XML_CONTENTS);
		
		//Add WEB-INF/wro.properties to src/main/webapp, if necessary
		setContents(getWro4jConfig(WRO_PROPERTIES), WRO_PROPERTIES_CONTENTS);
		
		return true;
	}

	private void installWro4jPlugin() {

         MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
         DependencyBuilder wro4jPluginDep = buildWro4jPlugin();

         if (plugins.hasPlugin(wro4jPluginDep)) {
            return;
         }
         
         MavenPlugin plugin = MavenPluginBuilder.create().setDependency(wro4jPluginDep);
         
         Execution e = ExecutionBuilder.create()
                                	   .setPhase("compile")
        		                       .addGoal("run");
         plugin.listExecutions().add(e);
         //configure(plugin, "minimize", true);
         configure(plugin, "destinationFolder", "${project.build.directory}/${project.build.finalName}/resources");
         configure(plugin, "targetGroups", "application");

         plugins.addPlugin(plugin);
    }

	private void configure(MavenPlugin plugin, String configurationKey, Object value) {
		plugin.getConfig().addConfigurationElement(
             ConfigurationElementBuilder.create()
                                        .setName(configurationKey)
                                        .setText(value.toString())
         );
	}


	private void setContents(FileResource<?> resource, String contents) {
		if (resource != null) {
			if (!resource.exists()) {
				if (!resource.createNewFile()) {
					//TODO log something
					return;
				}
				resource.setContents(contents);
			}
		}
	}
	
	private boolean hasWro4jConfig(String fileName) {
		FileResource<?> wro = getWro4jConfig(fileName);
		return wro != null && wro.exists();
	}

	
	private FileResource<?> getWro4jConfig(String fileName) {
		WebResourceFacet webFacet = project.getFacet(WebResourceFacet.class);
		return (webFacet == null)?null:webFacet.getWebResource("WEB-INF/"+fileName);
	}
	
	public boolean isInstalled() {
		MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
        DependencyBuilder wro4jPluginDep = buildWro4jPlugin();
		return plugins.hasPlugin(wro4jPluginDep) 
			&& hasWro4jConfig(WRO_XML)
			&& hasWro4jConfig(WRO_PROPERTIES)
			;
	}

	private DependencyBuilder buildWro4jPlugin() {
		return DependencyBuilder.create("ro.isdc.wro4j:wro4j-maven-plugin")
             					.setVersion("1.4.6");
	}

}
