/**
 * Copyright 2012 the original author or authors.
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

import static org.wro4j.forge.Aliases.wro4j;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 *
 */
@Alias(wro4j)
@RequiresFacet(Wro4jFacet.class)
public class Wro4jPlugin implements Plugin
{
	   @Inject
	   private Event<InstallFacets> event;
	 
	   @Inject
	   private Project project;
	 
	   @SetupCommand
	   public void setup(PipeOut out) {
	       if (!project.hasFacet(Wro4jFacet.class)) {
	    	   event.fire(new InstallFacets(Wro4jFacet.class));    	   
	       }
	       else
	           ShellMessages.info(out, wro4j +" is installed.");
	   };
}
