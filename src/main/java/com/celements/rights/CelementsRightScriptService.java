/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.celements.rights;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.script.service.ScriptService;

import com.celements.rights.CelementsRightServiceImpl.PubUnpub;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.user.api.XWikiRightService;

@Component("celementsright")
public class CelementsRightScriptService implements ScriptService {
  
  private static Log LOGGER = LogFactory.getFactory().getInstance(
      CelementsRightScriptService.class);
  
  @Requirement
  Execution execution;
  
  @Requirement("local")
  EntityReferenceSerializer<String> serializer_local;
  
  public boolean publicationActivated(DocumentReference forDoc) {
    XWikiRightService rightService = getContext().getWiki().getRightService();
    if(rightService instanceof CelementsRightServiceImpl) {
      return ((CelementsRightServiceImpl)rightService).isPublishActive(forDoc, 
          getContext());
    } else {
      LOGGER.warn("Needs CelementsRightServiceImpl for publish / unpublish to work");
      return false;
    }
  }
  
  public boolean hasAccessLevelPublished(String right, String username, 
      DocumentReference docname) {
    return hasAccessLevel(right, username, serializer_local.serialize(docname), 
        CelementsRightServiceImpl.PubUnpub.PUBLISHED);
  }
  
  public boolean hasAccessLevelUnpublished(String right, String username, 
      DocumentReference docname) {
    return hasAccessLevel(right, username, serializer_local.serialize(docname), 
        CelementsRightServiceImpl.PubUnpub.UNPUBLISHED);
  }
  
  boolean hasAccessLevel(String right, String username, String docname, 
      PubUnpub unpublished) {
    XWikiRightService rightService = getContext().getWiki().getRightService();
    if(rightService instanceof CelementsRightServiceImpl) {
      getContext().put("overridePubCheck", unpublished);
    } else {
      LOGGER.warn("Needs CelementsRightServiceImpl for publish / unpublish to work");
    }
    try {
      return rightService.hasAccessLevel(right, username, docname, getContext());
    } catch (XWikiException xwe) {
      LOGGER.error("hasAccessLevelPublished: Exception while checking access level for " +
          "right=" + right + ", username=" + username + ", docname=" + docname, xwe);
      return false;
    }
  }

  private XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }
}
